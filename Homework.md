# Homeworks

## 2026-05-07 텀 프로젝트: 2차 발표

기말 프로젝트를 위한 2차 발표를 준비하고 과제 본문에는 다음항목을 기재합니다. 
URL 은 링크 형태로 하여 클릭하여 볼 수 있도록 해 주세요.

* 프로젝트 제목
* README.md 에 대한 링크
* 프로젝트 Git Repository 에 대한 링크
* 2차 발표
  * 영상 자료에 대한 링크
* 1차 발표
  * 영상 자료에 대한 링크
  * 1차 발표때 버전의 README.md 에 대한 링크


발표 자료의 내용은 모두 README.md 에 포함되어야 하고 별도의 PPT 는 작성하지 마세요.
발표 자료에 포함되어야 하는 사항은 다음과 같습니다. 

- 게임에 대한 간단한 소개 (1차 발표때 이미 소개했으므로 떠올릴 정도만 해도 좋음)
- 현재까지의 진행 상황 (항목별 진행 정도를 %로 표시할 것)
- git commit 을 얼마나 자주 했는지 알 수 있는 자료 (github-insights-commits 포함)
  - 주별 commit 수를 주차별 표로 만들어 포함할 것
- 목표가 변경되었다면 변경된 내용과 이유 (합당하지 않는 경우 감점)
- Activity 구성
- Scene 구성 및 전환 관계
- MainScene 에 등장하는 game object 들에 대하여 다음 사항 포함
  - class 구성 정보 (그림 구성, 동작 구성 등)
  - 상호작용 정보
  - 게임 내에서 이 class 가 책임지는 핵심 코드에 대한 간단한 설명
  - UX 진행 방법
- 글자가 뚜렷하게 잘 보여야 하고 음성도 똑똑히 잘 나와야 함
- 구현하면서 특히 어려웠던/어려운 부분, 수업에서 추가로 다루었으면 하는 것 있으면 포함해도 좋습니다.

## 2026-04-15 조사: Kotlin/Android

* Kotlin Collections
  * `List` vs `MutableList`
  * `Set`
  * `Map`
* Android
  * UI
    * `Activity` & Life-cycle
    * `View` & Life-cycle
  * Event Model
    * Event driven programming
    * Listener pattern
    * Android Touch Event
  * `asset` vs `res`
* Game Engine
  * Game Loop
  * `update` / `draw`
  * Delta time
  * Parallax Scrolling

## 2026-04-06 조사: Kotlin

* Kotlin object syntax
  * `apply`
  * `also`
  * `let`
* Extension
  * Extension function
  * Extension property
  * Android 에서 Extension 사용 예
* Type system
  * `is`, `as`, `as?`
  * Elvis `?:` (이미 했지만 한번더)
  * `fun interface`
* `ConcurrentModificationException`
  * 의미
  * 언제 주로 발생하는지
  * 어떻게 해결하는지
* Android
  * `Activity` Life-Cycle
  * Profiling (Debugging)
    * 의미
    * 방법
* Object Pooling (Recycle)

## 2026-03-27 텀프로젝트: 1차 발표

발표 자료를 제작하고 과제 본문에는 다음 항목을 기재합니다. 링크 형태로 하여 클릭하여 볼 수 있도록 해 주세요.

* 프로젝트 제목
* 발표 영상 자료에 대한 링크
* 프로젝트 Git Repository 에 대한 링크
* README.md 에 대한 링크

개발 범위
* 수업 git repo 의 readme 에 있는, `이번학기에서 다루게 되는 것들`을 참고하여 개발 범위를 정해주세요
* 이번학기에 만들어 가는 framework 를 사용하는 것을 원칙으로 합니다. 안 사용해야 하는 사람은 개별면담 해주세요.

발표 자료는 PPT 로 만들지 않고, 다음 모든 내용이 README.md 에 포함될 수 있도록 해주세요.
* 게임 컨셉: High Concept 및 핵심 메카닉을 명확히 제시
* 개발 범위: 개발 주요 요소를 정량적으로 제시
* 예상 게임 실행 흐름
  * 게임 화면 스크린샷을 이용하여, 게임 실행 흐름 제시
  * 다른 게임 화면 이용 또는 간단히 스케치한 그림
  * 게임이 어떤 식으로 진행되는지 직관적으로 알 수 있도록 구성
* 개발 일정: 4월 6일에 시작하는 주를 1주차로 하여 8주간의 주단위 상세 개발 일정을 제시.
* 발표 동영상의 길이는 1분 30초가 되게 해 주세요. 1분 20초보다 짧거나 1분 40초보다 길면 감점합니다.
* 발표 내용은 README.md 에 모두 포함되어야 합니다.

발표를 하게 되면 과제 올린 시간의 역순으로 진행합니다. 발표를 진행하지 않을 수도 있습니다.

## 2026-03-26 설문: 1개월차 개발 소회

1달간 안드로이드 개발을 진행한 데 대한 피드백을 정리한다. 각 항목에 숫자만 적지 말고 사례를 들어 자세한 의견 개진 바람

* 난이도는 어떠한가? 
  * `0`=껌, 
  * `10`=도저히모르겠음. 
* 기대했던 것 대비 어떠한가?
  * `0`=전혀 다른 것을 하고 있다.
  * `10`=완전히 원했던 것 그대로 하고 있다.
* Kotlin 으로 선택한 것에 대하여
  * `0`=역시 Java 가 편했을 듯
  * `10`=Kotlin 완전 좋아
* 수업 진행 방식에 대하여
  * `0`=너무 많은 내용을 해서 따라갈 수가 없어
  * `10`=나님이 알아듣기 딱좋아
* 특이하거나 재미있었던 경험
  * Kotlin 의 이런 문법은 흥미로웠다
  * 안드로이드 개발에 이런 것은 흥미로웠다
  * 수업에서 소개한 이런 기법은 흥미로웠다
* 사용중인 AI Tool
  * 사용한 툴 종류별 특징이나 장단점 비교

## 2026-03-25 실습: Custom View Implementation

이전 과제에서 정했던 Spec 에 준하여 Custom View 를 구현하여 제출한다. 제출 항목은 다음과 같다

* 본문 제출
  * 구현 내용
    * Spec 에 대한 간단한 소개
      * 이전 과제의 구체적인 내용을 반복할 필요는 없다
    * 이전 과제와 연계하여 어느 부분을 구현하고 어느 부분을 구현하지 못했는지 명시
  * sample_layout 실행화면 캡처
    * 첨부제출한 layout xml 이 표시된 모양을 design preivew 혹은 emulator 캡처
* 첨부 제출
  * `*View.kt`
    * Custom View 를 구현한 Kotlin 파일
    * padding 은 적용되도록 할 것 (4방향 모두)
  * `sample_layout.xml`
    * 위 Custom View 가 2개 이상 포함되어 있는 layout
    * 가로세로 비율이 다르게 표현될 것
    * 속성이 여러가지인 경우 속성도 다양하면 좋음

## 2026-03-21 실습: 디자인 요구 사항 문서 제작
다음 시간에 Android 의 View 를 상속하여 Custom View 를 만들 예정이다. 이를 위해 내가 만들 View 의 Spec 을 정해 과제 본문으로 작성, 제출한다.

* View 의 이름: 예 - `MyView` `CircleView` `SmileyView` `PieProgress` 등
* View 를 그리는 방법에 대한 구체적인 방법: 크기에 따라 달라지도록 고안할것
    * 예1
      * View 한가운데에 View 높이의 `1/10` 크기로 검정색 원을 색칠하여 그린다
      * View 의 `1/4` 크기로 가운데에 `5px` 두께의 파란색 선으로 사각형을 그린다
      * View `x` 좌표로는 가운데, `y` 좌표로는 위에서 높이 `1/3` 위치에 가운데 정렬로 `"Hello"` 라고 검정색 글자로 적는다
    * 예2
      * 가로와 세로 중 짧은 것의 절반을 `r` 이라 하고 반지름이 `r` 인 원을 그려 얼굴로 삼는다. View 의 중심은 `cx`, `cy` 이다.
      * `cx - r/3`, `cy - r/4` 위치에 왼쪽 눈을, `cx + r/3`, `cy - r/4` 위치에 오른쪽 눈을 그린다. 눈의 반지름은 `r/4` 이다.
      * cx - r/2, cy + r/2 부터 cx + r/2, cy + r/2 까지 시작각도 15도에서 165도까지의 호를 그려 입으로 한다
      * 파란색 두께 `5` 의 선으로 그린다.
    * 그림판에서 마우스로 대충 끄적여서 과제 본문에 붙여넣어라. 이때 2개 이상의 크기와 비율에 대해 예를 들어라
* View 에게 XML attribute 로 줄 수 있는 항목들을 나열한다. 이름도 정해 준다
  * 예1
    * `centerCircleColor`: 가운데에 그릴 원의 색깔 (default: 검정색)
    * `centerCircleSize`: 가운데에 그릴 원의 크기 (default: 1/10)
    * `squareStrokeWidth`: `1/4` 크기의 사각형의 두께
    * `squareStrokeColor`: `1/4` 크기의 사각형의 선 색깔
    * `greetings`: `Hello` 대신 적을 문자열
* (Optional) View 에서 발생할 수 있는 Event. 구현하는 방법은 몰라도 된다. 어렵다면 고려하지 않아도 된다.
    * 사용자가 square 부분을 touch-down 했다
    * square 부분을 touch-down 한 지 2초의 시간이 지났다

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
