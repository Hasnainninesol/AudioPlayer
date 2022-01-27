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
        observer()
        if (position != -1) {
            newSongPlay()
        }
        with(binding)
        {
            btnPlay.setOnClickListener { playSong() }
            btnPause.setOnClickListener { pauseSong() }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    exoPlayer.seekTo(p1.toLong())

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    exoPlayer.play()
                }

            })
        }
        runOnUiThread {
            Handler().postDelayed({
                if (exoPlayer.isPlaying) {
                    val mCurrentPosition: Int = (exoPlayer.currentPosition.toInt())
                    binding.seekBar.progress = mCurrentPosition
                    getCurrentTimer()
                }
            }, 500)
        }


    }

    private fun observer() {
        mViewModel.isPlay.observe(this) {
            if (it) {
                with(binding) {
                    btnPause.visibility = View.VISIBLE
                    btnPlay.visibility = View.GONE
                }
            }
        }
        mViewModel.isPause.observe(this) {
            if (it) {
                with(binding) {
                    btnPause.visibility = View.GONE
                    btnPlay.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun init() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(this)
        position = intent.getIntExtra(POSITION, -1)
    }

    private fun pauseSong() {
        if (exoPlayer.isPlaying) {
            mViewModel.setIsPlay(false)
            mViewModel.setIsPause(true)
            exoPlayer.pause()
        }
    }

    private fun playSong() {
        if (!exoPlayer.isPlaying) {
            mViewModel.setIsPlay(true)
            mViewModel.setIsPause(false)
            exoPlayer.play()
        }
    }

    private fun newSongPlay() {
        with(binding)
        {
            songTitle.text = mViewModel.songList.value!![position].name
            mViewModel.setIsPause(false)
            mViewModel.setIsPlay(true)
        }
        if (exoPlayer.isPlaying) {
            exoPlayer.release()
        }
        val mediaItem: MediaItem = MediaItem.fromUri(mViewModel.songList.value!![position].uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        getDurationTimer()

    }

    private fun getDurationTimer() {
        binding.seekBar.max = (exoPlayer.duration / 1000).toInt()
        val minutes: Long = exoPlayer.duration / 1000 / 60
        val seconds = (exoPlayer.duration / 1000 % 60).toInt()
        binding.totalTime.text = "$minutes:$seconds"
    }

    private fun getCurrentTimer() {
        val mMinutes = (exoPlayer.duration / 1000) / 60
        val mSeconds = ((exoPlayer.duration / 1000) % 60)
        binding.currentTime.text = "$mMinutes:$mSeconds"
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }


}