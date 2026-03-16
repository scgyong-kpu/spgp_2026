# Class Slides

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
