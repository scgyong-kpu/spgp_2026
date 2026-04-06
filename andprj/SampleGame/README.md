# SampleGame

`SampleGame`은 하나의 게임을 만드는 데서 끝나지 않고, 재사용 가능한 2D 게임 프레임워크로 발전시키기 위한 중간 단계 프로젝트이다.

## 목표

장기적으로는 프로젝트를 두 개의 모듈로 나누는 것이 목표이다.

- `a2dg`
  - 재사용 가능한 2D 게임 프레임워크
  - 여러 게임에서 공통으로 사용할 수 있는 코드
- `app`
  - 실제 게임 구현
  - `a2dg` 위에서 만들어지는 게임별 코드

원하는 의존 방향은 다음과 같다.

- `app -> a2dg`
- `a2dg -> app` 의존은 생기지 않아야 한다

## 목표 구조

현재 코드는 아래와 같은 구조를 목표로 조금씩 정리되고 있다.

- `GameView`
  - Android `View`와 연결되는 계층
  - 프레임 루프와 scene stack 을 소유
- `SceneStack`
  - 현재 Scene 관리
  - Scene 전환 관리
- `Scene`
  - 하나의 화면 또는 게임 상태 단위
  - 보통은 `World`를 소유
- `World`
  - 실제 게임 오브젝트를 소유
  - 나중에는 layer 를 지원할 예정
- `IGameObject`
  - `update()` / `draw()` 공통 계약

## 현재까지의 방향

현재 프로젝트는 이미 아래 방향으로 정리되고 있다.

- `IGameObject`를 도입해 공통 오브젝트 동작을 묶음
- `MainScene`을 도입해 게임 오브젝트와 입력 처리를 Scene 쪽으로 이동
- `GameMetrics`를 도입해 가상 좌표계 크기와 좌표 변환을 한곳에 모음
- 디버그 격자, FPS 텍스트, FPS 그래프는 `BuildConfig` 설정으로 제어하도록 분리

아직은 모든 코드가 `app` 모듈 안에 있지만, 프레임워크 성격의 코드를 나중에 `a2dg`로 옮기기 쉽도록 구조를 먼저 정리하는 중이다.

## 왜 이런 정리가 필요한가

이 프로젝트의 중요한 목적은 단순히 게임 하나를 완성하는 것이 아니다.

더 중요한 목적은:

- `GameView`를 바꾸지 않고도 Scene 을 교체할 수 있게 하고
- Android `View` 코드와 게임 로직을 분리하고
- 게임별 코드는 `app`에 남기고
- 공통 프레임워크 코드는 `a2dg`로 옮길 수 있게 만드는 것이다

즉 지금의 리팩터링은 기능 추가보다 구조 정리에 더 큰 의미가 있다.

## 앞으로의 단계

다음 단계로 생각하고 있는 내용은 대략 다음과 같다.

1. 프레임워크 쪽에 공통 `Scene` 추상 타입을 도입한다.
2. `GameView`가 `SceneStack`을 소유하도록 바꾼다.
3. `World`가 오브젝트를 소유하는 구조를 더 분명히 만든다.
4. `World` 안에 layer 지원을 추가한다.
5. 프레임워크 성격의 클래스를 `a2dg` 모듈로 이동한다.
6. `GameActivity`를 공통 부분과 게임 전용 부분으로 나누고, 공통 `BaseGameActivity`를 `a2dg`로 올린다.
7. `SceneStack`과 `Scene`에 scene 전환과 생명주기 API 를 추가하고 여러 scene 흐름을 테스트한다.
8. `BitmapPool`, `Sprite`, `JoyStick` 같은 공통 리소스·입력 요소를 프레임워크 쪽으로 확장한다.
9. `Bullet` 추가와 `ConcurrentModificationException` 해결을 통해 월드의 오브젝트 관리 구조를 보강한다.
10. 게임마다 다른 가상 좌표계, 실제 화면 크기 노출, package 정리까지 반영해 프레임워크 경계를 더 다듬는다.

## 커밋 체크리스트

아래 목록은 앞으로 여러 커밋에 걸쳐 하나씩 해결해 나갈 후보들이다.
진행하면서 항목을 지우거나 체크해 나갈 수 있도록 남겨 둔다.
`(추가)` 표시는 처음 계획에는 없었지만, 진행 중 필요해져 중간에 끼워 넣은 항목이다.

- [x] `Scene` 공통 추상 타입 도입
- [x] `GameView`가 구체 `MainScene` 대신 `Scene` 타입만 바라보도록 정리
- [x] `MainScene`이 `Scene`을 상속하도록 변경
- [x] `World` 도입
- [x] `MainScene`이 `World`를 소유하도록 변경
- [x] `Scene`의 기본 `update()` / `draw()`가 `world?.update()` / `world?.draw()`를 위임하도록 정리
- [x] `GameView`에 `SceneStack` 도입
- [x] 현재 Scene을 `SceneStack`의 top 으로 처리하도록 변경
- [x] `World`에 layer 구조 도입
- [x] layer 종류를 게임별로 다르게 둘 수 있는 방식 정리
- [x] `a2dg` 모듈 추가 (#11 에서 추가함)
- [x] 프레임워크 성격의 클래스들을 `a2dg`로 이동
- [x] `app`이 `a2dg`를 사용하도록 의존 방향 정리
- [x] `a2dg -> app` 의존이 생기지 않도록 구조 점검 (#11 준비단계에서 완료함)
- [x] `GameActivity`에서 재사용 가능한 부분과 게임 전용 부분을 분리
- [x] 공통 `GameActivity` 또는 `BaseGameActivity`를 `a2dg`로 이동
- [x] `app`은 루트 `Scene` 결정과 `BuildConfig` 주입만 맡도록 정리
- [x] `SceneStack`에 `push()` / `pop()` 외에 `change()` 추가
- [x] `Scene`에도 `push()` / `pop()` / `change()` 편의 함수 추가
- [x] `Scene`에 `onEnter()` / `onExit()` / `onPause()` / `onResume()` 생명주기 함수 추가
- [x] 여러 `Scene`이 `push()` / `pop()` 되는 흐름을 테스트
- [x] `Scene`에 `onBackPressed()`를 추가하고 기본 동작으로 `pop()` 처리
- [x] deprecated 된 `onBackPressed` 대신 `OnBackPressedCallback` 을 사용하는 것으로 변경
- [x] 마지막 `Scene`이 `pop()` 되면 `Activity` 종료
  - [x] `context as? Activity` 대신 `ContextWrapper` 체인을 따라가는 `activity` 프로퍼티로 변경
- [x] `Bitmap` 을 cache 하는 `BitmapPool` 도입
- [x] `Sprite` class 추가
- [x] 화면 해상도에 관계없이 `Bitmap` 을 원본 크기로 로드
- [x] 게임마다 가상 좌표계를 다르게 둘 수 있는 구조 도입
- [x] 게임 내에서 실제 화면 크기도 알 수 있도록 정리
- [x] `JoyStick` 클래스 추가
- [x] `JoyStick` 기본 그리기
- [x] `JoyStick` 이 `Touch Down` 인 동안만 보이게 처리
- [x] `JoyStick` 이 `+/- BG_RADIUS` 범위 안에서만 움직이게 처리
- [x] `JoyStick` 이 적당한 반지름의 원 안에서만 움직이게 처리
- [x] `JoyStick` 의 `angle` 및 `power` 계산 적용
- [x] `Fighter` 를 `JoyStick` 으로 움직이게 변경
- [ ] `JoyStick` 을 8방향 입력만 사용하는 경우 처리
- [ ] `power` 를 쓰지 않고 움직임 여부만 사용하는 경우 처리
- [x] `(추가)` 오브젝트가 현재 `Scene` 에 접근할 수 있도록 `GameContext` 에 `scene` 추가
- [x] `(추가)` `JoyStick` 을 `a2dg` 로 이동시키기 위해 app 에 의존하던 bitmap res id 삭제
- [x] `(추가)` `JoyStick` 의 위치와 크기를 app 에서 주입하도록 정리
- [x] `JoyStick` 을 `a2dg` 로 이동
- [x] `Bullet` 추가 (`ConcurrentModificationException` 발생)
- [x] `(추가)` FPS 디버그 정보 옆에 현재 오브젝트 수 표시
- [x] `(추가)` FPS 디버그 정보 아래에 레이어별 오브젝트 수 표시
- [x] `ConcurrentModificationException` 해결
  - [x] 방법 1: `Handler` 를 통해 나중에 삭제하기
  - [x] 방법 2: add/remove 요청을 등록해 두었다가 `update()` 가 끝나면 일괄 반영하기
  - [x] 방법 3: `update` loop 를 거꾸로 돌기
  - [x] 방법 4: `Iterator` 를 사용해 순회 중 안전하게 삭제하기
  - [x] 방법 5: 삭제 여부를 멤버 변수에 표시해 두고 나중에 정리하기
- [ ] `a2dg` package 정리
- [ ] `app` package 정리

## 메모

이 문서는 단순한 소개 문서라기보다, 앞으로 어떤 방향으로 구조를 바꿔 나갈지를 정리해 두는 로드맵 문서 역할도 함께 한다.
