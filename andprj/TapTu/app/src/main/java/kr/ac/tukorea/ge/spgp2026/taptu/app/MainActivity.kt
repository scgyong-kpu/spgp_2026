package kr.ac.tukorea.ge.spgp2026.taptu.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.tukorea.ge.spgp2026.taptu.R
import kr.ac.tukorea.ge.spgp2026.taptu.data.Song
import kr.ac.tukorea.ge.spgp2026.taptu.data.SongCatalog
import kr.ac.tukorea.ge.spgp2026.taptu.databinding.ActivityMainBinding
import kr.ac.tukorea.ge.spgp2026.taptu.databinding.SongItemBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

        binding.songRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songRecyclerView.adapter = songAdapter
    }

    private fun selectSong(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = if (selectedPosition == position) {
            RecyclerView.NO_POSITION
        } else {
            position
        }
        songAdapter.selectedPosition = selectedPosition

        if (previousPosition != RecyclerView.NO_POSITION) {
            songAdapter.notifyItemChanged(previousPosition)
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            songAdapter.notifyItemChanged(selectedPosition)
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
