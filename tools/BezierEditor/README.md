# Bezier Curve Editor

Android `Path` 나 SVG path data 로 사용할 cubic Bezier 경로를 편집하는 간단한 HTML 도구이다.

## 사용 방법

1. `BezierCurveEditor.html` 파일을 브라우저에서 연다.
2. `Load BG Image` 로 경로를 맞출 배경 이미지를 불러온다.
3. `Width` 에 게임 좌표계 기준 가로 크기를 입력한다. TuDefence 는 `1600` 을 사용한다.
4. 점을 드래그해서 경로를 조정한다.
5. `Add Curve` / `Remove Curve` 로 cubic curve 구간을 추가하거나 제거한다.
6. `Java`, `Kotlin`, `SVG` 중 필요한 출력 형식을 고른다.
7. 아래 textarea 의 코드를 게임 코드에 붙여 넣는다.

## 입력 다시 읽기

textarea 에 기존 path 코드를 붙여 넣고 `Load from output` 을 누르면 편집점으로 다시 읽어 온다.

현재 지원하는 입력 형식은 다음과 같다.

- Java/Kotlin `Path` API 형식: `moveTo(...)`, `cubicTo(...)`
- SVG path 형식: `M`, `C`

SVG 는 `M 0,900` 처럼 명령과 좌표 사이에 공백이 있는 형식과 `M0,900` 처럼 붙어 있는 형식을 모두 읽는다.

## 출력 형식

- `Java`: `Path path = new Path();` 와 `path.moveTo(...)`, `path.cubicTo(...)` 형태로 출력한다.
- `Kotlin`: `val path = Path().apply { ... }` 형태로 출력한다.
- `SVG`: `PathParser.createPathFromPathData(...)` 에 넣기 좋은 `M` / `C` path data 로 출력한다.
