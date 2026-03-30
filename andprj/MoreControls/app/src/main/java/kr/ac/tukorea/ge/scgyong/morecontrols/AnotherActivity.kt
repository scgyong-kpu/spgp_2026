package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AnotherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이번 단계에서는 XML inflation 대신 코드에서 View 객체를 직접 만들어 붙여 본다.
        // 그래서 BallView 는 Context 생성자 하나만 있어도 된다.
        val ballView = BallView(this)
        setContentView(ballView)
    }
}
