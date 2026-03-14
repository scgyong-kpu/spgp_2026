package kr.ac.tukorea.ge.scgyong.cardsbygpt

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardsbygpt.databinding.ActivityMainBinding
import kr.ac.tukorea.ge.scgyong.cardsbygpt.model.GameState
import kr.ac.tukorea.ge.scgyong.cardsbygpt.model.SelectResult

class MainActivity : AppCompatActivity() {

    // activity_main.xml의 뷰를 코드에서 안전하게 다루기 위한 View Binding 객체이다.
    private lateinit var binding: ActivityMainBinding

    // 16장의 카드 버튼을 배열로 모아 인덱스로 접근할 수 있게 한다.
    private lateinit var cardButtons: Array<ImageButton>

    // 카드 열림 상태, 매칭 상태, 뒤집은 횟수 같은 게임 규칙 상태를 관리한다.
    private lateinit var gameState: GameState

    // 카드 애니메이션이 진행 중일 때 중복 입력을 막기 위한 플래그이다.
    private var isAnimating = false

    // 카드 종류 인덱스(0..7)를 실제 카드 이미지 리소스에 대응시키는 배열이다.
    private val cardImageIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 객체를 만들고 루트 레이아웃을 화면에 연결한다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면에 있는 카드 버튼들을 배열로 묶고 클릭 이벤트를 연결한다.
        initCardButtons()

        // 1. orientation change 직후라면 Bundle에 저장된 상태를 먼저 복원한다.
        // 2. Bundle이 없으면 마지막으로 저장한 SharedPreferences 상태를 읽어 본다.
        val restoredGameState = savedInstanceState?.loadGameState() ?: loadGameStateFromPrefs()

        // 복원할 상태가 있으면 그대로 화면에 반영하고,
        // 없으면 새 GameState를 만들어 새 게임으로 시작한다.
        if (restoredGameState != null) {
            gameState = restoredGameState
            showCurrentState()
        } else {
            gameState = GameState()
            startNewGame()
        }
    }

    private fun initCardButtons() {
        // View Binding으로 얻은 16개 카드 버튼을 순서대로 배열에 담는다.
        cardButtons = arrayOf(
            binding.card0, binding.card1, binding.card2, binding.card3,
            binding.card4, binding.card5, binding.card6, binding.card7,
            binding.card8, binding.card9, binding.card10, binding.card11,
            binding.card12, binding.card13, binding.card14, binding.card15
        )

        // 각 카드 버튼이 눌리면 자신의 인덱스를 가지고 공통 처리 함수로 들어간다.
        for (i in cardButtons.indices) {
            cardButtons[i].setOnClickListener {
                handleCardClick(i)
            }
        }
    }

    private fun startNewGame() {
        // 카드 배치와 매칭 상태, 뒤집은 횟수를 처음 상태로 되돌린다.
        gameState.reset()
        isAnimating = false

        // 모든 카드를 다시 보이게 하고 뒷면 이미지와 기본 변환 상태로 초기화한다.
        for (button in cardButtons) {
            resetCardButtonImageResource(button, R.mipmap.card_blue_back)
        }

        // 현재 뒤집은 횟수를 상단 텍스트에 반영한다.
        updateFlipCount()
    }

    private fun showCurrentState() {

        // 복원된 GameState를 기준으로 각 카드 버튼의 현재 화면 상태를 다시 그린다.

        for (i in cardButtons.indices) {

            val card = gameState.cards[i]
            val button = cardButtons[i]

            // 이미 매칭된 카드는 앞면 이미지를 잠깐 맞춘 뒤 화면에서 숨긴다.
            if (card.isMatched) {
                resetCardButtonImageResource(button, cardImageIds[card.cardIndex])
                button.visibility = View.INVISIBLE
                continue
            }

            // 현재 열린 카드라면 앞면을, 아니면 뒷면을 보여 준다.
            val imgRes = if (gameState.openedIndex == i) {
                cardImageIds[card.cardIndex]
            } else R.mipmap.card_blue_back
            resetCardButtonImageResource(button, imgRes)
        }

        updateFlipCount()
    }

    private fun resetCardButtonImageResource(button: ImageButton, @DrawableRes imgRes: Int) {
        // 카드 버튼을 기본 상태로 되돌리면서 지정한 이미지 리소스를 적용한다.
        button.setImageResource(imgRes)
        button.visibility = View.VISIBLE
        button.isEnabled = true
        button.rotation = 0f
        button.rotationY = 0f
        button.scaleX = 1f
        button.scaleY = 1f
        button.alpha = 1f
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Activity가 재생성될 때를 대비해 현재 게임 상태를 Bundle에 문자열로 저장한다.
        outState.putString("gameState", gameState.toJson())
    }

    override fun onPause() {
        super.onPause()

        // 앱이 잠시 백그라운드로 가도 이어서 할 수 있게 마지막 상태를 SharedPreferences에 저장한다.
        val prefs = getSharedPreferences("cards", MODE_PRIVATE)

        prefs.edit()
            .putString("gameState", gameState.toJson())
            .apply()
    }

    fun loadGameStateFromPrefs(): GameState? {
        // 이전에 저장한 게임 상태 JSON이 있으면 GameState로 복원한다.
        val prefs = getSharedPreferences("cards", MODE_PRIVATE)
        val json = prefs.getString("gameState", null) ?: return null
        return GameState.fromJson(json)
    }

    private fun handleCardClick(index: Int) {
        // 애니메이션 도중에는 추가 입력을 막아 상태가 꼬이지 않게 한다.
        if (isAnimating) return

        // 두 번째 카드 처리 시 필요할 수 있으므로 이전에 열려 있던 카드 인덱스를 기억해 둔다.
        val first = gameState.openedIndex

        // 실제 카드 선택 규칙 판정은 GameState가 담당하고, 화면은 결과만 반영한다.
        val result = gameState.selectCard(index)

        when (result) {
            SelectResult.FIRST_OPENED -> {
                isAnimating = true
                showFront(index) {
                    isAnimating = false
                }
                updateFlipCount()
            }

            SelectResult.MATCHED -> {
                val cleared = gameState.isCleared()

                isAnimating = true
                showFront(index) {
                    // 두 제거 애니메이션은 같은 duration으로 동시에 시작하므로
                    // 하나의 onEnd만 사용
                    removeCard(first!!)
                    removeCard(index) {
                        isAnimating = false

                        if (cleared) {
                            showRestartDialog(
                                R.string.clear_title,
                                R.string.clear_message
                            )
                        }
                    }
                }
                updateFlipCount()
            }

            SelectResult.MISMATCHED -> {
                isAnimating = true

                // 두 flip 애니메이션은 같은 duration으로 동시에 시작하므로
                // 하나의 onEnd만 사용
                showBack(first!!)
                showFront(index) {
                    isAnimating = false
                }

                updateFlipCount()
            }

            SelectResult.ALREADY_OPEN -> {
                Toast.makeText(
                    this,
                    R.string.already_open_card,
                    Toast.LENGTH_SHORT
                ).show()
            }

            SelectResult.ALREADY_MATCHED -> {
                // ignore
            }
        }
    }

    private fun flipCard(
        button: ImageButton,
        midAction: () -> Unit,
        onEnd: (() -> Unit)? = null
    ) {
        // 3D 회전이 자연스럽게 보이도록 cameraDistance를 크게 잡는다.
        val scale = resources.displayMetrics.density
        button.cameraDistance = 8000 * scale

        // 90도 회전 시점에 이미지를 바꾸고 나머지 절반을 다시 회전해 카드 뒤집기 효과를 만든다.
        button.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                midAction()

                button.rotationY = -90f
                button.animate()
                    .rotationY(0f)
                    .setDuration(150)
                    .withEndAction {
                        onEnd?.invoke()
                    }
                    .start()
            }
            .start()
    }

    private fun showFront(index: Int, onEnd: (() -> Unit)? = null) {
        val button = cardButtons[index]
        val cardIndex = gameState.cards[index].cardIndex
        val imageResId = cardImageIds[cardIndex]

        // 카드 종류 인덱스를 실제 카드 이미지로 바꿔 앞면을 보여 준다.
        flipCard(
            button = button,
            midAction = {
                button.setImageResource(imageResId)
            },
            onEnd = onEnd
        )
    }

    private fun showBack(index: Int, onEnd: (() -> Unit)? = null) {
        val button = cardButtons[index]

        // 카드 뒷면 이미지를 보여 주도록 flip 애니메이션을 수행한다.
        flipCard(
            button = button,
            midAction = {
                button.setImageResource(R.mipmap.card_blue_back)
            },
            onEnd = onEnd
        )
    }

    private fun removeCard(index: Int, onEnd: (() -> Unit)? = null) {
        val button = cardButtons[index]
        button.isEnabled = false

        // 맞춘 카드는 회전하면서 작아지고 사라지는 효과로 제거한다.
        button.animate()
            .rotation(button.rotation + 3 * 360f)
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(1000)
            .withEndAction {
                button.visibility = View.INVISIBLE
                button.rotation = 0f
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 1f
                onEnd?.invoke()
            }
            .start()
    }

    private fun updateFlipCount() {
        // 현재 뒤집은 횟수를 상단 텍스트에 반영한다.
        binding.flipCountText.text =
            getString(R.string.flip_count_format, gameState.flipCount)
    }

    fun onRestartClicked(view: View) {
        // 재시작 버튼을 누르면 확인 다이얼로그를 먼저 띄운다.
        showRestartDialog(
            R.string.restart_title,
            R.string.restart_message
        )
    }

    private fun showRestartDialog(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int
    ) {
        // 게임 재시작이나 클리어 후 재시작을 같은 다이얼로그 함수로 처리한다.
        AlertDialog.Builder(this)
            .setTitle(titleResId)
            .setMessage(messageResId)
            .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                startNewGame()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}

// Bundle에 저장된 JSON 문자열을 GameState로 변환하는 보조 함수이다.
fun Bundle.loadGameState() : GameState? {
    val json = getString("gameState") ?: return null
    return GameState.fromJson(json)
}
