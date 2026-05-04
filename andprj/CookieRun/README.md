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
    - `Player` : Cookie 주인공. `SheetSprite` 를 상속하고 상태별 프레임과 충돌 박스를 함께 관리한다.
    - `MapObject` : 맵 위에 놓이는 오브젝트들의 공통 기반.
      - `Floor` : Player 가 착지하고 달릴 바닥 타일. `Floor.Type` 으로 타일 종류를 구분한다.
      - `JellyItem` : 획득 가능한 아이템. 스프라이트 시트의 `index` 로 칸을 고른다.
      - `Obstacle` : 정적 장애물.
        - `AnimObstacle` : 시간에 따라 이미지가 바뀌는 장애물.
        - `FallingObstacle` : `ValueAnimator` 기반 움직임이 있는 장애물.
    - `MapObject` 계열은 앞으로 왼쪽으로 흐르는 맵 요소를 함께 다루는 데 쓰인다.
    - `CollisionChecker` : `Player` 와 `JellyItem` 충돌을 검사하고 아이템을 지운다.
    - `MapObjectRegistry` : stage 문자별 `MapObject` 생성 규칙을 저장한다.
    - `MapObjectCatalog` : 이 게임에서 사용할 `Floor`, `JellyItem`, `Obstacle` 생성 규칙을 등록한다.
    - `MapLoader` : assets 의 `stage_*.txt` 를 읽어 `Floor`, `JellyItem`, `Obstacle` 을 배치.
    - UI Components:
        - `Gauge` : 맵 진행상황 같은 값을 시각적으로 표시.
        - `Button` : `Jump`, `Slide`, `Pause`, `Back` 입력을 처리하는 UI 객체.

## Activity / App 시작

- [x] 타이틀 화면(`MainActivity`) 구성
- [x] 실제 게임 Activity(`CookieRunActivity`) 추가 (layout xml 없이)
- [x] `MainActivity` 에서 `CookieRunActivity` 실행
  - [x] Debug Build 시 1초 후 자동실행
- [ ] 선택한 `cookieId` / `stage` 를 Intent extra 로 전달
  - [x] 선택한 `stage` 를 Intent extra 로 전달
  - [ ] 선택한 `cookieId` 를 Intent extra 로 전달
- [ ] 게임 Activity 를 landscape mode 로 고정

## a2dg 연결

- [x] `CookieRun` 에 `a2dg` 모듈 연결
  - [x] `DragonFlight` 에서 `a2dg` 모듈 복사/연결
  - [x] gradle 파일에서 `a2dg` 사용 설정 (`settings.gradle.kts`)
  - [x] `versions.toml` 의존성 항목 보강
  - [x] `app` 모듈 의존성 연결 (`build.gradle.kts` / `:app`)
  - [x] 추가 오류 해결 (`build.gradle.kts` / `:app`)
- [x] `CookieRunActivity` 가 `GameActivity`/`BaseGameActivity` 계열을 상속
- [x] Activity 들을 `.app` package 로 옮김
- [x] `MainScene` 생성 및 root scene push
- [x] debug build 일 때만 debug 정보가 보이게 설정
- [x] debug build 에서 Grid 표시
- [x] `PauseScene` transparent scene 처리(`isTransparent` / `popAll` 포함)

## MainScene 배경 및 화면 좌표계 설정
- [x] 가상좌표계 가로방향으로 설정
- [x] BG 3장 추가
- [x] 수평 스크롤 배경(`HorzScrollBackground`) 적용
- [x] 바닥 타일(`Floor`) 기반 맵 구성


## Player / 입력 / 이동

- [x] `Player` 클래스 추가
- [x] `SheetSprite` 기반 상태(state)별 애니메이션 구성
- [x] state 에 따라 프레임 Rect 집합 선택 및 애니메이션 전환
- [x] `RUN` / `SLIDE` 중 발밑에 바닥이 없으면 `FALL` 상태로 전환
- [x] `JUMP` / `FALL` 중 중력에 따라 낙하하고 바닥에 닿으면 `RUN` 으로 복귀
- [x] 입력 처리(`Jump` / `Slide` / `Fall` 버튼, `ACTION_DOWN/UP`)
  - [x] `MainScene` 이 `Layer.TOUCH` 를 Scene touch dispatch 대상으로 지정
- [x] 점프/슬라이드 동작 세부 다듬기
- [x] 중력/더블 점프 물리 세부 조정
- [x] 낙하 중 플랫폼 착지 시 달리기 상태로 전환
- [ ] 플레이어 쿠키 스킨 선택 기능
- [x] Magnification/Scale 아이템 효과 적용
- [x] Magnification scale 에 따라 Player 크기와 점프 파워 조정
- [x] 플레이어 애니메이션 적용(run/jump/slide/fall)
- [x] 플레이어 hurt 애니메이션 적용

## Map / 장애물 / 아이템

- [x] 수평 스크롤 배경(`HorzScrollBackground`) 적용
- [x] 바닥 타일(`Platform`/`Floor`) 기반 맵 구성
- [x] `JellyItem` 추가 및 재활용 처리
- [x] `JellyItem.MAGNIFICATION_INDEX` 로 특수 젤리 구분
- [x] `MapLoader` 가 매 프레임 `Floor` / `JellyItem` 을 생성
- [x] assets 의 `stage_*.txt` 기반 맵 로딩
- [x] `Obstacle` 클래스 추가
- [x] `MapObjectRegistry` / `MapObjectCatalog` 로 맵 오브젝트 생성 규칙 분리
- [x] `SimpleObstacle`/`AnimObstacle`/`FallingObstacle` 등 장애물 하위 타입 추가
- [x] 텍스트 파일 기반 맵 로딩(`MapLoader`) 구현
- [ ] JSON 기반 맵 로딩 가능성 검토
- [x] 맵 진행 상황 `Gauge` 표시

## 충돌 / 판정

- [x] `IBoxCollidable` 적용
- [x] `CollisionChecker` 추가
- [x] `Player` 와 `JellyItem` 충돌 처리
- [x] `Player` / `JellyItem` 의 `collisionRect` 를 `dstRect` 와 분리해 inset 적용
- [x] `Player` 와 `Obstacle` 충돌 처리
- [x] `collisionRect` / inset 조정
- [x] `AnimObstacle` collision rect 보정

## Game Loop / 상태 전환

- [ ] 일시정지/재개 처리
- [x] `Back` 버튼 처리
- [x] `Pause` 버튼 추가 및 입력 처리
- [x] `Jump` / `Slide` 버튼 추가 (`Slide` pressed/released 처리)
- [x] `Fall` 버튼 추가
  - [x] 통과 가능한 바닥에서만 `Fall` 입력이 동작하도록 처리
- [x] `PausedScene` push/pop 으로 일시정지 UI 구성
  - [x] transparent Scene 처리
  - [x] Exit 버튼에서 Scene stack 전체 종료 처리

## 이펙트 / 마무리

- [x] 피격/획득/점프 효과음 추가
- [x] 게임 배경음 추가
- [ ] 리소스 정리 및 네이밍 통일
- [ ] 릴리즈 빌드 점검

## Notes

- 이 문서는 수업 진행에 맞춰 구현 단계와 체크리스트를 함께 갱신한다.
- 상황에 따라 항목이 추가/삭제될 수 있다.
