package kr.ac.tukorea.ge.scgyong.morecontrols

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kr.ac.tukorea.ge.scgyong.morecontrols.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val normalizedName: String
        get() {
            val nameInput = binding.yourNameEditText.text.toString().trim()
            return if (nameInput.isEmpty()) getString(R.string.noname) else nameInput
        }

    private val selectedMoney: Int
        get() = binding.moneySeekBar.progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateMoneyLabel()

        // 이름 입력이 바뀌면 결과를 다시 계산하거나 길이만 보여 준다.
        binding.yourNameEditText.addTextChangedListener { handleNameChanged() }

        // SeekBar 진행값이 바뀌면 금액 표시를 갱신한다.
        binding.moneySeekBar.onProgressChanged { handleProgressChanged() }

        Log.d(javaClass.simpleName, "onCreate called")
    }

    override fun onPause() {
        Log.d(javaClass.simpleName, "onPause called")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d(javaClass.simpleName, "onResume called")
    }

    override fun onDestroy() {
        // 이 로그는 찍힌다고 보장할 수 없다
        Log.d(javaClass.simpleName, "onDestroy called")
        super.onDestroy()
    }

    fun onDoItButtonClick(view: View) {
        doIt()
    }

    fun onCheckGoodProgrammer(view: View) {
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val strId = if (isGood) R.string.good_news else R.string.bad_news
        binding.mainTextView.setText(strId)
    }

    private fun handleNameChanged() {
        if (binding.applyImmediatelySwitch.isChecked) {
            doIt()
            return
        }

        binding.mainTextView.text = getString(R.string.name_length_fmt, normalizedName.length)
    }

    private fun handleProgressChanged() {
        updateMoneyLabel()
        if (binding.applyImmediatelySwitch.isChecked) {
            doIt()
        }
    }

    private fun doIt() {
        val isGood = binding.goodProgrammerCheckbox.isChecked
        val msg = if (isGood) {
            getString(R.string.you_get_money_fmt, selectedMoney)
        } else {
            getString(R.string.you_have_nothing)
        }

        val text = getString(R.string.main_msg_fmt, normalizedName, msg)
        binding.mainTextView.text = text
    }

    private fun updateMoneyLabel() {
        binding.moneyValueTextView.text = getString(R.string.money_value_fmt, selectedMoney)
    }

    fun onOpenNaverButtonClicked(view: View) {
        val uri = Uri.parse("https://www.naver.com/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun onOpenAnotherActivityButtonClicked(view: View) {
        val intent = Intent(this, AnotherActivity::class.java)
        startActivity(intent)
    }
}

// SeekBar 변경 리스너를 람다 한 줄로 연결하기 위한 extension function.
private fun SeekBar.onProgressChanged(action: (Int) -> Unit) {
    setOnSeekBarChangeListener(
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                action(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        },
    )
}
