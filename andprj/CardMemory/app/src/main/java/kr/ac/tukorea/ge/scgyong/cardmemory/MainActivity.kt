package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View들을 안전하게 접근하기 위한 ViewBinding 객체
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardButtons: List<ImageButton>
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
    }

    // XML의 android:onClick 속성으로 연결된 카드 버튼 클릭 처리 함수이다.
    // layout XML에서 여러 ImageButton이 동일한 onClick 함수를 사용하므로
    // 어떤 버튼이 눌렸든 이 함수 하나로 전달된다.
    fun onCardClicked(v: View) {

        // 클릭된 View 객체를 ImageButton으로 형변환한다.
        // onClick의 매개변수 타입은 View이기 때문에 ImageButton 전용 함수
        // (예: setImageResource)를 사용하려면 캐스팅이 필요하다.
        // as? 는 안전 캐스팅으로, 실패하면 null을 반환한다.
        val button = v as? ImageButton ?: return

        // openedCardIndex는 이전에 열려 있던 카드의 위치(인덱스)를 기억하는 변수이다.
        // 처음에는 아무 카드도 열려 있지 않으므로 null일 수 있다.
        if (openedCardIndex != null) {

            // 이전에 클릭된 카드가 있다면 그 카드를 다시 뒷면으로 돌린다.
            // cardButtons 배열에서 인덱스를 이용해 해당 버튼을 다시 얻을 수 있다.
            // !! 는 "null이 아님을 확신한다"는 의미의 연산자이다.
            // 위에서 null 체크를 했기 때문에 여기서는 안전하다고 가정한다.
            cardButtons[openedCardIndex!!].setImageResource(R.mipmap.card_blue_back)

        } else {
            // 아직 열린 카드가 없는 첫 클릭 상황
            // 특별히 할 일은 없지만 흐름 설명을 위해 남겨 둔 분기
            null
        }

        // 현재 클릭된 버튼의 이미지를 Ace of Spades로 변경한다.
        button.setImageResource(R.mipmap.card_as)

        // 클릭된 버튼이 cardButtons 배열의 몇 번째인지 찾아서 저장한다.
        // 이렇게 버튼 객체 자체를 저장하는 대신 "인덱스"를 저장하면
        // 게임 상태를 관리하거나 저장/복원(Bundle, JSON 등)할 때 더 유리하다.
        openedCardIndex = cardButtons.indexOf(button)
    }}

