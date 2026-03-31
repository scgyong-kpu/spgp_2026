# a2dg Roadmap

`a2dg` 는 Android 2D Game framework 를 수업용으로 천천히 만들어 가는 모듈이다.
남은 프로젝트 동안 `a2dg` 쪽에 추가해 볼 만한 기능들을 초안 형태로 정리한 것이다.

현재 이미 들어 있는 것:

- `BaseGameActivity`
- `GameView`, `GameContext`, `GameMetrics`
- `Scene`, `SceneStack`, `World`
- `BitmapPool`, `GameResources`
- `IGameObject`, `Sprite`, `JoyStick`

아래 항목들은 아직 확정된 계획이라기보다,
수업 중 하나씩 작은 commit 으로 풀어 가볼 수 있는 후보 목록이다.

## Objects

- [ ] `Sprite`
  - [ ] bitmap 을 바꿀 수 있는 public API 추가

- [ ] `AnimSprite`
  - [ ] 여러 frame 을 순서대로 보여주는 공통 클래스 추가
  - [ ] frame index 로 `srcRect` 를 바꾸기
  - [ ] fps 를 변경할 수 있는 API 추가
  - [ ] pause 되었을 때 애니메이션이 어떻게 동작해야 하는지 정리

- [ ] `SheetSprite`
  - [ ] `srcRects` 를 활용해 bitmap 일부만 그리는 공통 클래스 추가
  - [ ] frame index 로 `srcRect` 를 바꾸기


- [ ] Background objects
  - [ ] 세로 스크롤 Background 공통 클래스 추가
  - [ ] 가로 스크롤 Background 공통 클래스 추가
  - [ ] `TiledBackground` 공통 클래스 추가
  - [ ] 게임별 Background subclass 예제 추가

- [ ] UI / HUD objects
  - [ ] `Gauge` 공통 클래스 추가
  - [ ] `Score` 같이 숫자 변화가 보이는 공통 HUD 객체 추가
  - [ ] `Text` 같은, 문자열을 표시하는 공통 HUD 객체 추가
  - [ ] `Button` 공통 클래스 추가

## Scene / View / Game Loop

- [ ] `GameView`
  - [ ] frame time 이 비정상적으로 커지는 경우 대비

- [ ] Scene lifecycle
  - [ ] Scene 이 transparent 하게 위에 올라가는 경우
  - [ ] 마지막 Scene 이 pop 되는 경우 처리

- [ ] Scene structure
  - [ ] Scene 안의 touch 책임을 별도 객체로 위임하는 예제 정리
  - [ ] Scene 교체와 push / pop 사용 기준 정리

## Resource / Audio

- [ ] `GameResources`
  - [ ] Bitmap 외 다른 리소스도 이쪽으로 모을지 검토
  - [ ] bitmap 로딩 정책 문서 보강

- [ ] `Sound`
  - [ ] 사운드 관리 기능 추가
  - [ ] App pause / Scene pause 시 재생을 멈추는 공통 규칙 정리
  - [ ] resume 시 어떤 소리까지 복원할지 기준 정리

## Map / Background

- [ ] tile map 관련 기본 구조
  - [ ] `Tileset` 추가
  - [ ] `TiledMap` 추가
  - [ ] `MapLayer` 추가
  - [ ] map 좌표 변환용 `Converter` 추가

- [ ] map 활용 예제
  - [ ] 게임 전용 subclass 가 어떤 역할을 가져야 하는지 예제 추가
  - [ ] map 의 touch 처리 책임을 어디에 둘지 정리

## Collision / Utility

- [ ] Collision helpers
  - [ ] 반지름 기반 거리 충돌 helper
  - [ ] 사각형 충돌 (AABB) helper

- [ ] Utility classes
  - [ ] `RectUtil` 같은 사각형 helper 추가 검토
  - [ ] 공통 수학 / 좌표계 helper 가 필요한지 검토

## Interfaces / Patterns

- [ ] 입력 관련 인터페이스
  - [ ] `ITouchable` 이 필요한지 검토
  - [ ] `JoyStick` 와 `Button` 이 생기면 공통 터치 계약이 필요한지 다시 보기

- [ ] 충돌 / layer 관련 인터페이스
  - [ ] `IBoxCollidable` 이 필요한지 검토
  - [ ] `ILayerProvider` 같은 보조 인터페이스가 필요한지 검토

- [ ] 재활용 / 생명주기 관련 패턴
  - [ ] `IRecyclable` 이 필요한지 검토
  - [ ] 재활용 가능한 객체 패턴
  - [ ] 재활용과 Scene / World 생명주기 관계 정리

## Notes

- 어떤 항목은 이번 학기에 정말 구현하고, 어떤 항목은 "왜 아직 넣지 않는지"를 정리하는 것으로 끝날 수도 있다.
- 이 문서는 일단 초안이며, 실제 수업 진행에 맞게 항목을 더 잘게 나누거나 합칠 수 있다.
