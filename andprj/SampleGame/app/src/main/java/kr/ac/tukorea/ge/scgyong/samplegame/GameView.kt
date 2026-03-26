package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.util.AttributeSet
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    // Java 였다면 보통 아래 3개의 overload 를 직접 적었을 것이다.
    // GameView(Context context)
    // GameView(Context context, AttributeSet attrs)
    // GameView(Context context, AttributeSet attrs, int defStyleAttr)
    //
    // Kotlin 에서는 @JvmOverloads 를 쓰면 이런 constructor overload 들을 자동으로 만들어 준다.
    // XML inflation 까지 고려한 일반적인 Custom View 생성자 형태이다.
}
