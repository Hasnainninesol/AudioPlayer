package com.example.audioplayer.activites

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.example.audioplayer.POSITION
import com.example.audioplayer.R
import com.example.audioplayer.appclass.BaseActivity
import com.example.audioplayer.databinding.ActivityPlayerBinding
import com.example.audioplayer.interfaces.ActionInterface
import com.example.audioplayer.services.MusicService
import com.example.audioplayer.viewmodel.ViewModel.Companion.songList

class PlayerActivity : BaseActivity(), ServiceConnection, ActionInterface {
    private var position = -1
    private lateinit var _binding: ActivityPlayerBinding
    private val binding get() = _binding
    private var myMusicService: MusicService = MusicService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observer()
        init()
        if (position != -1) {
            newSongPlay()
        }
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        with(binding)
        {
            btnPlay.setOnClickListener { btnPlay() }
            btnBack.setOnClickListener { backBtnClick() }
            btnNext.setOnClickListener { nextBtnClick() }
        }
        super.onResume()
    }

    private fun observer() {
        mViewModel.isPlay.observe(this) {
            if (it) {
                with(binding) { btnPlay.icon = getDrawable(R.drawable.ic_baseline_pause_24) }
            }
        }
        mViewModel.isPause.observe(this) {
            if (it) {
                with(binding) { btnPlay.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24) }
            }
        }
        mViewModel.songName.observe(this) {
            with(binding)
            {
                songTitle.text = it
            }
        }
        mViewModel.songPosition.observe(this) {
            position = it
        }
    }

    private fun init() {
        position = intent.getIntExtra(POSITION, -1)
        mViewModel.setPosition(position)
    }

    private fun play() {
        mViewModel.setIsPlay(true)
        mViewModel.setIsPause(false)
    }

    private fun pause() {
        with(mViewModel) {
            setIsPlay(false)
            setIsPause(true)
        }
    }

    private fun newSongPlay() {
        mViewModel.setSongName(songList.value!![position].name)
        mViewModel.setIsPause(false)
        mViewModel.setIsPlay(true)
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra(POSITION, position)
        }
        startService(intent)

    }

    override fun btnPlay() {
        if (myMusicService.isPlaying()) {
            myMusicService.pause()
            myMusicService.createNotification(R.drawable.ic_baseline_play_arrow_24, position)
            pause()
        } else {
            myMusicService.play()
            myMusicService.createNotification(R.drawable.ic_baseline_pause_24, position)
            play()
        }

    }

    override fun backBtnClick() {
        myMusicService.stop()
        position = ((position - 1) % songList.value!!.size)
        mViewModel.setSongName(songList.value!![position].name)
        play()
        myMusicService.createNotification(R.drawable.ic_baseline_pause_24, position)
        myMusicService.createMediaPlayer(position)
        myMusicService.play()
    }

    override fun nextBtnClick() {
        myMusicService.stop()
        position = ((position + 1) % songList.value!!.size)
        mViewModel.setSongName(songList.value!![position].name)
        play()
        myMusicService.createNotification(R.drawable.ic_baseline_pause_24, position)
        myMusicService.createMediaPlayer(position)
        myMusicService.play()
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val myBinder = p1 as MusicService.MyBinder
        myMusicService = myBinder.getServices()
        myMusicService.setCallBack(this)
        myMusicService.createNotification(R.drawable.ic_baseline_pause_24, position)
        myMusicService.play()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        myMusicService.apply { null }
    }
}
