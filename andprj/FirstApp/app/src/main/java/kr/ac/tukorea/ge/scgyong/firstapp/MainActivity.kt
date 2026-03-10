package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.widget.Button
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

        // 첫 번째 버튼을 찾아 현재 Activity를 클릭 리스너로 등록한다.
        val firstButton = findViewById<Button>(R.id.first_button)

        // 람다를 전달해서 첫 번째 버튼 클릭 시 두 번째 TextView의 문구를 변경한다.
        firstButton.setOnClickListener {
            val lowerTextView = findViewById<TextView>(R.id.lower_text_view)
            lowerTextView.text = getString(R.string.first_button_clicked)
        }

        // 시스템 바 영역만큼 패딩을 적용해서 상태바, 내비게이션 바와 겹치지 않게 한다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}