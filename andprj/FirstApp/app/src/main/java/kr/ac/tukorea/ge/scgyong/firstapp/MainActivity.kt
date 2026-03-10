package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var lowerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 액티비티가 사용할 화면 레이아웃을 연결한다.
        setContentView(R.layout.activity_main)

        // 두 번째 TextView는 여러 곳에서 사용하므로 한 번만 찾아서 멤버에 저장한다.
        lowerTextView = findViewById(R.id.lower_text_view)

        // 시스템 바 영역만큼 패딩을 적용해서 상태바, 내비게이션 바와 겹치지 않게 한다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 첫 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnFirstButton(view: View) {
        lowerTextView.text = getString(R.string.first_button_clicked)
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 두 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnSecondButton(view: View) {
        lowerTextView.text = getString(R.string.second_button_clicked)
    }
}