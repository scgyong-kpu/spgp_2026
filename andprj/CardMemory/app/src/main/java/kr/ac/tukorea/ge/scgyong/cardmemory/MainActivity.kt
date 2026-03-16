package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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

        // 개발 중에는 굳이 셔플하지 않고 고정된 순서로 테스트할 수 있도록 주석 처리한다.
        // 나중에 완성된 버전에서는 이 부분의 주석을 해제하여 게임이 시작될 때마다 카드가 무작위로 섞이도록 한다.
        // shuffleCardImages()
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
        startNewGame()
    }

    private fun startNewGame() {
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
        openedCardIndex = buttonIndex
    }
}
