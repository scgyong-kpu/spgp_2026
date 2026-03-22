package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val normalizedName: String
        get() {
            // EditText.text 는 Editable 이므로 보통 toString()으로 String 으로 바꿔 쓴다.
            // trim()을 적용하면 앞뒤 공백만 입력한 경우도 빈 이름처럼 처리할 수 있다.
            // 비슷한 기능은 다른 언어에도 자주 나오며,
            // C 는 표준 trim 함수가 없어 직접 구현하는 경우가 많고, C++/C#/Swift/Java/JavaScript 는 trim(),
            // Python 은 strip() 같은 이름을 쓴다.
            val nameInput = binding.yourNameEditText.text.toString().trim()

            // 이름 입력이 비어 있으면 미리 준비한 noname 문자열을 기본값으로 사용한다.
            // 이렇게 하면 사용자가 아무것도 입력하지 않아도 포맷 문자열이 자연스럽게 완성된다.
            return if (nameInput.isEmpty()) getString(R.string.noname) else nameInput
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.yourNameEditText.addTextChangedListener { handleNameChanged() }
    }

    private fun handleNameChanged() {
        // addTextChangedListener 는 androidx.core.widget 가 제공하는 Kotlin 확장 함수이다.
        // Java 에서는 TextWatcher 객체를 만들고 before/on/after 메서드를 모두 구현해야 했지만,
        // Kotlin 에서는 필요한 동작만 람다로 바로 넘길 수 있어 코드가 훨씬 짧아진다.
        // 지금은 람다 안 코드를 따로 handleNameChanged() 함수로 빼 두어,
        // 입력 이벤트 처리와 실제 동작을 분리한 구조를 보여 준다.

        // Switch 가 켜져 있으면 이름이 바뀔 때마다 doIt()을 다시 호출해
        // 최종 결과 문장을 즉시 다시 계산한다.
        if (binding.applyImmediatelySwitch.isChecked) {
            doIt()
            // 여기서는 람다가 아니라 일반 함수 안이므로 return 만 써도 이 함수가 바로 끝난다.
            // 이전처럼 addTextChangedListener 람다 안에 직접 썼다면 return@addTextChangedListener 가 필요했다.
            return
        }

        // Switch 가 꺼져 있으면 최종 적용은 아직 하지 않고,
        // 사용자가 입력 중인 이름의 길이만 화면에 보여 준다.
        binding.mainTextView.text = getString(R.string.name_length_fmt, normalizedName.length)
    }

    fun onDoItButtonClick(view: View) {
        doIt()
    }

    private fun doIt() {
        // Kotlin 의 if 는 문장(statement)일 뿐 아니라 값을 만드는 표현식(expression)으로도 쓸 수 있다.
        // 그래서 체크 상태에 따라 문자열 자체가 아니라 문자열 리소스 ID 하나를 골라 val 에 바로 담을 수 있다.
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.you_get_one_grand else R.string.you_have_nothing

        // setText 를 포함한 많은 Android API 는 CharSequence 버전과
        // 문자열 리소스 ID(Int)를 받는 버전을 함께 제공한다.
        // 여기서는 strId 를 getString 으로 실제 문자열로 읽어 아래의 포맷 문자열 조합에 사용한다.
        val msg = getString(strId)

        // main_msg_fmt 는 %1$s, %2$s 자리표시자를 가진 포맷 문자열이다.
        // 첫 번째 자리에는 이름, 두 번째 자리에는 결과 메시지를 넣어 최종 문장을 만든다.
        val text = getString(R.string.main_msg_fmt, normalizedName, msg)

        // text 프로퍼티에 String 을 대입하면 TextView 의 setText(CharSequence) 버전이 호출된다.
        binding.mainTextView.text = text
    }

    fun onCheckGoodProgrammer(view: View) {
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.good_news else R.string.bad_news
        binding.mainTextView.setText(strId)
    }
}
