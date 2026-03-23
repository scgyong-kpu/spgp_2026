package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Context
import android.util.AttributeSet
import android.view.View

class BallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr)

// Java 였다면 아래와 같이 3개의 Constructor overload 가 있어야 한다
// 하지만 Kotlin에서는 @JvmOverloads 어노테이션을 사용하여 하나의 생성자로 모든 경우를 처리할 수 있다.
// public class BallView extends View {
//     public BallView(Context context) {
//         super(context);
//     }
//
//     public BallView(Context context, AttributeSet attrs) {
//         super(context, attrs);
//     }
//
//     public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
//         super(context, attrs, defStyleAttr);
//     }
// },