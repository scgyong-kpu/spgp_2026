# Homeworks

## 2026-03-16 조사: Kotlin, Design Pattern, 2D, ...

* Kotlin 관련 다음 항목들을 조사하여 정리하라
  - when 표현식
    - Java (C/C++) 의 switch 와의 차이
  - smart cast
    - `if (obj is Fly)` 의 의미 및 효과
  - ranges
    - `..`, `..<`, `until`, `downTo`
  - functions
    - default parameter
      - C++/Java Overloading 과의 차이
    - named argument
    - string template (interpolation)

* 수업 시간에 다루게 되는 다음 Design Pattern 들과 기타 기법, 각각의 Kotlin 문법에서 활용법
  - Singleton Pattern
  - Factory Pattern
  - Builder Pattern
  - Lazy Initialization (by lazy, lateinit)
  - Method Chaining (Dot Chain, Safe Call Chain)
  - Transformation (Matrix 를 이용한 2차원 좌표 변환)
  - Recursion (Recursive Call)
  - Padding vs Margin (Android 와 CSS 에서)

각 항목별로 정의, 사용법, 일반 예제, 장점, 주의사항 등에 대해 여러 검색결과를 취합하여 정리해 보아라


## 2026-03-11 실습: Image Switcher

수업 시간에 소개했던 Image Switcher 를 완성하여 제출한다. 제출하는 항목은 다음과 같다

* 첨부 제출
  - MainActivity.kt
  - activity_main.xml
* 화면 캡처 과제 본문 삽입
  - 에뮬레이터 실행 모습 2장 이상 (너무 크지 않게 적당히 줄여주세요)
  - Landscape 캡처 포함
  - 에뮬레이터가 아닌 실기기에서 캡쳐해도 됩니다

필요 요구사항 및 옵션 사항은 다음과 같다 

- 고양이 혹은 원하는 다른 그림을 5장 이상 준비해 두도록 한다
  - Portrait 및 Landscape 에서 하나의 layout xml 만으로 아래 첨부와 같은 모양이 나올 수 있도록 한다.
    - 아래와 같이 ImageButton 에 이미지를 구해서 사용해도 좋다. 그냥 Button 에 Previous/Next 로 표시해도 좋다
    - Selector 에 대해 알아보고 사용해도 좋다.
      - pressed state 에 따라 다른 이미지 사용.
      - enabled 에 따라서도 다른 이미지 사용 가능.
  - 1페이지에서 이전 버튼을 눌렀을 때에 대한 처리를 한다. 옵션은 다음 중에서 어떤 것을 썼는지 과제 본문에 적는다
    - 아무 일도 안 일어난다
    - 5페이지로 넘어간다
    - 버튼을 누를 수 없다 (disabled)
    - Alert 나 Toast 가 나타난다
  - 이미지가 추가되어 6장이 될 수도 있는 것에 대한 대비를 하도록 하며, 어떤 방법을 썼는지 과제 본문에 적는다.
  - ViewBinding 을 켜고 사용한다.
    - View 의 ID 는 snake_case 또는 lowerCamelCase 를 사용한다
    - 수업에서 보여 준 Commit [ [ViewBinding 을 활성화하고](https://github.com/scgyong-kpu/spgp_2026/commit/d6cf3796eea64a32162811e5ac03aed50867ff1a) ] 을 참고한다

## 2026-03-11 조사: Kotlin, XML/JSON, Layouts, Widgets

* Kotlin 관련 다음 항목들을 조사하여 정리하라
  - Data Class
  - Property 
    - Gettter/Setter 관련
    - Kotlin 에서 Field 와의 차이. 
  - `init` block
  - Companion object
  - Object Singleton
  - Top Level Funtion
    - vs Java

* 다음 영상에서 XML, JSON 관련 내용을 정리하라. YAML 은 필수 사항은 아니다.
  - https://www.youtube.com/watch?v=55FrHTNjTCc

* Android 화면 개발시 사용되는 Layout 들에 대한 특징 및 속성에 대해 조사하라. 아래 3번 항목 외에, 각 Layout 아래에 있는 Widget 들이 가져야 하는 `LayoutParams` 도 함께 알아보도록 하라.
  - `FrameLayout`
  - `LinearLayout`
  - `RelativeLayout`
  - `ConstraintLayout`

* Layout XML 속성 중 다음 항목에 대해 조사하라
  - `layout_width`
  - `layout_height`
  - `layout_weight`
  - `layout_gravity`
* Android 화면 구성시 사용되는 Widget 들에 대하여 조사하라. 목적 및 사용법을 포함하도록 한다. 각 항목별로 대표적인 Property(속성), Operation(함수), Event(함수 설정) 에 대해 조사하라. 생긴 모양을 소개하기위해 이미지를 과제 본문에 삽입하라
  - `TextView`
  - `ImageView`
  - `EditText`
  - `Button`
  - `CheckBox`
  - `RadioButton`
  - `Switch`
  - `ToggleButton`
  - `Spinner`
  - `ListView`
  - `SeekBar`

* 안드로이드 layout xml 내에서 사용하는 단위(`px`, `dp`, `sp` 등) 은 어떤 것들이 있는지 간단히 정리하라. 
