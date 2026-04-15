# DragonFlight

`DragonFlight` 는 이번 학기 Android 2D 게임 개발 수업에서 사용할 프로젝트이다.
이번 학기에 진행할 작업 순서를 초안 형태로 정리한 것이다.

현재 상태:

- [x] 프로젝트 생성
- [x] `Start Game` 버튼이 있는 시작 화면 추가
- [x] `ViewBinding` 적용

## Activity / App 시작

- [x] 타이틀 화면 성격의 `MainActivity` 에서 실제 게임 Activity 인 `DragonFlightActivity` 를 실행
- [x] 게임 Activity 를 portrait mode 로 고정
- [x] debug build 일 때만 debug 정보가 보이게 설정
- [x] Debug 용으로, `MainActivity` 생성시 자동으로 `DragonFlightActivity` 를 실행

## a2dg 연결

- [x] `DragonFlight` 에 `a2dg` 모듈 연결
  - [x] `SampleGame` 에서 작성한 `a2dg` 모듈 을 그대로 복사
  - [x] gradle 파일 에서 `a2dg` 사용여부 연결 (`settings.gradle.kts`)
  - [x] `app` 모듈 에서 `a2dg` 모듈에 의존하도록 연결 (`build.gradle.kts` / `:app`)
- [x] `DragonFlightActivity` 또는 그에 해당하는 Activity 가 `BaseGameActivity` 를 상속
- [x] 빈 `MainScene` 을 만들어 root scene 으로 push

## 기본 게임 화면

- [x] `MainScene` 생성
- [x] `Player` 클래스 추가
  - [x] `open`/`override` 관련 에러 처리
- [x] `MainScene` 이 `Player` 를 생성해서 화면에 배치
  - [x] Layer 로 `Int` 를 썼다가 `enum` 으로 변경
  - [x] Debug Grid 표시
    - [x] BuildConfig 를 사용해 표시
- [x] 입력을 받아 `Player` 가 좌우 또는 목표 방향으로 이동
  - [ ] `JoyStick` 사용
  - [x] 직접 처리
  - [x] 화면 경계를 벗어나지 않게 처리
  - [x] Target 을 향해 움직이도록
  - [x] Roll 적용
- [ ] 게임 캐릭터 이미지 적용

## Bullet / Spark / 기본 전투

- [x] (추가) `Bullet` 클래스 추가
- [x] 일정 시간마다 `Bullet` 발사
- [x] 총알 시작 위치를 전투기 중심보다 자연스러운 위치로 보정
- [x] 총알이 화면 밖으로 나가면 삭제
- [x] 발사 효과용 `Spark` 추가
- [x] `Spark` 표시 시간 조정

## Enemy / 생성 규칙

- [x] `Enemy` 클래스 추가
- [x] `Enemy` 생성 및 배치
- [x] 화면 위에서 생성되어 아래로 지나가게 처리
- [x] 화면 밖으로 나가면 삭제
- [x] `EnemyGenerator` 같은 생성 담당 객체 도입
- [x] (추가) `Enemy` 를 `AnimSprite` 로 바꾸고 `enemy_01` strip 적용
- [x] (추가) `enemy_02` ~ `enemy_20` 리소스를 복사하고 `Enemy` level 이미지 연결
- [x] (추가) `a2dg Sprite` 의 `dstRect` sync 규칙 변경에 맞춰 app 초기화 코드 정리
- [x] wave 에 따른 생성 규칙 추가
  - [x] wave 별 점차 센 적이 나오지만 랜덤 요소도 있도록
- [x] level 별 이미지/속도 차이 적용

## 충돌 / 점수 / HUD

- [x] Bullet 과 Enemy 충돌 처리
  - [x] `CollisionChecker` 를 두고 `BULLET`, `ENEMY` 를 순회하기 시작
  - [x] `Bullet`, `Enemy` 가 충돌 범위를 제공
  - [x] 충돌이 있는지 실제로 체크
  - [x] 충돌 시 `Bullet` 삭제
  - [x] 충돌 시 `Enemy` 삭제
  - [x] Loop 도는 방식 개선
  - [x] (추가) `collisionRect` 를 빨간 사각형으로 디버그 표시
  - [x] `dstRect` 와 `collisionRect` 는 다를 수 있음
  - [x] `IBoxCollidable` 적용
  - [x] (추가) `Player` 와 `Enemy` 가 충돌하면 `Enemy` 삭제
- [x] Enemy life 추가
- [x] Bullet power 적용
- [x] 충돌 시 점수 증가
- [x] `Score` 표시
- [x] 점수가 애니메이션되며 증가하도록 적용
- [x] `Gauge` 를 이용한 `Enemy` life / `Player` cooltime 표시
  - [x] (추가) `Enemy` 가 `Gauge` 로 life 표시
  - [x] (추가) `Enemy Gauge` 색을 color resource 에서 읽기
  - [x] (추가) `Player cooltime Gauge` 색을 color resource 에서 읽기
  - [x] (추가) `ScoreNumber` 가 `ImageNumber` 로 점수 표시

## Background / Map

- [x] (추가) 정지 background 이미지를 background layer 에 표시
- [x] scrolling background 추가
- [x] 세로 스크롤 background 로 일반화
- [x] 배경 layer 를 여러 장으로 나누어 draw 순서 조정
- [ ] map 관련 파일 복사 또는 추가
- [ ] `TiledBackground` 적용
- [ ] `ForestTiledBg` 같은 게임 전용 background subclass 적용

## Recycle / Collision / Layer 활용

- [x] 객체 삭제 기능 확인
- [x] 객체 재활용 패턴 적용
  - [x] `Bullet.get(...)` 으로 재활용 또는 새 생성을 감추기
  - [x] `Enemy.get(...)` 으로 재활용 또는 새 생성을 감추기
- [x] 충돌 helper 및 box collision 적용

## Pause / Time / Scene

- [x] pause 동안 시간이 흐르지 않도록 처리
- [x] Activity lifecycle 과 pause/resume 연결
- [ ] `PauseScene` 에서 transparent scene 방식 적용

## Notes

- 이 문서는 지난해 `DragonFlight` 커밋 목록을 바탕으로 만든 초안이다.
- 실제 수업에서는 각 항목을 더 작은 commit 단위로 다시 나눌 수 있다.
- 어떤 항목은 이번 학기에는 생략되거나 다른 순서로 진행될 수 있다.
