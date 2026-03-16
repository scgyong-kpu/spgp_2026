package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View들을 안전하게 접근하기 위한 ViewBinding 객체
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardButtons: List<ImageButton>

    // 카드 이미지 리소스 ID 배열. 이 순서대로 카드 버튼에 이미지를 설정할 것이다.
    private val imageResIds = arrayOf(
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
        R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
        R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd,
    )
    private var openedCardIndex: Int? = null // 이전에 클릭된 카드 버튼의 인덱스를 저장하는 변수
    //private var openedCardIndex: Int = -1 // 정수와 -1 을 쓰는 것보다 null을 쓰는 것이 더 명확하다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LayoutInflater를 사용해 activity_main.xml을 ViewBinding 객체로 생성
        binding = ActivityMainBinding.inflate(layoutInflater)

        // binding.root는 activity_main.xml의 최상위 View이다.
        // setContentView에 전달하여 화면에 표시한다.
        setContentView(binding.root)

        cardButtons = listOf(
            binding.card00, binding.card01, binding.card02, binding.card03,
            binding.card10, binding.card11, binding.card12, binding.card13,
            binding.card20, binding.card21, binding.card22, binding.card23,
            binding.card30, binding.card31, binding.card32, binding.card33,
        )

        // 카드 버튼들에 클릭 리스너를 연결한다. 
        // 버튼의 인덱스를 여기서 전달해서 나중에 어떤 버튼이 클릭됐는지 Int 로 알 수 있게 한다.
        cardButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleCardClick(index)
            }
        }
    }

    // XML의 android:onClick 속성으로 연결된 카드 버튼 클릭 처리 함수이다.
    // layout XML에서 여러 ImageButton이 동일한 onClick 함수를 사용하므로
    // 어떤 버튼이 눌렸든 이 함수 하나로 전달된다.
    fun handleCardClick(buttonIndex: Int) {
        // buttonIndex 가 넘어왔으므로, 
        // cardButtons 배열에서 해당 인덱스의 버튼을 찾아서 button 을 바로 알 수 있다.
        val button = cardButtons[buttonIndex]

        // buttonIndex에 해당하는 카드 이미지 리소스 ID를 가져온다.
        val imgResId = imageResIds[buttonIndex]

        // openedCardIndex는 이전에 열려 있던 카드의 위치(인덱스)를 기억하는 변수이다.
        // 처음에는 아무 카드도 열려 있지 않으므로 null일 수 있다.
        openedCardIndex?.let { index ->
            // 이전에 열려 있던 카드 버튼을 가져온다.
            val openedButton = cardButtons[index]
            // 이전에 열려 있던 카드에 보여주어야 하는 이미지 리소스 ID를 가져온다.
            val openedImgResId = imageResIds[index]

            if (openedImgResId == imgResId) {
                // 이전에 열려 있던 카드와 현재 클릭된 카드가 같은 이미지라면
                // 두 카드를 맞춘 것으로 간주하고 삭제를 시도해 본다
                openedButton.visibility = View.GONE
                button.visibility = View.GONE
            } else {
                // 이전에 열려 있던 카드와 현재 클릭된 카드가 다른 이미지라면
                // 이전에 열려 있던 카드를 다시 뒷면으로 돌린다.
                openedButton.setImageResource(R.mipmap.card_blue_back)
            }
        }

        // 현재 클릭된 카드의 이미지를 보여준다.
        button.setImageResource(imgResId)

        // 마지막 연 카드를 저장하는 코드를 그대로 둔다
        openedCardIndex = buttonIndex
    }
}

