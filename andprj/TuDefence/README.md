# TuDefence

Android 2D game programming 수업에서 진행할 타워 디펜스 예제 프로젝트이다.

플레이어는 타일 맵 위에 포탑을 설치하고, 정해진 경로를 따라 이동하는 적을 막는다. 프로젝트는 Android 기본 Activity 에서 시작해 `a2dg` 기반 게임 구조, 타일 맵, 오브젝트 재활용, 충돌 처리, 웨이브 생성, 일시정지 화면까지 단계적으로 확장한다.

## 현재 상태

- [x] Android 프로젝트 생성
- [x] 기본 `MainActivity` 생성
- [ ] 게임 Activity 분리
- [ ] `a2dg` 모듈 연결
- [ ] 게임 화면 좌표계 설정

## Project Setup

- [ ] 앱 패키지와 이름 정리
- [ ] `buildFeatures` 에 `viewBinding` / `buildConfig` 활성화
- [ ] `MainActivity` 를 시작 화면으로 사용
- [ ] `MainGameActivity` 추가
- [ ] `MainGameActivity` 는 layout xml 없이 게임 Activity 로 구성
- [ ] Debug build 에서 게임 화면 자동 실행 지원
- [ ] Landscape mode 고정
- [ ] 게임 리소스 복사 및 정리

## a2dg Integration

- [ ] `a2dg` library module 추가
- [ ] `settings.gradle.kts` 에 `:a2dg` module 등록
- [ ] `app` module 이 `a2dg` module 을 dependency 로 사용하도록 설정
- [ ] `BaseGameActivity` 기반 게임 Activity 구성
- [ ] `Scene` / `World` / `Layer` 구조 적용
- [ ] 게임 좌표계를 1600 x 900 기준으로 설정
- [ ] Debug grid / FPS / debug info 표시 확인

## Main Scene

- [ ] 빈 Scene 을 먼저 push 하여 GameActivity 동작 확인
- [ ] `MainScene` 생성
- [ ] Layer enum 정의
  - [ ] `BG`
  - [ ] `ENEMY`
  - [ ] `CANNON`
  - [ ] `SHELL`
  - [ ] `EXPLOSION`
  - [ ] `SCORE`
  - [ ] `SELECTION`
  - [ ] `CONTROLLER`
- [ ] Back key 로 `PauseScene` 진입
- [ ] Touch event 를 게임 좌표계로 변환
- [ ] Scene 별 package 분리

## Tiled Map

- [ ] `assets/map/desert.tmj` 추가
- [ ] tile image 추가
- [ ] TMJ 파일을 `kotlinx.serialization` 으로 읽기
- [ ] TMJ 전체 schema 를 만들지 않고 현재 필요한 필드만 `data class` 로 선언
- [ ] `a2dg` 에 `TiledBackground` 추가
- [ ] `TiledBackground` 로 배경 표시
- [ ] 이번 게임 전용 `DesertMapBg` 로 상속 분리
- [ ] touch 한 좌표의 tile index 를 debug log 로 확인
- [ ] 설치 가능한 tile 판정 구현
- [ ] 포탑이 차지하는 2 x 2 tile 영역 검사

### Map Json 파싱 방법 선정

Tiled 에서 저장한 `.tmj` 파일은 JSON 형식이다. 따라서 파일을 읽는 방법은 여러 가지가 있지만, 이번 프로젝트는 수업용 예제이므로 "빨리 읽는 것"뿐 아니라 "학생들이 구조를 이해할 수 있는가"도 함께 고려해야 한다.

Android 기본 API 인 `org.json.JSONObject` 를 쓰면 별도 의존성을 추가하지 않아도 된다. 하지만 `getInt()`, `getString()`, `getJSONArray()` 같은 호출이 반복되어 코드가 길어지고, JSON 의 구조가 Kotlin type 으로 드러나지 않는다. 작은 실험 코드에는 편하지만, map data 를 계속 확장할 예정인 프로젝트에는 유지보수가 불편해진다.

quicktype.io 같은 도구로 JSON 에서 class 를 자동 생성하는 방법도 있다. 이 방식은 처음 시작할 때 빠르고, 전체 JSON 구조를 빠짐없이 class 로 만들어 준다는 장점이 있다. 반면 Tiled 는 `wangsets`, `tiledversion`, `nextlayerid` 처럼 지금 단계에서 쓰지 않는 정보도 많이 저장한다. 자동 생성 class 를 그대로 사용하면 수업 초반부터 코드가 커지고, 정작 우리가 필요한 정보가 무엇인지 흐려질 수 있다.

그래서 이번 프로젝트에서는 `kotlinx.serialization` 을 사용한다. TMJ 전체 schema 를 모두 옮기지 않고, 지금 필요한 필드만 `@Serializable data class` 로 선언한다. 예를 들어 현재 단계에서는 `width`, `height`, `tilewidth`, `tileheight`, `layers.data`, `tilesets.image` 정도가 핵심이다.

`Json { ignoreUnknownKeys = true }` 옵션을 사용하면 data class 에 선언하지 않은 필드는 자동으로 무시된다. 덕분에 Tiled 가 저장한 부가 정보는 그대로 파일에 남겨 두면서도, Kotlin 코드에서는 현재 필요한 구조만 작게 다룰 수 있다.

이 방식은 JSON 구조를 Kotlin 의 `data class` 로 설명할 수 있고, 이후 필요한 필드가 생길 때마다 class 에 property 를 하나씩 추가해 나갈 수 있다. 즉, quicktype 의 "타입으로 읽는다"는 장점은 유지하면서도, 수업 단계에 맞게 코드 크기를 조절할 수 있다.

## Map Selection

- [ ] `MapSelector` 구현
- [ ] MainScene 의 touch 처리 책임을 `MapSelector` 로 이동
- [ ] 터치 위치를 tile 좌표로 변환
- [ ] 선택 표시를 tile grid 에 snap
- [ ] 선택 표시를 화면 밖 좌표로 옮겨 숨기는 방식 적용
- [ ] 설치 가능 위치와 불가능 위치를 서로 다른 이미지로 표시
- [ ] 기존 포탑과 겹치는 위치에는 설치 불가 처리
- [ ] 기존 포탑을 터치하면 해당 포탑 선택
- [ ] 선택 메뉴 배경 표시
- [ ] 설치 위치 선택 시 설치 메뉴 표시
- [ ] 기존 포탑 선택 시 업그레이드 / 철거 메뉴 표시
- [ ] vararg 호출로 배열 객체가 매번 생기지 않도록 메뉴 배열 상수화
- [ ] 화면 오른쪽 끝에서 메뉴가 잘리지 않도록 방향 조정
- [ ] 메뉴 rect 계산을 draw / touch hit-test 가 함께 쓰도록 함수로 분리
- [ ] resource id 로그는 debug build 에서만 resource entry name 으로 출력
- [ ] 설치 불가 / 업그레이드 불가 메뉴에 금지 표시 overlay
- [ ] 메뉴 표시 alpha animation 적용

## Cannon

- [ ] `Cannon` 구현
- [ ] 포탑 body 와 barrel 이미지를 따로 그림
- [ ] level 별 포탑 이미지 적용
- [ ] level 은 1-based index 로 사용
- [ ] `setLevel()` 로 이미지, 사거리, 발사 간격, barrel 크기 갱신
- [ ] 설치 비용 계산
- [ ] 업그레이드 비용 계산
- [ ] 판매 가격 계산
- [ ] 보유 gold 가 부족하면 설치 / 업그레이드 금지
- [ ] 사거리 표시
- [ ] 사거리는 level 에 따라 증가
- [ ] `DashPathEffect` 로 점선 사거리 원 표시
- [ ] 선택된 포탑만 사거리 표시
- [ ] 사거리 안의 가장 가까운 적 탐색
- [ ] 거리 제곱 비교로 불필요한 `sqrt` 계산 피하기
- [ ] x/y 축 거리만으로 빠른 범위 초과 판단
- [ ] 포신 회전
- [ ] barrel 원본 방향을 기준으로 초기 각도 보정
- [ ] 발사 간격 적용
- [ ] 업그레이드 / 철거 처리
- [ ] 최대 level 이후 동작 정리
- [ ] 철거 중 Scene remove 로 발생할 수 있는 문제 점검

## Enemy

- [ ] `Fly` 구현
- [ ] sprite sheet animation 적용
- [ ] type 별 source rect 미리 생성
- [ ] enemy type 정의
- [ ] type 별 life 설정
- [ ] type 별 등장 확률 설정
- [ ] 확률 가중치를 누적합으로 처리
- [ ] boss type 은 일반 random 생성에서 제외
- [ ] random size 적용
- [ ] random speed 적용
- [ ] factory 함수에서 type/size/speed 초기화
- [ ] recycle 된 객체의 상태 초기화
- [ ] 임시로 여러 마리를 생성해 표시
- [ ] `WaveGen` 으로 일정 간격 생성
- [ ] `Path` 를 만들고 임시로 화면에 그림
- [ ] `PathMeasure` 로 path 길이와 현재 위치 계산
- [ ] `PathParser.createPathFromPathData()` 로 SVG path data 사용
- [ ] Bezier editor 로 만든 경로를 코드에 적용
- [ ] 이동 방향에 따라 회전
- [ ] 경로 이동 중 흔들림 적용
- [ ] life gauge 표시
- [ ] life gauge 가 실제 life 를 따라가며 애니메이션되도록 표시값 분리
- [ ] 화면 끝에 도착하면 제거
- [ ] recycle bin 재사용 적용

## Shell And Collision

- [ ] `Shell` 구현
- [ ] 포탑 level 에 따라 shell 이미지 선택
- [ ] 포탑 각도에 맞춰 shell 속도 계산
- [ ] shell power 를 level 에 따라 `10 * 1.2^(level - 1)` 로 계산
- [ ] shell radius 를 level 에 따라 조정
- [ ] 화면 밖으로 나가면 제거
- [ ] `a2dg` 에 radius collision helper 추가
- [ ] shell 과 enemy 충돌 검사
- [ ] enemy life 감소
- [ ] enemy 사망 시 score 증가
- [ ] 높은 level shell 에 splash damage 적용
- [ ] splash radius 를 power 에 비례하여 계산
- [ ] splash damage 는 거리 제곱 비율에 따라 감소
- [ ] `Explosion` 표시
- [ ] `Explosion` 은 recycle 가능한 객체로 생성
- [ ] `Explosion` 은 일정 시간 animation 후 스스로 제거
- [ ] recycle bin 재사용 적용

## Wave

- [ ] `WaveGen` 구현
- [ ] 일정 간격으로 enemy 생성
- [ ] enemy 생성 시 boss 여부와 speed ratio 전달
- [ ] 시간이 지날수록 생성 간격 감소
- [ ] 최소 생성 간격 제한
- [ ] 일정 시간마다 boss phase 진입
- [ ] boss phase 에서는 boss enemy 생성
- [ ] boss phase 종료 조건 처리
- [ ] boss phase 시작 후 일정 시간이 지나면 종료
- [ ] 화면상의 enemy 가 모두 사라지면 boss phase 종료
- [ ] wave 증가에 따른 난이도 조정 여지 남기기
- [ ] wave debug 를 위한 시간 가속 옵션 검토

## Score

- [ ] 숫자 이미지 기반 score 표시
- [ ] 초기 자금 설정
- [ ] 포탑 설치 시 score 감소
- [ ] 포탑 업그레이드 시 score 감소
- [ ] 포탑 철거 시 score 일부 반환
- [ ] enemy 처치 시 score 증가
- [ ] enemy 점수는 max life 기반으로 계산
- [ ] score 변경 animation 적용

## Pause Scene

- [ ] Back key 로 빈 Scene 을 push 하여 pause 흐름 먼저 확인
- [ ] `PauseScene` 생성
- [ ] 투명 overlay scene 적용
- [ ] 반투명 배경 객체 추가
- [ ] Resume 버튼 추가
- [ ] Exit 버튼 추가
- [ ] Back key 동작 정리
- [ ] 빠른 Back key 두 번 입력 시 전체 종료 검토
- [ ] Scene stack 종료 흐름 정리
- [ ] Scene pause 시 Animator 등 외부 작업 pause/resume 구조 검토

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
