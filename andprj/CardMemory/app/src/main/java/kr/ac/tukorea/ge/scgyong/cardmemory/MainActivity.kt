package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View들을 안전하게 접근하기 위한 ViewBinding 객체
    private lateinit var binding: ActivityMainBinding
    private var prevImageButton: ImageButton? = null // 이전에 클릭된 카드 버튼을 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LayoutInflater를 사용해 activity_main.xml을 ViewBinding 객체로 생성
        binding = ActivityMainBinding.inflate(layoutInflater)

        // binding.root는 activity_main.xml의 최상위 View이다.
        // setContentView에 전달하여 화면에 표시한다.
        setContentView(binding.root)

        // 이제 XML에 있는 View들을 findViewById 없이 직접 접근할 수 있다.
        // 예: binding.flipCountText.text = "0"
    }

    // XML의 android:onClick 속성으로 연결된 카드 버튼 클릭 처리 함수이다.
    fun onCardClicked(v: View) {
        // 클릭된 View가 ImageButton이 아니면 (그럴 리는 없지만) 함수 종료
        val button = v as? ImageButton ?: return

        // 이전에 클릭된 카드 버튼이 있으면 뒤집어서 카드 뒷면으로 변경
        prevImageButton?.setImageResource(R.mipmap.card_blue_back)

        // Java 로 수업을 할 때에는
        // prevImageButton이 null인지 체크하는 if문이 필요했지만,
        // Kotlin에서는 안전한 호출 연산자(?.)를 사용하여 간단하게 처리할 수 있다.

        button.setImageResource(R.mipmap.card_as) // 클릭된 카드 버튼을 Ace of Spades로 변경
        prevImageButton = button // 현재 클릭된 카드 버튼을 이전 카드 버튼으로 저장
    }
}
