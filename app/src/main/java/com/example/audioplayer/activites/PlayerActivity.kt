package com.example.audioplayer.activites

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import com.example.audioplayer.POSITION
import com.example.audioplayer.appclass.BaseActivity
import com.example.audioplayer.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class PlayerActivity : BaseActivity(), Player.Listener {
    private var position = -1
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var _binding: ActivityPlayerBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        if (position != -1) {
            newSongPlay()
        }
        with(binding)
        {
            btnPlay.setOnClickListener { playSong() }
            btnPause.setOnClickListener { pauseSong() }
        }
        with(binding) {
            seekBar.max = exoPlayer.contentDuration.toInt()
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    exoPlayer.seekTo(p1.toLong())
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    TODO("Not yet implemented")
                }

            })
        }
        Handler().post {
            with(binding) {
                seekBar.progress = exoPlayer.currentPosition.toInt()
            }
        }


    }

    private fun init() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(this)
        position = intent.getIntExtra(POSITION, -1)
    }

    private fun pauseSong() {
        with(binding)
        {
            btnPause.visibility = View.GONE
            btnPlay.visibility = View.VISIBLE
        }
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    private fun playSong() {
        with(binding)
        {
            btnPause.visibility = View.VISIBLE
            btnPlay.visibility = View.GONE
        }
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    private fun newSongPlay() {
        with(binding)
        {
            songTitle.text = mViewModel.songList.value!![position].name
            btnPause.visibility = View.VISIBLE
            btnPlay.visibility = View.GONE
        }
        if (exoPlayer.isPlaying) {
            exoPlayer.release()
        }
        val mediaItem: MediaItem = MediaItem.fromUri(mViewModel.songList.value!![position].uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer.isPlaying) {
            exoPlayer.release()
        }
    }

}