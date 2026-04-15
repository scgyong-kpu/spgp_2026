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
  - [x] `setCenter()`, `setSize()` helper 에서 `dstRect` 를 바로 sync 하도록 정리
  - [x] `setCenterProportionalWidth()` helper 로 bitmap 비율을 유지한 채 중심과 가로폭을 함께 맞출 수 있게 정리
  - [x] `setCenterProportionalHeight()` helper 로 bitmap 비율을 유지한 채 중심과 세로폭을 함께 맞출 수 있게 정리
  - [x] `draw()` 에서 매번 sync 하지 않고, subclass 초기화 순서에 맞춰 `syncDstRect()` 를 호출하는 규칙 정리

- [x] `AnimSprite`
  - [x] 여러 frame 을 순서대로 보여주는 공통 클래스 추가
  - [x] frame index 로 `srcRect` 를 바꾸기
  - [x] fps 를 변경할 수 있는 API 추가
  - [ ] pause 되었을 때 애니메이션이 어떻게 동작해야 하는지 정리

- [ ] `SheetSprite`
  - [ ] `srcRects` 를 활용해 bitmap 일부만 그리는 공통 클래스 추가
  - [ ] frame index 로 `srcRect` 를 바꾸기


- [ ] Background objects
  - [x] 세로 스크롤 Background 공통 클래스 추가
  - [ ] 가로 스크롤 Background 공통 클래스 추가
  - [ ] `TiledBackground` 공통 클래스 추가
  - [ ] 게임별 Background subclass 예제 추가

- [ ] UI / HUD objects
  - [x] `Gauge` 공통 클래스 추가
  - [x] `ImageNumber` 공통 클래스 추가
  - [x] `LabelUtil` 공통 클래스 추가
  - [ ] `Button` 공통 클래스 추가

## Scene / World / View / Game Loop

- [ ] `GameView`
  - [x] Activity resume 직후 첫 frame 의 nanos 차이가 pause 시간까지 포함되지 않도록 리셋
  - [x] frame time 이 비정상적으로 커지는 경우 대비

- [x] `World`
  - [x] `objectsAt(layer)` 로 특정 layer 의 읽기 전용 객체 목록을 가져오기
  - [x] `forEachReversedAt(layer)` 로 삭제에 안전한 역순 순회 helper 추가
  - [x] `forEachReversedAt(layer)` 를 `inline` 으로 정리
  - [x] `public inline` 함수가 `private` 필드에 접근할 수 없어 `@PublishedApi internal` 이 필요하다는 점 정리
  - [x] `IBoxCollidable` 객체의 collision box 디버그 draw 를 `World.draw()` 로 이동
  - [x] recycle bin 과 `obtain(clazz)` 추가
  - [x] `remove()` 가 `IRecyclable` 객체를 자동 수거하도록 정리
  - [x] `update()` / `draw()` 의 layer 순회를 iterator 없이 돌도록 정리

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
  - [x] `IBoxCollidable` 추가
  - [x] `collidesWith()` extension 으로 AABB 충돌 helper 추가
  - [ ] `ILayerProvider` 같은 보조 인터페이스가 필요한지 검토

- [ ] 재활용 / 생명주기 관련 패턴
  - [x] `IRecyclable` 추가
  - [ ] 재활용 가능한 객체 패턴
  - [ ] 재활용과 Scene / World 생명주기 관계 정리

## Notes

- 어떤 항목은 이번 학기에 정말 구현하고, 어떤 항목은 "왜 아직 넣지 않는지"를 정리하는 것으로 끝날 수도 있다.
- 이 문서는 일단 초안이며, 실제 수업 진행에 맞게 항목을 더 잘게 나누거나 합칠 수 있다.
