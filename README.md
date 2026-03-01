# spgp_2026
TUKorea Dept. of Game Engineering. Smartphone Game Programming Lecture Reources

아래는 작년까지 진행했던 Java 기준으로 작성되어 있습니다. 설문조사 후 혹시 Kotlin 으로 진행하게 되면 알맞게 바뀔 수 있습니다.

## 다루는 프로그램들
* SampleJavaApp
  * Command
    * App -> UI Component
    * text, color 등 속성 변경
  * Event Handling
    * UI Component -> App
    * 버튼 클릭 등 사용자 동작에 의한 함수 호출
    * 4 가지 방법
      * interface 연결 3가지 방법
        - `this` 가 구현하는 방법 - "버튼 클릭되면 나에게 알려줘"
        - Member Variable 로 지정하는 방법 - "내 멤버에게 알려줘"
        - Anonymous interface implementation - "즉석에서 객체를 만들어 줄테이 얘한테 알려줘"
      * onclick 의 경우 XML 에 넣을 수 있음
    * 같은 함수가 호출되면 ID 로 분기하는 방법도 있지만, 동작이 다르다면 다른 함수가 호출되게 하자.
  * `findViewById()` 함수 호출은 최초 한번만 하자.
    - ViewBinding 을 사용하게 되면 프로그래머가 직접 할 필요가 없긴 하다.
* ImageSwitcher
  * TextView, Button, ImageButton, LinearLayout
  * Selector
    * `pressed` state 에 따라 다른 이미지 사용.
    * `enabled` 에 따라서도 다른 이미지 사용 가능.
  * dimens, colors, strings 등 다른 "값" Resource 들을 참조하도록.
  * ViewBinding 을 사용할 때와의 차이.
  * `LinearLayout` 사용시 `layout_weight` 항목
* Cards (Memory Game)
  * `Style` 사용하기. Android Studio 의 Refactor 이용 가능.
  * 16장 카드 모두 같은 함수로 `onclick` 연결하고 ID 로 구분.
  * `ImageButton` 에 `tag` 를 `Integer` 로 연결. 열렸을 때 보여줄 이미지의 Resource ID 를 넣어 둔다.
  * Shuffle
    * Language Runtime 에서 지원하는 경우도 있다 (Python 등)
    * `Collections.shuffle()` 을 쓸 수도 있지만, Integer[] 및 List<Integer> 로 변환되어야 하므로 불편하다.
    * Fisher-Yates 알고리즘을 사용하여 배열을 무작위로 섞을 수 있다. 알고리즘의 각 단계는 다음과 같습니다:
      - 배열의 마지막 인덱스에서 시작하여 첫 번째 인덱스까지 반복합니다.
      - 현재 인덱스 i와 0부터 i까지의 무작위 인덱스 j를 선택합니다.
      - 배열의 i번째 요소와 j번째 요소를 교환합니다.
  * `Visibility` : `Visible`, `Invisible`, `Gone`
  * `AlertDialog` - Builder pattern 을 사용하여 생성.
    * `AlertDialog.Builder`
    * 객체의 일관성 보장
    * 불변 객체 생성
    * dot chaining (method chaining) 을 통한 설정
  * `configChanges="orientation|screenSize"`
    * 화면 가로/세로 기울기를 변경해도 Activity 가 재시작되지 않음.
  * `screenOrientation="portrait"`
    * Activity 가 세로 방향만 지원하도록 함.
  * Color Table, Style, String Resource
    * 다국어 지원. `i18n` = Internationalization.
      - 참고: `l18n` = Localization, `a11y` = Accessibility, `m17n` = Multilingualization, `s12n` = Serialization
    * 언어 지원 뿐 아니라 다양한 기기 지원을 위해서도 여러 셋의 리소스를 준비할 수 있음.
  * `Toast`
    * `AlertDialog` 는 사용자가 반드시 버튼을 눌러야 하므로 방해가 될 수 있다. `Toast` 는 그럴 필요가 없는 메시지를 보여준다.
    * Factory pattern 사용.
* MoreControls
  * ViewBinding 의 본격적인 사용
    * `build.gradle.kts` 파일에 지정
    * Sync Now -> Build -> binding java 파일 자동 생성
  * CheckBox, RadioButton, Switch
  * EditText, TextWatcher
    * `Button` 처럼 단순이 눌렸다 수준이 아닌 다양한 이벤트를 전달받아야 할 필요성이 있음
    * `TextWatcher` interface 를 구현하여 전달받음. interface 구현 전달에는 3가지 방법이 있음.
    * `hint` 는 iphone 이나 html5 에서는 `placeholder` 라는 이름으로 부른다.
  * Seekbar
    * `interface SeekBar.OnSeekBarChangeListener` 를 구현, 지정하여 값 변화를 전달받는다.
  * CustomView
    * Android Studio 로 New-UIComponent-CustomView 로 만들면 우리한테는 필요없는 것들이 좀 생긴다.
      * `attrs_custom_view.xml` - XML 로 속성을 설정할 수 있도록 정의
      * `sample_custom_view.xml` - XML 로 만들때의 예제를 만들어 준다.
      * `values/styles.xml`, `values-night/styles.xml` - Style 예제를 두 개 만들어 준다.
      * CustomView.java 의 각종 attribute getter/setter 및 `onDraw` 구현들
    * New-JavaClass 로 만들어도 된다. Alt+Insert 를 활용하여 Constructor 를 만들어 본다.
      * `View(Context)` - 코드로 생성할 때
      * `View(Context, AttributeSet)` - XML 로부터 만들어질 때
      * `View(Context, AttributeSet, int defStyle) - XML/Style 로부터 만들어질 때
    * 한 번만 생성하면 되는 `Paint`, `Rect` 객체 등을 `onDraw()` 등에서 반복 생성하지 않도록 한다.
    * Lazy Initialization 기법 이해하기.
    * Stroke, Fill
    * Width, Cap, Join
    * `onSizeChanged()` 함수를 통해 크기 변화를 알 수 있지만, 생성시에 크기는 `0,0` 이다.
    * Recursive 하게 Smiley 를 그리는 것을 해 보았다
      * Canvas transform 을 적용하여 변수 없이 진행.
  * `margin` 과 `padding` 의 차이 이해하기. html5/css 등 다른 시스템에서도 동일한 용어를 사용한다.
  * Activity 가 다른 Activity 를 호출할 때는 `Intent` 를 사용한다.
    * 다른 process 의 Activity 일 수도 있다.
  * ActionBar, StatusBar, NavigationBar 를 없앤다
* CustomView based Game App
  * postDelayed(), Choreographer
  * Frame Time, FPS
  * Move based time
* FighterPlane (First 2D Game)
  * GameObject class/interface, Sprite
  * MainGame class
  * Touch Event handling
  * Frame Animation
  * Game Framework
* DragonFlight
  * EnemyGenerator
  * Object Lifecycle Management (recycle)
  * GameObject Layering
  * Score / Font Drawing
  * Background Layering (Parallex Scroll)
* CookieRun
  * HorzScroll BG / VertScroll BG
  * Image Resource Sheet
  * Stage data file
  * Collision Check / Collision Handling
  * Multiple Scene / Transparent Scene
* Path Draw / Path Animation
  * `SmoothingPath` App
  * `PathView` 생성.
  * Touch Event 를 통해 점 추가, 점들을 선으로 연결
  * 점 갯수를 `TextView` 에 표시하고 `Button` 누를시 0 으로 초기화
  * Path close 여부를 `Checkbox` 로 지정
  * Start `Button` 을 누르면 비행기가 Path 를 따라 진행. 위치 및 각도 적용
  * Bezier Curve (Cubic) 적용
* Tiled MapEditor
  * DragonFlight rework
* TuDefence
  * Tile based Tower Defence Game
* TapTU
  * Rhythem Action Game

## Schedule
* 1차 발표 : 5주차
* 2차 발표 : 10주차
* 2차 발표 : 15주차
