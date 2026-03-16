package kr.ac.tukorea.ge.scgyong.cardmemory

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding
import kr.ac.tukorea.ge.scgyong.cardmemory.model.GameState
import kr.ac.tukorea.ge.scgyong.cardmemory.model.SelectResult

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CARD_ANIMATION_DURATION = 300L
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
            button.setOnTouchListener { _, event ->
                // ACTION_DOWN 은 손가락이 버튼에 닿는 순간이다.
                // Click 대신 이 시점에 처리하면 손을 떼기 전에 카드가 바로 뒤집힌다.
                if (event.action == MotionEvent.ACTION_DOWN) {
                    handleCardClick(index)
                    // true 를 반환하면 이 버튼이 이번 터치 시퀀스를 처리하겠다고 알리고,
                    // 기본 Click 동작으로 이어지지 않게 막는다.
                    true
                } else {
                    // 여기서는 ACTION_DOWN 에서만 동작이 필요하므로 나머지 이벤트는 직접 처리하지 않는다.
                    false
                }
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
        cardButtons.indices.forEach { index ->
            showCardBackInstant(index)
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
                hideCardInstant(index)
                return@forEachIndexed
            }

            if (index == gameState.openedCardIndex) {
                // 현재 열려 있는 카드 한 장은 앞면 이미지로 복원한다.
                showCardFrontInstant(index)
            } else {
                // 나머지 살아 있는 카드는 아직 닫힌 상태이므로 뒷면으로 그린다.
                showCardBackInstant(index)
            }
        }

        // 모델에 저장된 뒤집은 횟수도 함께 화면에 다시 표시한다.
        updateFlipCountText()
    }

    // startNewGame 이나 renderGameState 처럼 화면을 즉시 맞춰야 할 때는 애니메이션 없이 앞면을 그린다.
    private fun showCardFrontInstant(index: Int) {
        val cardIndex = gameState.cardIndices[index] ?: return
        with(cardButtons[index]) {
            animate().cancel()
            clearAnimation()
            visibility = View.VISIBLE
            alpha = 1f
            rotationY = 0f
            setImageResource(imageResIds[cardIndex])
        }
    }

    // 복원이나 재시작 중에는 카드가 하나씩 뒤집히지 않도록 뒷면도 즉시 반영한다.
    private fun showCardBackInstant(index: Int) {
        with(cardButtons[index]) {
            animate().cancel()
            clearAnimation()
            visibility = View.VISIBLE
            alpha = 1f
            rotationY = 0f
            setImageResource(R.mipmap.card_blue_back)
        }
    }

    // 이미 제거된 카드를 다시 그릴 때는 페이드아웃 없이 바로 숨긴다.
    private fun hideCardInstant(index: Int) {
        with(cardButtons[index]) {
            animate().cancel()
            clearAnimation()
            alpha = 1f
            rotationY = 0f
            visibility = View.INVISIBLE
        }
    }

    // flip 애니메이션은 반쯤 돌아간 시점에 이미지를 바꾸는 방식으로 앞뒤가 뒤집히는 느낌을 만든다.
    // 첫 절반은 0 -> 90, 중간에서 midAction 실행, 그 다음 -90 -> 0 으로 돌아온다.
    private fun animateFlip(index: Int, beforeAction: () -> Unit, midAction: () -> Unit) {
        val button = cardButtons[index]
        with(button) {
            animate().cancel()
            clearAnimation()
            visibility = View.VISIBLE
            alpha = 1f
            rotationY = 0f
        }

        beforeAction()

        val firstHalf = ObjectAnimator.ofFloat(button, View.ROTATION_Y, 0f, 90f).apply {
            duration = CARD_ANIMATION_DURATION / 2
        }
        val secondHalf = ObjectAnimator.ofFloat(button, View.ROTATION_Y, -90f, 0f).apply {
            duration = CARD_ANIMATION_DURATION / 2
        }

        firstHalf.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                midAction()
                button.rotationY = -90f
            }
        })

        AnimatorSet().apply {
            playSequentially(firstHalf, secondHalf)
            start()
        }
    }

    // 카드를 열 때는 먼저 뒷면 상태에서 시작하고, 애니메이션 중간에 앞면으로 바꾼다.
    private fun openCard(index: Int) {
        animateFlip(
            index = index,
            beforeAction = { showCardBackInstant(index) },
            midAction = { showCardFrontInstant(index) },
        )
    }

    // 카드를 닫을 때는 현재 앞면에서 시작해, 애니메이션 중간에 뒷면으로 바꿔 다시 닫는다.
    private fun closeCard(index: Int) {
        animateFlip(
            index = index,
            beforeAction = { showCardFrontInstant(index) },
            midAction = { showCardBackInstant(index) },
        )
    }

    // 제거 애니메이션은 flip 대신 alpha 1 -> 0 으로 사라지는 느낌만 준다.
    // 모델의 null 처리는 여기서 하지 않고, 호출한 쪽에서 따로 관리한다.
    private fun removeCard(index: Int) {
        showCardFrontInstant(index)
        with(cardButtons[index]) {
            animate().cancel()
            clearAnimation()
            visibility = View.VISIBLE
            rotationY = 0f
            alpha = 1f
            animate()
                .alpha(0f)
                .setDuration(CARD_ANIMATION_DURATION)
                .withEndAction {
                    visibility = View.INVISIBLE
                    alpha = 1f
                }
                .start()
        }
    }

    fun handleCardClick(buttonIndex: Int) {
        val prev = gameState.openedCardIndex
        val result = gameState.selectCard(buttonIndex)
        when (result) {
            SelectResult.ALREADY_MATCHED -> {
                // 이미 짝이 맞은 카드는 다시 선택할 수 없으므로 아무 동작도 하지 않는다.
                return
            }
            SelectResult.ALREADY_OPEN -> {
                // 이미 열려 있는 카드를 다시 누른 경우는 Toast 메시지로 알려주고 아무 동작도 하지 않는다.
                Toast.makeText(this, R.string.card_already_open_toast, Toast.LENGTH_SHORT).show()
                return
            }
            SelectResult.FIRST_OPENED -> {
                // 아직 열려 있는 카드가 없어서 이번 카드가 첫 번째로 열린 경우는 애니메이션과 게임 종료 체크 없이 바로 열린 상태로 유지한다.
                openCard(buttonIndex)
                incrementFlipCount()
                return
            }
            SelectResult.MISMATCHED -> {
                // 카드 종류가 일치하지 않으면, 이번에 선택한 카드가 새로 열린 카드가 된다.
                // 이전에 열려 있던 카드는 애니메이션으로 닫히고, 현재 카드는 애니메이션으로 열린 상태로 유지한다.
                closeCard(prev!!)
                openCard(buttonIndex)
                incrementFlipCount()
                return
            }
            SelectResult.MATCHED -> {
                // 카드 종류가 일치하면, 두 카드를 모두 애니메이션으로 제거한다.
                removeCard(prev!!)
                removeCard(buttonIndex)

                // 애니메이션 완료 후에 하는 것이 더 자연스럽지만, 간단히 카드 제거와 게임 종료 체크를 바로 한다.
                gameState.removeCard(prev)
                gameState.removeCard(buttonIndex)

                if (gameState.isGameOver()) {
                    askRestart(R.string.game_over_dialog_title, R.string.game_over_dialog_message)
                }
                return
            }
        }
    }
}
