# Class Slides

## 2026-03-16 Cards Requirements (PPT)

* 프로젝트 개요
  - 프로젝트명: Cards
  - 플랫폼: Android
  - 개발 언어: Kotlin
  - UI 방식: Android View 기반
  - 화면 방향: Portrait 모드만 지원

* 게임 개요
  - 간단한 카드 짝맞추기 게임
  - 카드를 뒤집어 같은 그림의 카드를 찾는다
  - 같은 카드 두 장을 찾으면 카드가 제거된다
  - 모든 카드를 제거하면 게임 종료

* 카드 구성
  - 트럼프 카드 이미지를 사용
  - 카드 종류: 8종
  - 각 카드: 2장씩
  - 총 카드 수: 16장
  - 게임 시작 시 모든 카드는 뒷면 상태

* 화면 구성
  - 상단 영역
    - 뒤집은 횟수 표시
    - Restart 버튼

  - 게임 영역
    - 4 × 4 카드 그리드
    - 총 16장의 카드 표시

* UI Wireframe
```
+---------------------------------------+
| Flip Count: 0              [Restart]  |
+---------------------------------------+
|         |         |         |         |
|  Card   |   Card  |   Card  |   Card  |
|         |         |         |         |
|         |         |         |         |
|  Card   |   Card  |   Card  |   Card  |
|         |         |         |         |
|         |         |         |         |
|  Card   |   Card  |   Card  |   Card  |
|         |         |         |         |
|         |         |         |         |
|  Card   |   Card  |   Card  |   Card  |
|         |         |         |         |
+---------------------------------------+
```

* 카드 동작 규칙
  - 카드를 누르면 카드가 열린다
    - 두 번째 카드를 누르면 규칙 적용

  - 같은 카드일 경우
    - 두 카드 제거

  - 다른 카드일 경우
    - 이전 카드 닫힘
    - 현재 카드는 열린 상태 유지

* 예외 처리
  - 이미 열려 있는 카드를 누르면
    - Toast 메시지 표시
    - 게임 상태 변경 없음

  - 이미 제거된 카드를 누르면
    - 아무 동작 없음

* Restart 및 게임 종료
  - Restart 버튼 클릭 시 확인 대화상자 표시
    - Yes → 게임 재시작
    - No → 게임 계속 진행

  - 모든 카드 제거 시 게임 종료
    - 재시작 여부를 묻는 대화상자 표시

* 개발 요구사항
  - Kotlin 사용
  - Android View 기반 UI
  - ViewBinding 사용
  - Portrait 모드만 지원
  - 한국어 / 영어 다국어 지원
  - 모든 텍스트는 String Resource로 관리

* 이 프로젝트를 통해 학습하는 기술
  - Android View 기반 UI 구성
  - ImageButton을 이용한 카드 UI 구현
  - View 클릭 이벤트 처리
  - 게임 상태 관리 로직 구현
  - Kotlin 컬렉션(List) 활용
  - View Tag를 이용한 데이터 연결
  - Toast 및 AlertDialog 사용
  - String Resource 기반 다국어 지원
  - Drawable 리소스 관리
  - ViewBinding 사용

## 2026-03-16
* `val` = immutable
* `var` = mutable
```Kotlin
val name = "Hello" 
name = "World" // ❌ error
// 한번 정한 값은 
// 바꿀 수 없다  
// 속도가 빠른 코드 생성
```

```Kotlin
var name = "Hello"
name = "World"
// 자유롭게
// 바꿀 수 있다

// 보호 코드 생성
```

* `Nullable` = null 일 수도 있다 
* `Non-nullable` = null 일 수 없다
  - 이게 더 중요한 개념

```Java
// Java (C++, ...)

int length;
if (name != null) {
    length = name.length();
} else {
    length = 0; 
}
```

```Kotlin
// Kotlin

val length = name?.length ?: 0;
```

* 누구세요?

<img src="https://media2.dev.to/cdn-cgi/image/width=800%2Cheight=%2Cfit=scale-down%2Cgravity=auto%2Cformat=auto/https%3A%2F%2Fdev-to-uploads.s3.amazonaws.com%2Fuploads%2Farticles%2Fa4jecfmsge2ofcfthkjt.jpg" />

  - Elvis Presley, 1935-1977

* elvis = null coalescing
* `??`  in C#, Javascript, Swift

* Data Class
```Kotlin
// Kotlin. var/val 
data class User(val name: String, val age: Integer) 
```

```Java
// Java
public class User {
    private final String name;
    private final Integer age;
    public User(String name, Integer age) {
        this.name = name; this.age = age;
    }
    // Getter, equals(), hashCode(), toString() 생략... 
} 
```

```Java
// Java record (java 14: 2020, java 16: 2021). all final
public record User(String name, Integer age) {} 
```

* Refactor:  
  - `FirstApp` ➔ `ImageSwitcher`

```Kotlin
private val catImageIds: [Int]
```

  - Image Resource ID 를 page 에 따라 선택
  - page 는 0-based? 1-based?

* Scalability
  - Image 5장 ➔ 6장

* 페이지 범위가 벗어나면?
  - 아무것도 안하기
  - 순환하기 
  - 메시지 보여주기 
  - 못 가게 하기

* Selector drawable  
  - `normal`, `pressed`, `disabled`, `focused`, …

* Page 번호 유지하기
  - savedInstanceState
  - ViewModel
  - Storage(`SharedPreferences`)


## 2026-03-09
* SmartPhone Game Programming
  - Android
  - #2, 2026-03-09
* Kotlin ??
* Tools: 
  - Android Studio
    - JRE, JDK, Kotlin
    - AVD: Binary/Display
* Procedural, OOP, Functional, Multi-threading
  - Abstraction: Procedure vs Data
* val vs var
  - null safety
  `?`, `??`, `!`, `!!`
* OOP: Inheritance, Encapsulation, Polymorphism
  - Interface(Protocol)
* Type, Concept, Class, Abstract
  - vs Object, Instance, Concrete, (Variable)
* Function, Method, Message, Operation, Behavior 
  - vs Attribute, Property, State, Field, Variable
* Sample App:
  - Compose vs View based
* App ID
  - Reverse domain
  - MinSDK
  - Gradle, Kotlin DSL
* Android Studio 
  - Editors/Views
  - Area

## 2026-03-03
* SmartPhone Game Programming
  - Android
  - 김기용/게임공학부
* Tools: Android Studio by JetBrains
* Why, What, How
  - AI Assistant/Agent
* Procedural, OOP, Functional, Multi-threading
* Java? Kotlin?
* Homeworks, Requirements
