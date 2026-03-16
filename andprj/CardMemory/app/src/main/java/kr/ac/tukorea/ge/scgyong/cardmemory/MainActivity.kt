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
import kr.ac.tukorea.ge.scgyong.cardmemory.model.GameState

class MainActivity : AppCompatActivity() {
    companion object {
        private const val GAME_STATE_JSON_KEY = "game_state_json"
        private const val GAME_STATE_PREFS_NAME = "game_state_prefs"
    }

    // activity_main.xml 의 View 를 안전하게 참조한다.
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardButtons: List<ImageButton>

    // 카드 종류별 앞면 이미지 리소스 표이다.
    private val imageResIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
    )

    // 카드 리소스 표와 실제 게임 상태는 분리한다.
    // imageResIds 는 종류 표이고, 카드 배치와 제거 상태는 GameState 가 담당한다.
    private val gameState = GameState(imageResIds.size)

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

        // 저장된 Bundle 안에 게임 상태 JSON 이 있으면 회전이나 테마 변경 뒤에도 이전 상태를 이어서 복원한다.
        // savedInstanceState 가 없을 때는 SharedPreferences 에 저장해 둔 JSON 을 읽어 마지막 게임 상태를 복원한다.
        // 에뮬레이터의 Device Settings 창을 열고 Dark Mode 를 체크하면 Activity 재시작과 상태 복원 흐름을 테스트해 볼 수 있다.
        val prefs = getSharedPreferences(GAME_STATE_PREFS_NAME, MODE_PRIVATE)
        val gameStateJson = savedInstanceState?.getString(GAME_STATE_JSON_KEY)
            ?: prefs.getString(GAME_STATE_JSON_KEY, null)
        if (gameStateJson == null) {
            startNewGame()
        } else {
            Log.d("MainActivity", "Restoring game state from JSON: $gameStateJson")
            gameState.loadFromJson(gameStateJson)
            renderGameState()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Activity 가 재생성되기 전에 현재 게임 상태를 JSON 문자열로 저장해 둔다.
        val json = gameState.toJson()
        Log.d("MainActivity", "Saving game state to JSON: $json")
        outState.putString(GAME_STATE_JSON_KEY, json)
    }

    override fun onPause() {
        super.onPause()

        // 앱이 잠시 화면에서 사라질 때도 현재 게임 상태를 SharedPreferences 에 저장해 둔다.
        val json = gameState.toJson()
        Log.d("MainActivity", "Saving game state to SharedPreferences: $json")
        getSharedPreferences(GAME_STATE_PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(GAME_STATE_JSON_KEY, json)
            .apply()
    }

    fun onRestartButtonClick(view: View) {
        // Restart 버튼은 바로 재시작하지 않고 먼저 확인 대화상자를 띄운다.
        askRestart(R.string.restart_dialog_title, R.string.restart_dialog_message)
    }

    // 현재 flipCount 값을 상단 TextView 에 반영한다.
    private fun updateFlipCountText() {
        binding.flipCountTextView.text = getString(R.string.flip_count_format, gameState.flipCount)
    }

    // setter 의 숨은 부작용보다 명시적인 함수 호출이 읽기 쉽다.
    private fun incrementFlipCount() {
        gameState.flipCount += 1
        updateFlipCountText()
    }

    private fun askRestart(@StringRes titleId: Int, @StringRes msgId: Int) {
        // @StringRes 는 이 인자가 일반 정수가 아니라 문자열 리소스 ID 여야 함을 드러낸다.
        AlertDialog.Builder(this)
            .setTitle(titleId)
            .setMessage(msgId)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                startNewGame()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    // 새 게임을 시작할 때는 모델을 먼저 초기화하고, 그다음 화면을 초기 상태로 맞춘다.
    private fun startNewGame() {
        gameState.start()
        Log.d("MainActivity", "Shuffled cardIndices: ${gameState.cardIndices.joinToString(",")}")

        // 모든 카드를 다시 뒷면으로 돌리고, 이전 게임에서 사라진 카드도 다시 보이게 만든다.
        for (button in cardButtons) {
            button.setImageResource(R.mipmap.card_blue_back)
            button.visibility = View.VISIBLE
        }

        updateFlipCountText()
    }

    // 현재 모델 상태를 기준으로 카드 앞면, 뒷면, 제거 상태를 화면에 다시 반영한다.
    // onCreate 에서 JSON 으로 복원한 직후에는 버튼 UI 가 이전 게임 상태를 모르므로 이 함수로 화면을 다시 맞춘다.
    private fun renderGameState() {
        cardButtons.forEachIndexed { index, button ->
            val cardIndex = gameState.cardIndices[index]

            if (cardIndex == null) {
                // null 은 이미 제거된 카드이므로 버튼도 화면에서 숨긴다.
                button.visibility = View.INVISIBLE
                return@forEachIndexed
            }

            button.visibility = View.VISIBLE
            if (index == gameState.openedCardIndex) {
                // 현재 열려 있는 카드 한 장은 앞면 이미지로 복원한다.
                button.setImageResource(imageResIds[cardIndex])
            } else {
                // 나머지 살아 있는 카드는 아직 닫힌 상태이므로 뒷면으로 그린다.
                button.setImageResource(R.mipmap.card_blue_back)
            }
        }

        // 모델에 저장된 뒤집은 횟수도 함께 화면에 다시 표시한다.
        updateFlipCountText()
    }

    fun handleCardClick(buttonIndex: Int) {
        if (buttonIndex == gameState.openedCardIndex) {
            Toast.makeText(this, R.string.card_already_open_toast, Toast.LENGTH_SHORT).show()
            return
        }

        val button = cardButtons[buttonIndex]

        // null 이면 이미 제거된 카드 위치이므로 추가 동작 없이 무시한다.
        val cardIndex = gameState.cardIndices[buttonIndex] ?: return
        val imgResId = imageResIds[cardIndex]

        // 이미 열려 있는 카드가 있으면 현재 카드와 비교한다.
        gameState.openedCardIndex?.let { index ->
            val openedButton = cardButtons[index]

            // 이전에 열려 있던 카드가 이미 제거되었다면 더 비교할 것이 없으므로 그대로 종료한다.
            val openedCardIndexValue = gameState.cardIndices[index] ?: return
            val openedImgResId = imageResIds[openedCardIndexValue]

            if (openedImgResId == imgResId) {
                // 같은 그림이면 두 카드를 화면에서 숨기고 모델 상태도 null 로 바꾼다.
                openedButton.visibility = View.INVISIBLE
                button.visibility = View.INVISIBLE
                gameState.cardIndices[index] = null
                gameState.cardIndices[buttonIndex] = null
                gameState.openedCardIndex = null

                if (gameState.isGameOver()) {
                    askRestart(R.string.game_over_dialog_title, R.string.game_over_dialog_message)
                }
                return
            } else {
                // 다르면 이전 카드는 다시 뒷면으로 돌리고 현재 카드만 열린 상태로 유지한다.
                openedButton.setImageResource(R.mipmap.card_blue_back)
            }
        }

        // 현재 카드를 공개하고 마지막으로 열린 카드 위치를 기록한다.
        button.setImageResource(imgResId)
        incrementFlipCount()
        gameState.openedCardIndex = buttonIndex
    }
}
