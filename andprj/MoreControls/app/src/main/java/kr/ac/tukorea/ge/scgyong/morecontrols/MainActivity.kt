package kr.ac.tukorea.ge.scgyong.morecontrols

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Java 에서는 EditText 의 텍스트 변경을 감지하려면 TextWatcher 인터페이스를 구현한 객체를 만들어 addTextChangedListener() 메서드에 넘겨야 했다.
        // binding.yourNameEditText.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //    }
        //
        //    @Override
        //    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //    }
        //
        //    @Override
        //    public void afterTextChanged(Editable editable) {
        //    }
        // });
    }

    fun onDoItButtonClick(view: View) {
        doIt()
    }

    private fun doIt() {
        // Kotlin 의 if 는 문장(statement)만이 아니라 값을 만드는 표현식(expression)으로도 쓸 수 있다.
        // 그래서 체크 상태에 따라 String 자체가 아니라 문자열 리소스 ID 하나를 골라 val 에 바로 담을 수 있다.
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.you_get_one_grand else R.string.you_have_nothing

        // setText 는 CharSequence 를 받는 버전과 문자열 리소스 ID(Int)를 받는 버전이 따로 있다.
        // 여기서는 if 표현식이 돌려준 R.string.xxx 값을 그대로 넘겨 리소스를 읽는 버전을 사용한다.
        val msg = getString(strId)

        // EditText 의 text 는 Editable 이므로, 다른 문자열과 조합하려면 보통 String 으로 바꿔 쓴다.
        // trim() 을 적용하면 앞뒤 공백만 입력한 경우를 빈 문자열처럼 다룰 수 있어 결과 문장이 더 자연스럽다.
        val nameInput = binding.yourNameEditText.text.toString().trim()

        // 이름 입력이 비어 있으면 미리 준비한 noname 문자열 리소스를 대신 사용한다.
        // 이렇게 하면 사용자가 아무것도 입력하지 않았을 때도 포맷 문자열이 깨지지 않고 일관된 문장을 만들 수 있다.
        val name = if (nameInput.isEmpty()) getString(R.string.noname) else nameInput

        // main_msg_fmt 는 %1$s, %2$s 자리표시자를 가진 포맷 문자열이다.
        // 첫 번째 자리에 이름, 두 번째 자리에 결과 메시지를 넣어 최종 문장을 완성한다.
        val text = getString(R.string.main_msg_fmt, name, msg)

        // text 프로퍼티에 String 을 대입하면 TextView 의 setText(CharSequence) 버전이 호출된다.
        binding.mainTextView.text = text
    }

    fun onCheckGoodProgrammer(view: View) {
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.good_news else R.string.bad_news
        binding.mainTextView.setText(strId)
    }
}
