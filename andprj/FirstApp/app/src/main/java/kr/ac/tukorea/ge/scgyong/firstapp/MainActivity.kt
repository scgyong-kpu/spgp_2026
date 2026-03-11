package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.scgyong.firstapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml에 대한 View Binding 객체이다.
    private lateinit var binding: ActivityMainBinding

    // 현재 화면에 표시 중인 고양이 페이지 번호를 저장한다.
    private var currentPage = 1

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

        // 앱이 시작되면 첫 번째 고양이 페이지를 먼저 보여준다.
        showCatPage(1)
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 첫 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnPrevious(view: View) {
        // 이전 버튼을 누르면 페이지 번호를 1 감소시켜 다시 표시한다.
        showCatPage(currentPage - 1)
    }

    // Event Listener 연결하는 방법 #4
    // XML의 android:onClick 속성으로 두 번째 버튼 클릭 메서드를 직접 연결한다.
    fun onBtnNext(view: View) {
        // 다음 버튼을 누르면 페이지 번호를 1 증가시켜 다시 표시한다.
        showCatPage(currentPage + 1)
    }

    // 페이지 번호와 연결되는 고양이 이미지 리소스 목록이다.
    private val catImageIds = intArrayOf(
        R.mipmap.cat1, R.mipmap.cat2, R.mipmap.cat3, R.mipmap.cat4, R.mipmap.cat5
    )

    private fun showCatPage(page: Int) {
        // 전달받은 페이지 번호를 현재 페이지 상태로 저장한다.
        currentPage = page

        // 상단의 페이지 표시 문자열을 "n / 5" 형식으로 갱신한다.
        binding.pageTextView.text = getString(R.string.page_format, page)

        // 페이지 번호가 유효한 범위에 있을 때만 해당 고양이 이미지를 표시한다.
        if (page in 1..catImageIds.size) {
            binding.catImageView.setImageResource(catImageIds[page - 1])
        }
    }
}