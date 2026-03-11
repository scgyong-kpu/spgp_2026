package kr.ac.tukorea.ge.scgyong.firstapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.tukorea.ge.scgyong.firstapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val KEY_CURRENT_PAGE = "current_page"
    }

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

        // 저장된 상태가 있으면 마지막으로 보던 페이지를, 없으면 첫 페이지를 보여준다.
        val restoredPage = savedInstanceState?.getInt(KEY_CURRENT_PAGE) ?: 1
        showCatPage(restoredPage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // orientation change가 일어나도 현재 페이지 번호를 복원할 수 있게 저장한다.
        outState.putInt(KEY_CURRENT_PAGE, currentPage)
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
        R.mipmap.cat1, R.mipmap.cat2, R.mipmap.cat3, R.mipmap.cat4, R.mipmap.cat5, R.mipmap.cat6
    )

    private fun showCatPage(page: Int) {
        // 전체 페이지 수는 이미지 배열의 길이로부터 계산한다.
        val total = catImageIds.size

        // 범위를 벗어난 페이지 번호는 무시한다.
        if (page !in 1..total) {
            return
        }

        binding.prevButton.isEnabled = page > 1
        binding.nextButton.isEnabled = page < total

        // 현재 페이지 번호를 갱신한다.
        currentPage = page

        // 상단의 페이지 표시 문자열을 "n / total" 형식으로 갱신한다.
        binding.pageTextView.text = getString(R.string.page_format, currentPage, total)

        // 현재 페이지 번호에 맞는 고양이 이미지를 화면에 보여준다.
        binding.catImageView.setImageResource(catImageIds[currentPage - 1])
    }
}