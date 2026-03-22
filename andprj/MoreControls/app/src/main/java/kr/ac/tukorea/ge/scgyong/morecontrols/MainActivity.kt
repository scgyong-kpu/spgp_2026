package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onDoItButtonClick(view: View) {
        // Kotlin 의 if 는 문장(statement)만이 아니라 값을 만드는 표현식(expression)으로도 쓸 수 있다.
        // 그래서 체크 상태에 따라 String 자체가 아니라 문자열 리소스 ID 하나를 골라 val 에 바로 담을 수 있다.
        val resultTextId = if (binding.goodProgrammerCheckbox.isChecked) {
            R.string.you_get_one_grand
        } else {
            R.string.you_have_nothing
        }

        // setText 는 CharSequence 를 받는 버전과 문자열 리소스 ID(Int)를 받는 버전이 따로 있다.
        // 여기서는 if 표현식이 돌려준 R.string.xxx 값을 그대로 넘겨 리소스를 읽는 버전을 사용한다.
        binding.mainTextView.setText(resultTextId)
    }

    fun onCheckGoodProgrammer(view: View) {
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.good_news else R.string.bad_news
        binding.mainTextView.setText(strId)
    }
}
