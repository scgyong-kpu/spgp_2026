package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View들을 안전하게 접근하기 위한 ViewBinding 객체
    // 처음 접근할 때 lazy block 이 실행되어 초기화되는 것을 이용한다.
    private val binding by lazy {
         ActivityMainBinding.inflate(layoutInflater)
    }

    // Kotlin 의 lazy 속성을 이용해 카드 버튼들을 리스트로 모은다. 실제로 처음 접근할 때 초기화된다.
    // cardButtons 가 처음 접근될 때 binding 객체가 이미 초기화되어 있어야 하므로 lazy 없이는 쓸 수 없다.
    // Java 로 했다면 getter 함수를 만들어서 그 안에서 초기화하는 방식으로 구현했을 것이다.
    private val cardButtons by lazy {
        listOf(
            binding.card00, binding.card01, binding.card02, binding.card03,
            binding.card10, binding.card11, binding.card12, binding.card13,
            binding.card20, binding.card21, binding.card22, binding.card23,
            binding.card30, binding.card31, binding.card32, binding.card33,
        )
    }
    private var openedCardIndex: Int? = null // 이전에 클릭된 카드 버튼의 인덱스를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
        openedCardIndex?.let {
            // openedCardIndex가 null이 아니면 let 블록이 실행된다.
            // it 변수에는 openedCardIndex의 값이 들어 있다.

            // 이전에 클릭된 카드가 있다면 그 카드를 다시 뒷면으로 돌린다.
            // cardButtons 배열에서 인덱스를 이용해 해당 버튼을 다시 얻을 수 있다.
            cardButtons[it].setImageResource(R.mipmap.card_blue_back)
        }

        // let 블록은 아래처럼 it 변수를 사용하지 않고 openedCardIndex를 직접 참조하는 방식으로도 작성할 수 있다.
        // openedCardIndex?.let { index ->
        //     cardButtons[index].setImageResource(R.mipmap.card_blue_back)
        // }

        // 현재 클릭된 버튼의 이미지를 Ace of Spades로 변경한다.
        button.setImageResource(R.mipmap.card_as)

        // 클릭된 버튼이 cardButtons 배열의 몇 번째인지 찾아서 저장한다.
        // 이렇게 버튼 객체 자체를 저장하는 대신 "인덱스"를 저장하면
        // 게임 상태를 관리하거나 저장/복원(Bundle, JSON 등)할 때 더 유리하다.
        openedCardIndex = cardButtons.indexOf(button)
    }
}

