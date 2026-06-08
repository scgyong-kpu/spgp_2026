import base64
import json
import urllib.request
from pathlib import Path
import re

# 이 script 는 tools/TapTu 폴더 밖에서 실행해도 같은 결과가 나와야 한다.
# 그래서 현재 작업 디렉터리(os.getcwd())가 아니라, 이 파일이 놓인 폴더를 기준으로
# songs.json 과 thumbnails/ 폴더 위치를 결정한다.
SCRIPT_DIR = Path(__file__).resolve().parent
SONGS_JSON = SCRIPT_DIR / "songs.json"
THUMBNAIL_DIR = SCRIPT_DIR / "thumbnails"


def sanitize_text(text):
    # 이 값은 파일명에는 직접 쓰지 않지만, 로그에 출력할 때 제어문자나 이상한 공백이
    # 섞여 있으면 원인 파악이 어려워진다. chart_grab.js 와 같은 의도로 NBSP 를 정리한다.
    return (text or "").replace("\u00a0", " ").strip()


def extension_from_data_url(data_url):
    # data:image/png;base64,... 또는 data:image/jpeg;base64,... 같은 형태를 처리한다.
    # VIBE 에서는 보통 URL 이 오지만, 브라우저가 base64 data URL 을 넣는 경우도 대비한다.
    header = data_url.split(",", 1)[0]
    if "image/png" in header:
        return "png"
    if "image/webp" in header:
        return "webp"
    return "jpg"


def save_base64_image(data_url, filepath):
    # data URL 은 앞쪽 header 와 뒤쪽 base64 payload 로 나뉜다.
    # 파일에는 payload 를 decode 한 실제 image byte 만 저장해야 한다.
    _header, base64_data = data_url.split(",", 1)
    image_data = base64.b64decode(base64_data)
    filepath.write_bytes(image_data)


def download_image(url, filepath):
    # urllib.request.urlretrieve(url, filepath) 로도 내려받을 수 있지만,
    # 일부 서버는 User-Agent 가 없는 요청을 막을 수 있다. 브라우저에서 온 요청처럼
    # 보이도록 User-Agent 를 명시해 둔다.
    request = urllib.request.Request(
        url,
        headers={
            "User-Agent": (
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/125.0 Safari/537.36"
            ),
        },
    )
    with urllib.request.urlopen(request) as response:
        filepath.write_bytes(response.read())


def load_songs():
    with SONGS_JSON.open("r", encoding="utf-8") as file:
        return json.load(file)


def thumbnail_filename(song, index):
    # 앱에서는 곡의 rank 로 cover_001.jpg 같은 파일을 찾는 구조가 가장 단순하다.
    # enumerate 순서로 파일명을 만들면 songs.json 정렬이 바뀌거나 일부 곡만 남겼을 때
    # rank 와 파일명이 어긋날 수 있으므로, rank 를 우선 사용한다.
    rank = song.get("rank", index)
    thumb = song.get("thumbnail", "")
    ext = extension_from_data_url(thumb) if thumb.startswith("data:image/") else "jpg"
    return f"cover_{rank:03d}.{ext}"


def main():
    THUMBNAIL_DIR.mkdir(exist_ok=True)
    songs = load_songs()

    for index, song in enumerate(songs, start=1):
        title = sanitize_text(song.get("title", f"song_{index}"))
        artist = sanitize_text(song.get("artist", "unknown"))
        thumb = song.get("thumbnail", "")

        if not thumb:
            print(f"[!] {title} - {artist}: thumbnail 정보 없음")
            continue

        thumb = re.sub(r'(.+\.jpg)(/melon/resize/.+)', r'\1', thumb)
        filename = thumbnail_filename(song, index)
        filepath = THUMBNAIL_DIR / filename

        try:
            if thumb.startswith("data:image/"):
                save_base64_image(thumb, filepath)
                print(f"[OK] Base64 image saved: {filename} - {title} / {artist}")
            else:
                download_image(thumb, filepath)
                print(f"[OK] URL image downloaded: {filename} - {title} / {artist}")

        except Exception as error:
            print(f"[ERR] {title} - {artist}: image save failed")
            print("      ", error)


if __name__ == "__main__":
    main()
