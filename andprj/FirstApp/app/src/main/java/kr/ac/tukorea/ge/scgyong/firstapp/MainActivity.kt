package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.scgyong.firstapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding 객체를 만들고 루트 레이아웃을 화면에 연결한다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시스템 바 영역만큼 패딩을 적용해서 상태바, 내비게이션 바와 겹치지 않게 한다.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showCatPage(1)
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 첫 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnPrevious(view: View) { // rename 은 Shift + F6 키로 한다.
        showCatPage(1)
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 두 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnNext(view: View) {
        showCatPage(2)
    }

    private fun showCatPage(page: Int) {
        binding.pageTextView.text = getString(R.string.page_format, page)

        when (page) {
            1 -> binding.catImageView.setImageResource(R.mipmap.cat1)
            2 -> binding.catImageView.setImageResource(R.mipmap.cat2)
        }
    }
}