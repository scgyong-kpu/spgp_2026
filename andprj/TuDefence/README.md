# TuDefence

Android 2D game programming 수업에서 진행할 타워 디펜스 예제 프로젝트이다.

플레이어는 타일 맵 위에 포탑을 설치하고, 정해진 경로를 따라 이동하는 적을 막는다. 프로젝트는 Android 기본 Activity 에서 시작해 `a2dg` 기반 게임 구조, 타일 맵, 오브젝트 재활용, 충돌 처리, 웨이브 생성, 일시정지 화면까지 단계적으로 확장한다.

## 현재 상태

- [x] Android 프로젝트 생성
- [x] 기본 `MainActivity` 생성
- [x] 게임 Activity 분리
- [x] `a2dg` 모듈 연결
- [x] 게임 화면 좌표계 설정
- [x] Tiled map 배경 표시
- [x] 임시 enemy wave 생성

## Project Setup

- [x] 앱 패키지와 이름 정리
- [x] `buildFeatures` 에 `viewBinding` / `buildConfig` 활성화
- [x] `MainActivity` 를 시작 화면으로 사용
- [x] `MainGameActivity` 추가
- [x] `MainGameActivity` 는 layout xml 없이 게임 Activity 로 구성
- [x] Debug build 에서 게임 화면 자동 실행 지원
- [x] Landscape mode 고정
- [x] 게임 리소스 복사 및 정리

## a2dg Integration

- [x] `a2dg` library module 추가
- [x] `settings.gradle.kts` 에 `:a2dg` module 등록
- [x] `app` module 이 `a2dg` module 을 dependency 로 사용하도록 설정
- [x] `BaseGameActivity` 기반 게임 Activity 구성
- [x] `Scene` / `World` / `Layer` 구조 적용
- [x] 게임 좌표계를 1600 x 900 기준으로 설정
- [x] Debug grid / FPS / debug info 표시 확인

## Main Scene

- [x] 빈 Scene 을 먼저 push 하여 GameActivity 동작 확인
- [x] `MainScene` 생성
- [x] 현재 단계용 Layer enum 정의
  - [x] `BG`
  - [x] `ENEMY`
  - [x] `CONTROLLER`
- [x] 이후 단계용 Layer 확장
  - [x] `MainLayer` 에 정리.
- [x] `CAMERA_BEGIN` / `CAMERA_END` marker 로 map transform 적용 범위 실험
- [x] `MainWorld` 에서 camera 적용 layer 를 같은 draw scope 안에서 처리
- [x] `CameraBegin` / `CameraEnd` marker 객체 제거
- [x] Back key 로 `PauseScene` 진입
- [x] Touch event 를 게임 좌표계로 변환
- [x] Scene 별 package 분리

## Tiled Map

- [x] `assets/map/stage_1.tmj` ~ `stage_3.tmj` 추가
- [x] MainActivity 에서 Stage 1~3 선택 후 Intent 로 전달
- [x] tile image 추가
- [x] TMJ 파일을 `kotlinx.serialization` 으로 읽기
- [x] TMJ 전체 schema 를 만들지 않고 현재 필요한 필드만 `data class` 로 선언
- [x] app module 에 `TiledBackground` 추가
- [x] `TiledBackground` 로 배경 표시
- [ ] `TiledMap` 을 a2dg 로 옮기기
- [ ] 이번 게임 전용 `DesertMapBg` 로 상속 분리
- [x] multi-touch pinch 로 tile 확대/축소
- [x] drag 로 map scroll
- [x] scroll 범위를 제한해 map 바깥이 보이지 않도록 처리
- [x] 화면에 보이는 tile 범위만 그리도록 draw loop 구성
- [x] `MapCamera` 로 map 확대/이동 matrix 분리
- [x] `MapCamera.visibleMapRect` 기준으로 필요한 tile 만 선택해 그림
- [x] 설치 가능한 tile 판정 구현

### Map Json 파싱 방법 선정

Tiled 에서 저장한 `.tmj` 파일은 JSON 형식이다. 따라서 파일을 읽는 방법은 여러 가지가 있지만, 이번 프로젝트는 수업용 예제이므로 "빨리 읽는 것"뿐 아니라 "학생들이 구조를 이해할 수 있는가"도 함께 고려해야 한다.

Android 기본 API 인 `org.json.JSONObject` 를 쓰면 별도 의존성을 추가하지 않아도 된다. 하지만 `getInt()`, `getString()`, `getJSONArray()` 같은 호출이 반복되어 코드가 길어지고, JSON 의 구조가 Kotlin type 으로 드러나지 않는다. 작은 실험 코드에는 편하지만, map data 를 계속 확장할 예정인 프로젝트에는 유지보수가 불편해진다.

quicktype.io 같은 도구로 JSON 에서 class 를 자동 생성하는 방법도 있다. 이 방식은 처음 시작할 때 빠르고, 전체 JSON 구조를 빠짐없이 class 로 만들어 준다는 장점이 있다. 반면 Tiled 는 `wangsets`, `tiledversion`, `nextlayerid` 처럼 지금 단계에서 쓰지 않는 정보도 많이 저장한다. 자동 생성 class 를 그대로 사용하면 수업 초반부터 코드가 커지고, 정작 우리가 필요한 정보가 무엇인지 흐려질 수 있다.

그래서 이번 프로젝트에서는 `kotlinx.serialization` 을 사용한다. TMJ 전체 schema 를 모두 옮기지 않고, 지금 필요한 필드만 `@Serializable data class` 로 선언한다. 예를 들어 현재 단계에서는 `width`, `height`, `tilewidth`, `tileheight`, `layers.data`, `tilesets.image` 정도가 핵심이다.

`Json { ignoreUnknownKeys = true }` 옵션을 사용하면 data class 에 선언하지 않은 필드는 자동으로 무시된다. 덕분에 Tiled 가 저장한 부가 정보는 그대로 파일에 남겨 두면서도, Kotlin 코드에서는 현재 필요한 구조만 작게 다룰 수 있다.

이 방식은 JSON 구조를 Kotlin 의 `data class` 로 설명할 수 있고, 이후 필요한 필드가 생길 때마다 class 에 property 를 하나씩 추가해 나갈 수 있다. 즉, quicktype 의 "타입으로 읽는다"는 장점은 유지하면서도, 수업 단계에 맞게 코드 크기를 조절할 수 있다.

## Path Finding

- [x] Marker layer 에서 start / end / walkable tile 스캔
- [x] Marker layer 의 walkable / start / end tile 을 화면에 표시
- [x] A* 를 update 마다 한 단계씩 진행
- [x] 선택한 tile 의 A* 상태를 숫자로 표시하고 step 일시정지
- [x] raw path 를 tile 영역으로 표시
- [x] 같은 방향 path 를 축약해 표시
- [x] centered waypoint 를 표시
- [ ] randomized waypoint 를 표시
- [ ] cubic path 를 생성해 표시
- [ ] `Fly` 가 계산된 path 를 따라 이동

## Map Selection

- [x] 터치 위치를 map 좌표로 변환
- [x] tap / drag / pinch 입력 구분
- [x] 설치 위치를 tile 중심으로 snap
- [x] 선택 표시를 tile grid 에 snap
- [x] 설치 가능 위치와 불가능 위치를 서로 다른 이미지로 표시
- [x] 기존 포탑과 겹치는 위치에는 설치 불가 처리
- [x] 기존 포탑을 터치하면 해당 포탑 선택
- [x] 선택 메뉴 배경 표시
- [x] tap 위치에 1 level `Cannon` 즉시 설치
- [x] 설치 위치 선택 시 설치 메뉴 표시
- [x] 기존 포탑 선택 시 업그레이드 / 철거 메뉴 표시
- [x] vararg 호출로 배열 객체가 매번 생기지 않도록 메뉴 배열 상수화
- [x] 화면 오른쪽 끝에서 메뉴가 잘리지 않도록 방향 조정
- [x] 메뉴 rect 계산을 draw / touch hit-test 가 함께 쓰도록 함수로 분리
- [ ] resource id 로그는 debug build 에서만 resource entry name 으로 출력
- [x] 설치 불가 / 업그레이드 불가 메뉴에 금지 표시 overlay
- [x] 메뉴 표시 alpha animation 적용

### Touch Policy Summary

- `touch down / move` 에서는 selection 이 보이고, 설치 가능 여부를 즉시 표시한다.
- `touch up` 에서는 설치 가능 타일이면 install menu 를, cannon 이 선택되어 있으면 manage menu 를 보여 준다.
- 설치/업그레이드가 실패하면 selection/menu 는 그대로 유지한다.
- uninstall 이 성공하면 selection/menu 모두 사라진다.
- 메뉴가 보이는 동안 메뉴 밖을 누르면 메뉴만 닫고, 같은 위치 기준으로 selection 을 다시 보여 준다.
- 첫 `move` 가 빠르면 drag 로 처리하고, 늦거나 drag 불가면 selection 을 갱신한다.
- multi-touch 가 시작되면 selection/menu 모두 사라진다.
- 설치 불가 / 업그레이드 불가 항목에는 `not_available.png` 오버레이를 덮어서 현재 상태를 보여 준다.
- 금지 여부 판단은 `MainScene`의 `isMenuItemProhibited()`가 담당하고, `CannonMenu`는 그 결과를 그리기와 터치 차단에만 쓴다.

| Situation | Selection | Menu | Note |
|---|---|---|---|
| `touch down / move` | show | hidden or same | tile 의 가능 / 불가능 상태를 즉시 보여 준다 |
| `touch up` + 설치 가능 타일 | keep | install menu | cannon 이 선택돼 있으면 manage menu |
| `touch up` + 불가능 타일 | hide | hide | selection 이 사라진다 |
| install success | keep | switch to manage menu | install 실패 시 selection/menu 그대로 유지 |
| install fail | keep | keep | 골드 부족 등으로 설치하지 못하면 아무 것도 닫지 않는다 |
| `touch up` + cannon selected | keep | upgrade / uninstall menu | cannon 위 터치 상태를 관리 메뉴로 바꾼다 |
| upgrade success | keep | keep manage menu | upgrade 실패 시 selection/menu 그대로 유지 |
| upgrade fail | keep | keep | 골드 부족 등으로 업그레이드하지 못하면 아무 것도 닫지 않는다 |
| uninstall success | hide | hide | selection/menu 모두 사라진다 |
| menu visible + outside `touch down / move` | show again | hide | 메뉴를 닫고 바로 selection 을 다시 보여 준다 |
| first `move` within drag window | drag | hide or keep | drag 가능하면 map drag, 아니면 selection update |
| multi-touch start / move | hide | hide | pinch 우선 |
| menu item touch start | same gesture consumed | same gesture consumed | 같은 제스처의 move / up 에서는 상태를 바꾸지 않는다 |

## Cannon

- [x] `Cannon` 구현
- [x] 포탑 body 와 barrel 이미지를 따로 그림
- [x] level 별 포탑 이미지 적용
- [x] level 은 1-based index 로 사용
- [x] level 로 발사 간격과 barrel 크기 갱신
- [x] 설치 비용 계산
- [x] 업그레이드 비용 계산
- [x] 판매 가격 계산
- [x] 보유 gold 가 부족하면 설치 / 업그레이드 금지
- [x] 사거리 표시
- [x] 사거리는 level 에 따라 증가
- [x] `DashPathEffect` 로 점선 사거리 원 표시
- [x] 선택된 포탑만 사거리 표시
- [x] 사거리 안의 가장 가까운 적 탐색
- [x] 거리 제곱 비교로 불필요한 `sqrt` 계산 피하기
- [x] x/y 축 거리만으로 빠른 범위 초과 판단
- [x] 포신 회전
- [x] 발사 간격 적용
- [x] 업그레이드 / 철거 처리
- [ ] 최대 level 이후 동작 정리
- [ ] 철거 중 Scene remove 로 발생할 수 있는 문제 점검

## Enemy

- [x] `Fly` 구현
- [x] sprite sheet animation 적용
- [x] type 별 source rect 를 상수 Rect 목록으로 정의
- [x] enemy type 정의
- [x] type 별 life 설정
- [x] type 별 등장 확률을 `Random.nextInt(100)` 과 `when` 구간으로 표현
- [x] boss type 은 일반 random 생성에서 제외
- [x] boss 생성 전용 `Fly.boss()` 추가
- [x] boss 는 일반 Fly 보다 크게 생성
- [x] random size 적용
- [x] random speed 적용
- [x] factory 함수에서 type/size/speed 초기화
- [x] recycle 된 객체의 상태 초기화
- [x] 임시로 여러 마리를 생성해 표시
- [x] `WaveGen` 으로 일정 간격 생성
- [x] 왼쪽에서 생성해 오른쪽으로 이동
- [x] 화면 오른쪽 밖으로 나가면 제거
- [x] `Path` 를 만들고 임시로 화면에 그림
- [x] `PathMeasure` 로 path 길이와 현재 위치 계산
- [x] `PathParser.createPathFromPathData()` 로 SVG path data 사용
- [x] Bezier curve 로 부드러운 경로 적용
- [x] 이동 방향에 따라 회전
- [x] 경로 이동 중 흔들림 적용
- [x] life gauge 표시
- [x] life gauge 가 실제 life 를 따라가며 애니메이션되도록 표시값 분리

## Shell And Collision

- [x] `Shell` 구현
- [x] 포탑 level 에 따라 shell 이미지 선택
- [x] 포탑 각도에 맞춰 shell 속도 계산
- [x] shell power 를 level 에 따라 `10 * 1.2^(level - 1)` 로 계산
- [x] shell radius 를 level 에 따라 조정
- [x] 화면 밖으로 나가면 제거
- [x] app 공통 코드에 radius collision helper 추가
- [x] shell 과 enemy 충돌 검사
- [x] enemy life 감소
- [x] 높은 level shell 에 splash damage 적용
- [x] splash radius 를 power 에 비례하여 계산
- [x] splash damage 는 거리 제곱 비율에 따라 감소
- [x] `Explosion` 표시
- [x] `Explosion` 은 recycle 가능한 객체로 생성
- [x] `Explosion` 은 일정 시간 animation 후 스스로 제거
- [x] recycle bin 재사용 적용

## Wave

- [x] `WaveGen` 구현
- [x] 일정 간격으로 enemy 생성
- [x] enemy 생성 시 boss 여부와 speed ratio 전달
- [x] 시간이 지날수록 생성 간격 감소
- [x] 최소 생성 간격 제한
- [x] 일정 시간마다 boss phase 진입
- [x] boss phase 에서는 boss enemy 생성
- [x] boss phase 종료 조건 처리
- [x] boss phase 시작 후 일정 시간이 지나면 종료
- [x] 화면상의 enemy 가 모두 사라지면 boss phase 종료

## Score

- [x] 숫자 이미지 기반 score 표시
- [x] 초기 자금 설정
- [x] 포탑 설치 시 score 감소
- [x] 포탑 업그레이드 시 score 감소
- [x] 포탑 철거 시 score 일부 반환
- [x] enemy 처치 시 score 증가
- [x] enemy 점수는 max life 기반으로 계산
- [x] score 변경 animation 적용

## Pause Scene

- [x] Back key 로 빈 Scene 을 push 하여 pause 흐름 먼저 확인
- [x] `PauseScene` 생성
- [x] 투명 overlay scene 적용
- [x] `DrawableSprite` 로 speech box 표시
- [x] 1초 전후로 바뀌는 pause 안내 메시지 표시
- [x] 반투명 배경 객체 추가
- [x] Back key 동작 정리
- [x] 빠른 Back key 두 번 입력 시 전체 종료 검토
- [x] Scene stack 종료 흐름 정리

## Polish

- [ ] game / scene / object package 구조 정리
- [ ] 리소스 이름 정리
- [ ] 로그 정리
- [ ] 문자열 생성 비용이 있는 log 는 debug build 에서만 수행
- [ ] 불필요한 allocation 점검
- [ ] draw/update 중 객체 생성이 일어나지 않도록 member 재사용
- [ ] Kotlin 변환 시 `when` 에서 resource id 상수 사용 가능 여부 확인
- [ ] Debug build 와 release build 동작 차이 점검
- [ ] README 진행 상황 갱신
