# CookieRun

`CookieRun` 은 이번 학기 Android 2D 게임 개발 수업에서 진행할 러닝 액션 프로젝트이다.
이 문서는 앞으로 구현할 작업을 체크리스트 형태로 정리한 초안이다.

현재 상태:

- [x] 프로젝트 생성
- [x] 기본 `MainActivity` 생성
- [x] `ViewBinding` 적용
- [x] `buildConfig` 생성 활성화

## Activity / Scene 구성
- `MainActivity` : 게임 타이틀이자 Player Character / Stage 선택 용도로 사용
- `CookieRunActivity` : In-game 화면.
  - Game Loop (`Choreographer`)
  - Character / Stage 를 전달받아 실행
  - Scenes:
    - `MainScene` : Cookie 가 달려감
    - `PauseScene` : 게임이 일시정지되어 재개/종료 를 물음
  - Classes/Objects:
    - `Player` : Cookie 주인공. `SheetSprite` 를 상속하게 될 예정.
    - `MapObject` : 화면 오른쪽에서 생성되어 왼쪽으로 흐르는 맵 구성요소들의 공통 기반.
        - `Platform` / `Floor` : Player 가 착지하고 달릴 바닥 타일.
        - `JellyItem` : 획득 가능한 아이템. 화면 밖으로 나가면 제거하거나 재활용.
        - `Obstacle` : 정적 장애물.
            - `AnimObstacle` : 시간에 따라 이미지가 바뀌는 장애물.
            - `FallingObstacle` : `ValueAnimator` 기반 움직임이 있는 장애물.
    - `ObstacleFactory` : 장애물 종류에 따라 적절한 객체를 생성.
    - `MapLoader` : text file 또는 JSON 기반 맵 데이터를 읽어 `MapObject` 를 배치.
    - UI Components:
        - `Gauge` : 맵 진행상황 같은 값을 시각적으로 표시.
        - `Button` : `Jump`, `Slide`, `Pause`, `Back` 입력을 처리하는 UI 객체.

## Activity / App 시작

- [ ] 타이틀 화면(`MainActivity`) 구성
- [ ] 실제 게임 Activity(`CookieRunActivity`) 추가 (layout xml 없이)
- [ ] `MainActivity` 에서 `CookieRunActivity` 실행
- [ ] 선택한 `cookieId` / `stage` 를 Intent extra 로 전달
- [ ] 게임 Activity 를 landscape mode 로 고정

## a2dg 연결

- [ ] `CookieRun` 에 `a2dg` 모듈 연결
  - [ ] `DragonFlight` 에서 `a2dg` 모듈 복사/연결
  - [ ] gradle 파일에서 `a2dg` 사용 설정 (`settings.gradle.kts`)
  - [ ] `versions.toml` 의존성 항목 보강
  - [ ] `app` 모듈 의존성 연결 (`build.gradle.kts` / `:app`)
- [ ] `CookieRunActivity` 가 `GameActivity`/`BaseGameActivity` 계열을 상속
- [ ] Activity 들을 `.app` package 로 옮김
- [ ] `MainScene` 생성 및 root scene push
- [ ] debug build 일 때만 debug 정보가 보이게 설정
- [ ] debug build 에서 Grid 표시
- [ ] `PauseScene` transparent scene 처리(`isTransparent` / `popAll` 포함)

## Player / 입력 / 이동

- [ ] `Player` 클래스 추가
- [ ] `SheetSprite` 기반 상태(state)별 애니메이션 구성
- [ ] state 에 따라 프레임 Rect 집합 선택 및 애니메이션 전환
- [ ] 입력 처리(`Jump` / `Slide` 버튼, `ACTION_DOWN/UP`)
- [ ] 점프/슬라이드/낙하 동작 구현
- [ ] 중력/더블 점프 물리 적용
- [ ] 낙하 중 플랫폼 착지 시 달리기 상태로 전환
- [ ] 플레이어 쿠키 스킨 선택 기능
- [ ] Magnification/Scale 아이템 효과 적용
- [ ] 플레이어 애니메이션 적용(run/jump/slide/hit)

## Map / 장애물 / 아이템

- [ ] 수평 스크롤 배경(`HorzScrollBackground`) 적용
- [ ] 바닥 타일(`Platform`/`Floor`) 기반 맵 구성
- [ ] 장애물(`Obstacle`) 클래스 추가
- [ ] `ObstacleFactory` 로 장애물 생성 분리
- [ ] `AnimObstacle`/`FallingObstacle` 등 하위 타입 추가
- [ ] `JellyItem` 추가 및 재활용 처리
- [ ] 텍스트 파일 기반 맵 로딩(`MapLoader`) 구현
- [ ] JSON 기반 맵 로딩 가능성 검토
- [ ] 맵 진행 상황 `Gauge` 표시

## 충돌 / 판정

- [ ] `IBoxCollidable` 적용
- [ ] `Player` 와 `JellyItem` 충돌 처리
- [ ] `Player` 와 `Obstacle` 충돌 처리
- [ ] `collisionRect` / inset 조정
- [ ] `AnimObstacle` collision rect 보정

## Game Loop / 상태 전환

- [ ] 일시정지/재개 처리
- [ ] `Back` 버튼 처리
- [ ] `Pause` 버튼 추가 및 입력 처리
- [ ] `Jump` / `Slide` 버튼 추가 (`Slide` pressed/released 처리)
- [ ] `PausedScene` push/pop 으로 일시정지 UI 구성

## 이펙트 / 마무리

- [ ] 피격/획득/점프 이펙트 추가
- [ ] 리소스 정리 및 네이밍 통일
- [ ] 릴리즈 빌드 점검

## Notes

- 이 문서는 작년 CookieRun 이력을 기준으로 정리했다.
- 상황에 따라 항목이 추가/삭제될 수 있다.

