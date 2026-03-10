package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 액티비티가 사용할 화면 레이아웃을 연결한다.
        setContentView(R.layout.activity_main)

        // 시스템 바 영역만큼 패딩을 적용해서 상태바, 내비게이션 바와 겹치지 않게 한다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // XML의 android:onClick 속성을 통해 호출되는 첫 번째 버튼 클릭 처리 메서드이다.
    fun onBtnFirstButton(view: View) {
        val lowerTextView = findViewById<TextView>(R.id.lower_text_view)
        lowerTextView.text = getString(R.string.first_button_clicked)
    }
}