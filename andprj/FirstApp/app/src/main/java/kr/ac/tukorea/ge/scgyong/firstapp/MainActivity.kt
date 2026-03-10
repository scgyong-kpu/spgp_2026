package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
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

        val lowerTextView = findViewById<TextView>(R.id.lower_text_view)
        val firstButton = findViewById<Button>(R.id.first_button)
        val secondButton = findViewById<Button>(R.id.second_button)

        // Event Listener 연결하는 방법 #3
        // 즉석에서 객체를 만들어 전달해 줄테니 얘한테 알려줘.
        firstButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                lowerTextView.text = getString(R.string.first_button_clicked)
            }
        })

        // Event Listener 연결하는 방법 #3
        // 즉석에서 객체를 만들어 전달해 줄테니 얘한테 알려줘.
        secondButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                lowerTextView.text = getString(R.string.second_button_clicked)
            }
        })

        // 시스템 바 영역만큼 패딩을 적용해서 상태바, 내비게이션 바와 겹치지 않게 한다.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}