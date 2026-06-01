package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.databinding.ActivityMainBinding
import kr.ac.tukorea.ge.spgp2026.taptu.databinding.SongItemBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val demoStopHandler = Handler(Looper.getMainLooper())
    private val demoStopRunnable = Runnable { stopDemo() }
    private var selectedPosition = RecyclerView.NO_POSITION
    private val songAdapter = SongAdapter { song, position ->
        Log.d(javaClass.simpleName, "song clicked: position=$position, $song")
        selectSong(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        SongCatalog.load(assets)
        Log.d(javaClass.simpleName, "loaded ${SongCatalog.songs.size} songs")
        SongCatalog.songs.take(3).forEach { song ->
            Log.d(javaClass.simpleName, "$song")
        }
        updatePreview(null, animated = false)

        val layoutManager = LinearLayoutManager(this)
        binding.songRecyclerView.layoutManager = layoutManager
        binding.songRecyclerView.addItemDecoration(
            DividerItemDecoration(this, layoutManager.orientation)
        )
        binding.songRecyclerView.adapter = songAdapter
        binding.startButton.setOnClickListener {
            val position = selectedPosition
            if (position != RecyclerView.NO_POSITION) {
                val intent = Intent(this, MainGameActivity::class.java)
                intent.putExtra(MainGameActivity.EXTRAS_SONG_INDEX, position)
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // MainActivity 가 background 로 가면 preview 용 음악과 선택 상태를 함께 정리한다.
        // 돌아왔을 때 선택 표시는 남아 있는데 미리듣기는 멈춘 상태가 되면 UI 의미가 애매해진다.
        stopDemo()
        clearSelection()
    }

    private fun selectSong(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = if (selectedPosition == position) {
            RecyclerView.NO_POSITION
        } else {
            position
        }
        songAdapter.selectedPosition = selectedPosition
        binding.startButton.isEnabled = selectedPosition != RecyclerView.NO_POSITION
        val selectedSong = SongCatalog.songs.getOrNull(selectedPosition)
        updatePreview(selectedSong, animated = true)
        playDemo(selectedSong)

        if (previousPosition != RecyclerView.NO_POSITION) {
            songAdapter.notifyItemChanged(previousPosition)
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            songAdapter.notifyItemChanged(selectedPosition)
        }
    }

    private fun clearSelection() {
        val previousPosition = selectedPosition
        if (previousPosition == RecyclerView.NO_POSITION) return

        selectedPosition = RecyclerView.NO_POSITION
        songAdapter.selectedPosition = RecyclerView.NO_POSITION
        binding.startButton.isEnabled = false
        updatePreview(null, animated = false)
        songAdapter.notifyItemChanged(previousPosition)
    }

    private fun playDemo(song: Song?) {
        stopDemo()
        if (song == null) return

        // assets 안의 mp3 는 res/raw 와 달리 resource id 로 열 수 없다.
        // AssetFileDescriptor 의 fileDescriptor/startOffset/length 를 MediaPlayer 에 넘겨야 한다.
        val afd = runCatching {
            assets.openFd(song.mp3AssetPath)
        }.getOrNull() ?: return

        mediaPlayer = MediaPlayer().apply {
            afd.use {
                setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }
            setOnCompletionListener {
                stopDemo()
            }
            prepare()
            if (song.demoStart > 0) {
                seekTo(song.demoStart)
            }
            start()
        }
        val demoDuration = song.demoEnd - song.demoStart
        if (demoDuration > 0) {
            demoStopHandler.postDelayed(demoStopRunnable, demoDuration.toLong())
        }
    }

    private fun stopDemo() {
        // demoStart~demoEnd 구간 재생을 위해 stopDemo() 를 예약해 두었을 수 있다.
        // 새 곡을 재생할 때 이전 곡의 예약 callback 이 남아 있으면,
        // 이전 곡의 종료 시간이 되었을 때 새 곡까지 갑자기 멈추는 버그가 생긴다.
        // 그래서 MediaPlayer 를 release 하기 전에 예약된 stop callback 을 항상 제거한다.
        demoStopHandler.removeCallbacks(demoStopRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updatePreview(song: Song?, animated: Boolean) {
        if (song == null) {
            binding.previewCoverImageView.setImageResource(R.mipmap.default_thumbnail)
            binding.previewTitleTextView.text = "Tap TU!"
            binding.previewArtistTextView.text = "Select a song"
            binding.previewAlbumTextView.text = ""
        } else {
            setThumbnail(binding.previewCoverImageView, song)
            binding.previewTitleTextView.text = song.title
            binding.previewArtistTextView.text = song.artist
            binding.previewAlbumTextView.text = song.album
        }

        if (!animated) return

        // 선택된 곡이 바뀌었음을 눈으로 느낄 수 있도록,
        // preview 전체를 잠깐 작고 투명하게 만든 뒤 원래 크기로 되돌린다.
        binding.previewContainer.animate().cancel()
        binding.previewContainer.alpha = 0.35f
        binding.previewContainer.scaleX = 0.96f
        binding.previewContainer.scaleY = 0.96f
        binding.previewContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(180L)
            .start()
    }

    private fun setThumbnail(imageView: ImageView, song: Song) {
        val thumbnail = song.loadThumbnail(assets)
        if (thumbnail == null) {
            imageView.setImageResource(R.mipmap.default_thumbnail)
        } else {
            imageView.setImageBitmap(thumbnail)
        }
    }

    private class SongAdapter(
        private val onSongClick: (Song, Int) -> Unit,
    ) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
        var selectedPosition: Int = RecyclerView.NO_POSITION

        class SongViewHolder(
            private val binding: SongItemBinding,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(song: Song, selected: Boolean, onSongClick: (Song, Int) -> Unit) {
                val thumbnail = song.loadThumbnail(binding.root.context.assets)
                if (thumbnail == null) {
                    binding.thumbnailImageView.setImageResource(R.mipmap.default_thumbnail)
                } else {
                    binding.thumbnailImageView.setImageBitmap(thumbnail)
                }
                binding.titleTextView.text = song.title
                binding.artistTextView.text = song.artist
                binding.albumTextView.text = song.album
                binding.root.isSelected = selected
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onSongClick(song, position)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
            // RecyclerView 가 새 row view 를 필요로 할 때 호출된다.
            // song_item.xml 을 ViewBinding 으로 inflate 해서 ViewHolder 에 넘긴다.
            val inflater = LayoutInflater.from(parent.context)
            val binding = SongItemBinding.inflate(inflater, parent, false)
            return SongViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            // 이미 만들어져 있거나 재사용된 row view 에 현재 position 의 Song 을 표시한다.
            holder.bind(
                SongCatalog.songs[position],
                position == selectedPosition,
                onSongClick
            )
        }

        override fun getItemCount(): Int {
            // RecyclerView 는 이 값을 보고 몇 개의 item 을 표시할지 판단한다.
            return SongCatalog.songs.size
        }
    }
}
