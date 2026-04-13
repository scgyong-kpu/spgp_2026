
# Mid Exam 2026

## Kotlin 언어

1. Kotlin의 `object` 선언은 싱글턴 객체를 정의할 때 사용할 수 있다.
1. `companion object` 는 클래스 이름으로 접근하는 멤버를 둘 때 사용할 수 있다.
1. 하나의 클래스 안에 `init` 블록을 두 개 이상 둘 수 있다.
1. 생성자 파라미터에 `val` 또는 `var` 를 붙이면 프로퍼티가 된다.
1. Kotlin 프로퍼티는 getter/setter 를 가질 수 있다.
1. backing field 와 프로퍼티는 구분되는 개념이다.
1. `data class` 는 데이터를 담는 객체를 간결하게 정의할 때 유용하다.
1. named argument 는 함수 호출 시 각 인자의 의미를 더 분명히 드러낸다.
1. default parameter 를 사용하면 비슷한 오버로딩 함수 수를 줄일 수 있다.
1. string template 는 문자열 안에 변수나 식의 값을 쉽게 삽입하는 문법이다.
1. `when` 은 문장뿐 아니라 식으로도 사용할 수 있다.
1. `if (obj is Fly)` 처럼 검사한 뒤에는 조건이 참인 분기 안에서 smart cast 가 가능할 수 있다.
1. `as?` 는 캐스팅에 실패하면 예외 대신 `null` 을 반환한다.
1. Elvis 연산자 `?:` 는 왼쪽 값이 `null` 일 때 오른쪽 값을 사용한다.
1. `apply` 는 객체 초기화 코드를 묶고 그 객체 자신을 반환받을 때 자주 쓴다.
1. `also` 는 일반적으로 람다 결과가 아니라 원래 객체 자신을 반환한다.
1. `let` 은 safe call(`?.`)과 함께 자주 사용된다.
1. extension function 은 기존 클래스를 수정하지 않고 함수 형태의 기능을 추가하는 방식이다.
1. extension property 자체는 독립적인 backing field 를 가질 수 없다.
1. Android 코드에서도 extension function 을 자주 활용할 수 있다.
1. `fun interface` 는 추상 메서드가 하나인 인터페이스를 함수형처럼 다루기 위한 문법이다.
1. `..` 는 양 끝값을 포함하는 범위를 만든다.
1. `until` 은 마지막 값을 포함하지 않는 범위를 만든다.
1. `downTo` 는 큰 값에서 작은 값으로 감소하는 범위를 만들 때 사용한다.
1. `by lazy` 는 값이 실제로 필요해질 때 초기화하는 방식이다.
1. `lateinit` 은 기본형 프로퍼티에는 사용할 수 없다.
1. safe call chain 은 `null` 검사 코드를 줄이는 데 도움이 된다.
1. method chaining 은 호출 결과를 이어서 다음 호출에 사용하는 스타일이다.
1. Builder pattern 은 설정할 항목이 많은 객체를 단계적으로 구성할 때 유용하다.
1. Factory pattern 은 생성 방법을 감추고 호출부를 단순하게 만들 수 있다.
1. Singleton pattern 은 전역적으로 하나의 인스턴스만 쓰고 싶은 경우에 적합하다.
1. 재귀 호출은 종료 조건이 반드시 필요하다.
1. `Class<*>` 는 임의 타입의 `Class` 객체를 참조할 수 있는 선언이다.
1. `inline` 함수는 람다 호출과 관련된 오버헤드를 줄이는 데 도움이 될 수 있다.

## Android

1. 문자열을 `strings.xml` 로 분리하면 지역화에 유리하다.
1. 일반적인 화면 요소 크기는 `dp`, 글자 크기는 `sp` 를 쓰는 것이 보통이다.
1. 일반적인 UI 크기 지정에는 `px` 보다 `dp` 나 `sp` 가 더 적합한 경우가 많다.
1. `LinearLayout` 은 자식 View 를 한 방향으로 배치하기 좋은 레이아웃이다.
1. `FrameLayout` 은 여러 View 를 겹쳐 배치하기에 적합하다.
1. `ConstraintLayout` 은 제약 조건을 이용해 유연한 배치를 만들 수 있다.
1. `layout_weight` 는 주로 `LinearLayout` 에서 공간 배분에 사용된다.
1. `layout_gravity` 는 부모 안에서 자식 `View` 의 배치 위치에 영향을 준다.
1. `padding` 은 `View` 내부 여백이고, `margin` 은 `View` 바깥 여백이다.
1. `TextView` 는 텍스트를 표시하는 위젯이다.
1. `ImageView` 는 이미지를 표시하는 위젯이다.
1. `EditText` 는 사용자 입력을 받는 위젯이다.
1. `SeekBar` 는 일정 범위의 값을 슬라이더 형태로 입력받을 수 있다.
1. `Switch` 와 `CheckBox` 는 둘 다 `on`/`off` 상태 입력에 사용할 수 있다.
1. `Spinner` 는 여러 선택지 중 하나를 고를 수 있게 해 준다.
1. `ListView` 는 목록 형태 데이터를 표시하는 위젯이다.
1. 버튼 클릭 이벤트는 Kotlin 에서 람다로 연결할 수 있다.
1. XML 의 `android:onClick` 으로 연결되는 함수는 정해진 시그니처를 만족해야 한다.
1. ViewBinding 을 사용하면 `findViewById()` 사용을 줄일 수 있다.
1. ViewBinding 은 gradle 설정에서 활성화해야 사용할 수 있다.
1. `Activity` 는 화면 회전 등의 configuration change 시 다시 생성될 수 있다.
1. `savedInstanceState` 는 일시적인 UI 상태를 복원하는 데 사용할 수 있다.
1. `ViewModel` 은 configuration change 중 상태 유지에 도움이 된다.
1. `SharedPreferences` 는 앱을 다시 실행한 뒤에도 남는 간단한 데이터를 저장할 수 있다.
1. `Toast` 는 짧은 사용자 피드백을 보여줄 때 적합하다.
1. `AlertDialog.Builder` 는 확인/취소 대화상자를 구성할 때 자주 사용된다.
1. explicit intent 는 목적 `Activity` 나 컴포넌트를 명시하는 방식이다.
1. implicit intent 는 조건에 맞는 다른 앱이나 컴포넌트가 처리할 수 있다.
1. Custom View 는 `onDraw()` 를 `override` 해서 직접 그릴 수 있다.
1. Custom View 에서 크기 의존적인 계산은 `onSizeChanged()` 에서 준비하기 좋다.
1. Custom View 를 만들 때 XML custom attribute 로 동작이나 모양을 조절할 수 있다.
1. `Canvas` 의 `translate`, `scale`, `rotate` 는 좌표계를 변환하는 방식이다.
1. Android Studio 의 프로파일러는 메모리 할당이나 GC 흐름을 관찰하는 데 도움이 된다.

## Game Programming

1. 게임 루프는 보통 입력, 업데이트, 그리기의 반복으로 설명할 수 있다.
1. Android View 기반 게임에서도 프레임 콜백이나 `invalidate()` 를 이용해 반복 갱신이 가능하다.
1. 가상 좌표계를 두면 기기 해상도 차이와 무관하게 게임 로직을 작성하기 쉬워진다.
1. 실제 터치 좌표를 게임 좌표로 바꾸려면 좌표 변환을 고려해야 한다.
1. `Scene` 은 하나의 게임 상태를 나타내는 단위로 사용할 수 있다.
1. `SceneStack` 은 장면 전환을 구조적으로 관리하는 데 도움이 된다.
1. `World` 를 레이어별로 나누면 업데이트와 그리기 순서 관리가 쉬워진다.
1. `IGameObject` 인터페이스로 `update()` 와 `draw()` 를 통일하면 관리가 쉬워진다.
1. `dstRect` 를 불필요하게 매 프레임 재계산하면 낭비가 될 수 있다.
1. 위치나 크기가 바뀌는 시점에 `syncDstRect()` 를 호출하도록 설계할 수 있다.
1. 충돌 판정은 처음에는 bounding box 교차 검사로 시작하는 경우가 많다.
1. `collisionRect` 를 시각적 이미지보다 조금 줄이면 체감상 더 자연스러운 충돌이 될 수 있다.
1. 디버그 단계에서 충돌 박스를 화면에 그려 보면 충돌 판정을 확인하기 쉽다.
1. 리스트를 순회하면서 원소를 제거하면 `ConcurrentModificationException` 이 발생할 수 있다.
1. 뒤에서 앞으로 순회하면 순회 중 삭제를 다루기 쉬운 경우가 많다.
1. 뒤에서 앞으로 순회하는 도우미 함수는 순회 중 삭제 문제를 줄이는 데 사용할 수 있다.
1. `RectF.intersects()` 같은 함수로 두 사각형의 충돌을 검사할 수 있다.
1. `IBoxCollidable` 인터페이스는 충돌 사각형 프로퍼티를 공통화하는 데 적합하다.
1. 확장 함수로 `collidesWith()` 같은 공통 충돌 판정 로직을 분리할 수 있다.
1. `Enemy` 에 life 를 두면 총알 한 발에 무조건 제거되지 않게 만들 수 있다.
1. 충돌 시 `Enemy` 를 바로 삭제하는 대신 life 를 감소시키는 구조는 확장성에 유리하다.
1. `Gauge` 는 progress 값을 draw 시점에 받아 사용하는 상태 없는 도구로 설계할 수 있다.
1. `Label` 이나 숫자 표시 도구를 공통 모듈로 분리하면 재사용성이 좋아진다.
1. 점수의 실제 값과 화면에 보이는 값을 분리하면 증가 애니메이션을 만들 수 있다.
1. Object Pooling 은 객체 재사용을 통해 할당과 GC 부담을 줄이려는 기법이다.
1. recycle bin 에서 객체를 꺼내고 없으면 새로 만드는 방식은 pooling 의 한 형태다.
1. `private` 생성자와 factory 함수 조합은 생성 경로를 통제하는 데 도움이 된다.
1. factory 를 함께 쓰면 호출부가 재활용 여부를 몰라도 되게 설계할 수 있다.
1. pause 되었다가 resume 된 뒤 프레임 시간 차이를 그대로 누적하면 게임이 순간이동하듯 보일 수 있다.
1. pause/resume 시 시간 기준을 다시 잡아 주면 큰 frame time 문제를 줄일 수 있다.
1. 세로 스크롤 배경은 같은 이미지를 여러 장 이어 배치한 뒤 오프셋만 바꿔 구현할 수 있다.
1. `Scene` 에 `clipRect` 를 두면 가상 좌표계 범위 안에서만 그리도록 제한할 수 있다.
1. 게임의 hot path 에서는 `iterator`, 임시 객체, 로그 문자열 생성도 할당 원인이 될 수 있다.

