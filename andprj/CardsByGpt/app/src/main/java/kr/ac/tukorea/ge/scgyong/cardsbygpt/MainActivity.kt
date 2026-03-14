    package kr.ac.tukorea.ge.scgyong.cardsbygpt

    import android.content.DialogInterface
    import android.os.Bundle
    import android.widget.ImageButton
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import kr.ac.tukorea.ge.scgyong.cardsbygpt.databinding.ActivityMainBinding

    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding

        private lateinit var cardButtons: List<ImageButton>

        // 각 버튼에 배정된 카드 앞면 이미지 리소스
        private val cardFaces = mutableListOf<Int>()

        // 현재 열려 있는 카드의 인덱스 (없으면 null)
        private var prevIndex: Int? = null

        // 뒤집은 횟수
        private var flipCount = 0

        // 카드 앞면 리소스 8종
        private val cardImageIds = listOf(
            R.mipmap.card_as, R.mipmap.card_2c, R.mipmap.card_3d, R.mipmap.card_4h,
            R.mipmap.card_5s, R.mipmap.card_jc, R.mipmap.card_qh, R.mipmap.card_kd
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            cardButtons = listOf(
                binding.card0, binding.card1, binding.card2, binding.card3,
                binding.card4, binding.card5, binding.card6, binding.card7,
                binding.card8, binding.card9, binding.card10, binding.card11,
                binding.card12, binding.card13, binding.card14, binding.card15
            )

            initCardButtons()

            binding.restartButton.setOnClickListener {
                showRestartDialog(
                    R.string.restart_title,
                    R.string.restart_message
                )
            }

            startGame()
        }

        private fun initCardButtons() {
            for (i in cardButtons.indices) {
                cardButtons[i].setOnClickListener {
                    onCardClicked(i)
                }
            }
        }

        private fun startGame() {
            flipCount = 0
            prevIndex = null
            updateFlipCount()

            buildAndShuffleDeck()

            for (button in cardButtons) {
                button.setImageResource(R.mipmap.card_blue_back)
                button.visibility = ImageButton.VISIBLE
            }
        }

        private fun buildAndShuffleDeck() {
            cardFaces.clear()

            // 8종 카드 2장씩 넣어서 16장 구성
            for (imageId in cardImageIds) {
                cardFaces.add(imageId)
                cardFaces.add(imageId)
            }

            cardFaces.shuffle()
        }

        private fun onCardClicked(index: Int) {
            val button = cardButtons[index]

            // 이미 제거된 카드면 무시
            if (button.visibility != ImageButton.VISIBLE) return

            // 이미 열려 있는 카드 다시 누른 경우
            if (isCardOpen(index)) {
                Toast.makeText(this, R.string.already_open_card, Toast.LENGTH_SHORT).show()
                return
            }

            // 현재 카드 열기
            openCard(index)
            flipCount++
            updateFlipCount()

            // 첫 번째 카드인 경우
            if (prevIndex == null) {
                prevIndex = index
                return
            }

            val oldIndex = prevIndex!!

            // 같은 위치를 또 눌렀는지 방어
            if (prevIndex == index) {
                Toast.makeText(this, R.string.already_open_card, Toast.LENGTH_SHORT).show()
                return
            }

            if (cardFaces[oldIndex] == cardFaces[index]) {
                // 두 카드가 같으면 제거
                removeCard(oldIndex)
                removeCard(index)
                prevIndex = null

                if (isGameFinished()) {
                    showRestartDialog(
                        R.string.clear_title,
                        R.string.clear_message
                    )
                }
            } else {
                // 다르면 이전 카드는 닫고, 현재 카드는 열어 둔다
                closeCard(oldIndex)
                prevIndex = index
            }
        }

        private fun isCardOpen(index: Int): Boolean {
            return prevIndex == index
        }
        private fun openCard(index: Int) {
            val button = cardButtons[index]
            button.setImageResource(cardFaces[index])
        }

        private fun closeCard(index: Int) {
            val button = cardButtons[index]
            button.setImageResource(R.mipmap.card_blue_back)
        }

        private fun removeCard(index: Int) {
            val button = cardButtons[index]
            button.visibility = ImageButton.INVISIBLE
        }

        private fun isGameFinished(): Boolean {
            return cardButtons.all { it.visibility != ImageButton.VISIBLE }
        }

        private fun updateFlipCount() {
            binding.flipCountText.text = getString(R.string.flip_count_format, flipCount)
        }

        private fun showRestartDialog(titleResId: Int, messageResId: Int) {
            AlertDialog.Builder(this)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                    startGame()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }
