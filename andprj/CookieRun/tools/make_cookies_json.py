#!/usr/bin/env python3

import json
from pathlib import Path


ROOT_2026 = Path(__file__).resolve().parents[1]
TOOLS_SOURCE = ROOT_2026 / "tools" / "source"

SOURCE_COOKIES = TOOLS_SOURCE / "w08" / "res" / "cookies.json"
SOURCE_TYPES = TOOLS_SOURCE / "w08" / "res" / "cookie_types.json"
SOURCE_OVERRIDES = TOOLS_SOURCE / "andprj_2025" / "app" / "src" / "main" / "assets" / "cookies.json"
TARGET_COOKIES = ROOT_2026 / "app" / "src" / "main" / "assets" / "cookies.json"

# 2024 state order:
#   RUNNING, JUMP, DOUBLE_JUMP, SLIDE, FALLING, HURT
# 2026 state order:
#   RUN, JUMP, FALL, DOUBLE_JUMP, SLIDE, HURT
STATE_ORDER_2026_FROM_2024 = [0, 1, 4, 2, 3, 5]

BRAVE_COOKIE_ID = 107566
EXPECTED_BRAVE_STATE_RECTS = [
    [[2, 274, 272, 544], [274, 274, 544, 544], [546, 274, 816, 544], [818, 274, 1088, 544]],
    [[1906, 2, 2176, 272], [2178, 2, 2448, 272]],
    [[2, 2, 272, 272]],
    [[274, 2, 544, 272], [546, 2, 816, 272], [818, 2, 1088, 272], [1090, 2, 1360, 272]],
    [[2450, 2, 2720, 272], [2722, 2, 2992, 272]],
    [[818, 1362, 1088, 1632], [1090, 1362, 1360, 1632]],
]


def load_json(path: Path):
    with path.open() as f:
        return json.load(f)


def type_rows(type_name: str) -> int:
    # Examples: "11x6", "15x5-Fox", "15x5-Tiger"
    return int(type_name.split("x", maxsplit=1)[1].split("-", maxsplit=1)[0])


def convert_index(old_index: int, rows: int) -> int:
    old_row, col = divmod(old_index, 100)
    new_row = rows - 1 - old_row
    return new_row * 100 + col


def rect_for_index(index: int, cell_size: int) -> list[int]:
    row, col = divmod(index, 100)
    left = 2 + col * (cell_size + 2)
    top = 2 + row * (cell_size + 2)
    return [left, top, left + cell_size, top + cell_size]


def build_state_rects(cookie: dict, cookie_types: dict) -> list[list[list[int]]]:
    # 2024 player.py used types["11x6"] when a cookie type was missing.
    # The 2024 cookies list includes "15x6", but cookie_types.json does not define it.
    # Keep the same fallback so the generated data follows the old runtime behavior.
    type_name = cookie["type"]
    type_info = cookie_types.get(type_name, cookie_types["11x6"])
    rows = type_rows(type_name)
    cell_size = int(cookie["size"])

    state_rects = []
    for old_state_index in STATE_ORDER_2026_FROM_2024:
        old_rect_indices = type_info["states"][old_state_index]["rect"]
        android_indices = [convert_index(index, rows) for index in old_rect_indices]
        state_rects.append([rect_for_index(index, cell_size) for index in android_indices])
    return state_rects


def build_overrides() -> dict[int, dict]:
    overrides = {}
    for cookie in load_json(SOURCE_OVERRIDES):
        cookie_id = int(cookie["id"])
        override = {}
        if "jumpPower" in cookie:
            override["jumpPower"] = cookie["jumpPower"]
        if "scoreRate" in cookie:
            override["scoreRate"] = cookie["scoreRate"]
        overrides[cookie_id] = override
    return overrides


def format_rect(rect: list[int]) -> str:
    return json.dumps(rect)


def format_rects(rects: list[list[int]]) -> str:
    return "[" + ", ".join(format_rect(rect) for rect in rects) + "]"


def format_cookie(cookie: dict, indent: int = 2) -> str:
    space = " " * indent
    nested = " " * (indent + 2)
    lines = [
        f"{space}{{",
        f'{nested}"id": {cookie["id"]},',
        f'{nested}"name": {json.dumps(cookie["name"])},',
        f'{nested}"stateRects": [',
    ]
    for index, rects in enumerate(cookie["stateRects"]):
        comma = "," if index < len(cookie["stateRects"]) - 1 else ""
        lines.append(f"{nested}  {format_rects(rects)}{comma}")
    lines.append(f"{nested}]")

    if "jumpPower" in cookie:
        lines[-1] += ","
        lines.append(f'{nested}"jumpPower": {cookie["jumpPower"]}')
    if "scoreRate" in cookie:
        lines[-1] += ","
        lines.append(f'{nested}"scoreRate": {cookie["scoreRate"]}')

    lines.append(f"{space}}}")
    return "\n".join(lines)


def format_cookies(cookies: list[dict]) -> str:
    lines = ["["]
    for index, cookie in enumerate(cookies):
        suffix = "," if index < len(cookies) - 1 else ""
        lines.append(format_cookie(cookie) + suffix)
    lines.append("]")
    return "\n".join(lines) + "\n"


def main() -> None:
    source_cookies = load_json(SOURCE_COOKIES)
    cookie_types = load_json(SOURCE_TYPES)
    overrides = build_overrides()

    generated = []
    for source in source_cookies:
        cookie_id = int(source["id"])
        cookie = {
            "id": cookie_id,
            "name": source["name"],
            "stateRects": build_state_rects(source, cookie_types),
        }
        cookie.update(overrides.get(cookie_id, {}))
        generated.append(cookie)

    brave = next(cookie for cookie in generated if cookie["id"] == BRAVE_COOKIE_ID)
    if brave["stateRects"] != EXPECTED_BRAVE_STATE_RECTS:
        raise RuntimeError("Brave Cookie stateRects do not match Player.kt")

    TARGET_COOKIES.parent.mkdir(parents=True, exist_ok=True)
    with TARGET_COOKIES.open("w") as f:
        f.write(format_cookies(generated))

    print(f"Wrote {TARGET_COOKIES}")
    print(f"Generated {len(generated)} cookies")
    print("Validated Brave Cookie stateRects")


if __name__ == "__main__":
    main()
