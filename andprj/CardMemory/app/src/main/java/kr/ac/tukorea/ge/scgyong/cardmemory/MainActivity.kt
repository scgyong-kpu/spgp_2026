package kr.ac.tukorea.ge.scgyong.cardmemory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View를 안전하게 참조한다.
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardButtons: List<ImageButton>

    // 카드 앞면 이미지 리소스를 쌍으로 보관한다.
    private val imageResIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
    )
    // 직전에 뒤집은 카드 위치를 기억한다.
    private var openedCardIndex: Int? = null
    // 사용자가 카드를 뒤집은 횟수를 누적한다.
    private var flipCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardButtons = listOf(
            binding.card00, binding.card01, binding.card02, binding.card03,
            binding.card10, binding.card11, binding.card12, binding.card13,
            binding.card20, binding.card21, binding.card22, binding.card23,
            binding.card30, binding.card31, binding.card32, binding.card33,
        )

        cardButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleCardClick(index)
            }
        }

        // Activity 가 시작될 때에도 게임이 시작된다
        startNewGame()
    }

    private fun shuffleCardImages() {
        // Kotlin의 Array.shuffle()은 내부적으로 무작위 위치를 골라 원소를 섞는 방식이라
        // 직접 Fisher-Yates 셔플을 구현했을 때와 목적이 같다.
        // 차이는 구현 위치에 있는데, 기존 방식은 인덱스를 순회하며 교환 로직을 직접 써야 했고
        // 지금 방식은 표준 라이브러리에 이미 준비된 함수를 호출해서 같은 의도를 더 짧고 명확하게 표현한다.
        // 이런 형태의 shuffle() 호출은 Kotlin 표준 라이브러리에서 바로 제공하는 편의 함수이고,
        // Java 배열에서는 같은 이름의 함수를 바로 호출할 수 없어서 보통 직접 구현하거나 다른 유틸리티를 사용해야 한다.
        // 즉, 셔플 알고리즘의 개념이 바뀐 것이 아니라 직접 구현을 표준 함수 호출로 치환한 것이다.
        imageResIds.shuffle()
        Log.d("MainActivity", "Shuffled imageResIds: ${imageResIds.joinToString(",")}")
    }

    fun onRestartButtonClick(view: View) {
        // Restart 버튼은 바로 재시작하지 않고 먼저 확인 대화상자를 띄운다.
        // Android Studio 에서는 Ctrl+NumPad-/+ 단축키로 소스를 접거나 펼 수 있는데,
        // 리소스 문자열도 접으면 로드해서 보여준다.
        askRestart(R.string.restart_dialog_title, R.string.restart_dialog_message)
    }

    // 현재 flipCount 값을 상단 TextView에 반영한다.
    private fun updateFlipCountText() {
        binding.flipCountTextView.text = getString(R.string.flip_count_format, flipCount)
    }

    // 숨은 부작용이 있는 setter 대신 명시적인 함수로 횟수 증가와 화면 갱신을 함께 처리한다.
    private fun incrementFlipCount() {
        flipCount += 1
        updateFlipCountText()
    }

    private fun askRestart(@StringRes titleId: Int, @StringRes msgId: Int) {
        // @StringRes 는 이 인자가 일반 정수가 아니라 문자열 리소스 ID여야 함을 알려 주는 표시이다.
        // 덕분에 실수로 다른 종류의 리소스 ID나 임의의 숫자를 넘겼을 때 IDE나 Lint가 더 잘 잡아줄 수 있다.

        // 교육용 예시라서 Builder Pattern의 단계는 드러내되, 지역변수는 builder와 dlg만 사용한다.
        // titleId, msgId 는 이미 문자열 리소스 ID이므로 getString()으로 미리 꺼내지 않고 Builder에 바로 전달한다.
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        // Builder에 제목과 메시지를 차례대로 채워 넣는다.
        builder.setTitle(titleId)
        builder.setMessage(msgId)

        // setPositiveButton 은 마지막 인자가 람다이므로 괄호 밖으로 빼는 trailing lambda 문법을 사용할 수 있다.
        // 람다는 내부적으로 두 인자(DialogInterface, which)를 받는 함수 형태라서,
        // 둘 다 사용하지 않더라도 { _, _ -> ... }처럼 인자 자리는 맞춰 주어야 한다.
        // 여기서는 두 값이 필요 없으므로 밑줄 두 개로 받고 startNewGame()만 호출한다.
        builder.setPositiveButton(R.string.dialog_yes) { _, _ ->
            startNewGame()
        }

        // No 는 별도 동작이 없으므로 null 을 넘기면 AlertDialog 가 기본적으로 대화상자만 닫아 준다.
        // 이쪽은 람다를 넘기는 대신 null 을 전달하므로 trailing lambda 문법을 적용할 대상이 없다.
        // 즉, 이 경우에는 dismiss()를 직접 쓰지 않아도 현재 요구사항을 만족한다.
        builder.setNegativeButton(R.string.dialog_no, null)

        // create() 단계에서 실제 AlertDialog 객체를 만들고,
        // show() 단계에서 화면에 표시한다는 점을 분리해서 보여주기 위해 dlg 변수로 한 번 받는다.
        val dlg: AlertDialog = builder.create()
        dlg.show()
    }

    // 게임이 시작되면 모두 Design Time 상태로 초기화해 주는 코드를 실행한다
    private fun startNewGame() {
        // 개발 중에는 굳이 셔플하지 않고 고정된 순서로 테스트할 수 있도록 주석 처리한다.
        // 나중에 완성된 버전에서는 이 부분의 주석을 해제하여 게임이 시작될 때마다 카드가 무작위로 섞이도록 한다.
        // shuffleCardImages()

        // 모든 카드를 다시 뒷면으로 돌리고, 맞춰서 사라진 카드도 다시 보이게 만든다.
        for (button in cardButtons) {
            button.setImageResource(R.mipmap.card_blue_back)
            button.visibility = View.VISIBLE
        }

        // 새 게임이 시작되므로 뒤집은 횟수와 열린 카드 상태도 처음으로 되돌린다.
        flipCount = 0
        updateFlipCountText()
        openedCardIndex = null
    }

    fun handleCardClick(buttonIndex: Int) {
        if (buttonIndex == openedCardIndex) {
            return
        }

        val button = cardButtons[buttonIndex]
        val imgResId = imageResIds[buttonIndex]

        // 이미 열린 카드가 있으면 현재 카드와 비교한다.
        openedCardIndex?.let { index ->
            val openedButton = cardButtons[index]
            val openedImgResId = imageResIds[index]

            if (openedImgResId == imgResId) {
                // 같은 그림이면 두 카드를 화면에서 숨긴다.
                openedButton.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                openedCardIndex = null
                return
            } else {
                // 다르면 이전 카드를 다시 뒷면으로 돌린다.
                openedButton.setImageResource(R.mipmap.card_blue_back)
            }
        }

        // 현재 카드를 공개하고 마지막 선택으로 기록한다.
        button.setImageResource(imgResId)
        // 실제로 새 카드가 열리는 순간에만 뒤집은 횟수를 증가시킨다.
        incrementFlipCount()
        openedCardIndex = buttonIndex
    }
}
