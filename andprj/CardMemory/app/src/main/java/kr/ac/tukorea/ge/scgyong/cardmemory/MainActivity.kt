package kr.ac.tukorea.ge.scgyong.cardmemory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
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
    )

    // 카드 종류 인덱스를 카드 버튼과 매핑한다.
    // Resource ID 는 View 의 영역이고, cardIndex 는 Model 의 영역이다.
    // 게임이 시작될 때마다 이 배열을 섞어서 카드 배치를 무작위로 만든다.
    // 값이 null 이면 그 위치의 카드는 이미 맞춰져서 제거된 상태로 본다.
    private lateinit var cardIndices: Array<Int?>
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
        // imageResIds 는 카드 종류별 리소스 표이고, 실제 카드 배치 순서는 cardIndices 가 담당한다.
        // 따라서 게임을 섞을 때는 리소스 표 자체가 아니라 카드 배치 인덱스 배열을 섞어야 한다.
        cardIndices.shuffle()
        Log.d("MainActivity", "Shuffled cardIndices: ${cardIndices.joinToString(",")}")
    }

    private fun resetCardIndices() {
        // 새 게임이 시작될 때 기본 카드 배치를 다시 만든다.
        // 이 초기화가 먼저 끝나야 그 다음에 shuffleCardImages()로 배치를 섞을 수 있다.

        // 카드 종류 인덱스를 두 장씩 연속해서 만들기 위해 정수 나눗셈을 사용한다.
        // index 가 0, 1일 때는 0 / 2, 1 / 2가 모두 0이므로 [0, 0]이 된다.
        // index 가 2, 3일 때는 2 / 2, 3 / 2가 모두 1이므로 [1, 1]이 된다.
        // 이런 방식으로 index 가 두 칸씩 진행될 때마다 같은 카드 종류 번호가 두 번 반복된다.
        // 따라서 전체 결과는 [0, 0, 1, 1, 2, 2, ...] 형태가 된다.
        // 카드 종류가 8개이므로 imageResIds.size * 2 길이의 배열을 만들면 16장의 카드 구성이 완성된다.
        cardIndices = Array(imageResIds.size * 2) { index -> index / 2 }

        // shuffleCardImages()
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

        // AlertDialog 객체를 따로 보관하지 않고
        // create()를 생략하고 Builder.show()를 바로 사용해도 된다.
        AlertDialog.Builder(this)
            .setTitle(titleId)
            .setMessage(msgId)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                startNewGame()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    private fun isGameOver(): Boolean {
        // 이제 게임 종료 여부는 화면의 visibility 가 아니라 모델 역할을 하는 cardIndices 로 판정한다.
        // 모든 위치가 null 이면 16장의 카드가 모두 제거된 상태이므로 게임이 끝난 것이다.
        return cardIndices.all { cardIndex ->
            cardIndex == null
        }
    }

    // 게임이 시작되면 모두 Design Time 상태로 초기화해 주는 코드를 실행한다
    private fun startNewGame() {
        resetCardIndices()

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
            Toast.makeText(this, R.string.card_already_open_toast, Toast.LENGTH_SHORT).show()
            return
        }

        val button = cardButtons[buttonIndex]
        // cardIndices 에서 null 이 나오면 이미 제거된 카드 위치라는 뜻이므로 더 진행하지 않는다.
        val cardIndex = cardIndices[buttonIndex] ?: return
        val imgResId = imageResIds[cardIndex]

        // 이미 열린 카드가 있으면 현재 카드와 비교한다.
        openedCardIndex?.let { index ->
            val openedButton = cardButtons[index]
            // 이전에 열려 있던 카드도 이미 제거되었다면 비교할 대상이 없으므로 그대로 종료한다.
            val openedCardIndexValue = cardIndices[index] ?: return
            val openedImgResId = imageResIds[openedCardIndexValue]

            if (openedImgResId == imgResId) {
                // 같은 그림이면 두 카드를 화면에서 숨긴다.
                openedButton.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                // 같은 그림을 찾았으므로 두 위치는 더 이상 카드가 없는 상태를 null 로 기록한다.
                cardIndices[index] = null
                cardIndices[buttonIndex] = null
                openedCardIndex = null

                if (isGameOver()) {
                    askRestart(R.string.game_over_dialog_title, R.string.game_over_dialog_message)
                }
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
