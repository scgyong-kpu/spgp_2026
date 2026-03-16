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

        shuffleCardImages()
    }

    private fun shuffleCardImages() {
        // Fisher-Yates 방식으로 카드 배치를 섞는다.
        imageResIds.forEachIndexed { index, _ ->
            val randomIndex = (index..imageResIds.size - 1).random()
            val temp = imageResIds[index]
            imageResIds[index] = imageResIds[randomIndex]
            imageResIds[randomIndex] = temp
        }
        Log.d("MainActivity", "Shuffled imageResIds: ${imageResIds.joinToString(",")}")
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
