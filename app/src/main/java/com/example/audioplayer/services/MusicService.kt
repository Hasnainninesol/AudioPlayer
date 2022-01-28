package com.example.audioplayer.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.audioplayer.*
import com.example.audioplayer.activites.PlayerActivity
import com.example.audioplayer.interfaces.ActionInterface
import com.example.audioplayer.modelclass.Songs
import com.example.audioplayer.services.broadcast.NotificationReceiver
import com.example.audioplayer.viewmodel.ViewModel.Companion.songList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


class MusicService : Service() {
    private var mBinder: IBinder = MyBinder()
    private lateinit var exoPlayer: ExoPlayer
    private var mediaSeason: MediaSessionCompat? = null
    private var actionPlay: ActionInterface? = null
    private var musicFiles: ArrayList<Songs> = ArrayList()
    private var uri: Uri? = null
    private var position = -1
    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    inner class MyBinder : Binder() {
        fun getServices(): MusicService {
            return this@MusicService
        }
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSeason = MediaSessionCompat(this, "My Audio")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        position = intent!!.extras!!.getInt(POSITION, -1)
        if (position != -1) {
            playMedia(position)
        }
        val actionName = intent.getStringExtra("ActionName")
        if (uri != null) {
            prepare()
            play()
        }
        if (actionName != null) {
            when (actionName) {
                "playPause" -> btnPlayClick()
                "next" -> btnNextClick()
                "previous" -> btnPreClick()
            }
        }
        return START_STICKY
    }

    private fun btnPlayClick() {
        actionPlay!!.btnPlay()
    }

    private fun btnNextClick() {
        actionPlay!!.nextBtnClick()
    }

    private fun btnPreClick() {
        actionPlay!!.backBtnClick()
    }

    private fun playMedia(position: Int) {
        musicFiles = songList.value as ArrayList<Songs>
        this.position = position
        createMediaPlayer(this.position)
        play()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(playPause: Int, position: Int) {
        this.position = position
     /*   val intent = Intent(this, PlayerActivity::class.java)
        val contentPending = PendingIntent.getActivity(this, 0, intent, 0)*/
        val pauseIntent =
            Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        val pausePending =
            PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PREV)
        val prevPending =
            PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_NEXT)
        val nextPending =
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(playPause)
                .setContentTitle(musicFiles[position].name)
                .addAction(R.drawable.ic_baseline_arrow_back_24, "Previous", prevPending)
                .addAction(playPause, "Pause", pausePending)
                .addAction(R.drawable.ic_baseline_arrow_forward_24, "Next", nextPending)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
               /* .setContentIntent(contentPending)*/
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSeason!!.sessionToken)
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        startForeground(1, notification)
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    private fun prepare() {
        exoPlayer.prepare()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun play() {
        exoPlayer.play()
    }


    fun setCallBack(actionPlay: ActionInterface) {
        this.actionPlay = actionPlay
    }

    fun createMediaPlayer(pos: Int) {
        uri = musicFiles[pos].uri
        val mediaItem: MediaItem =
            MediaItem.fromUri(this.uri!!)
        exoPlayer.setMediaItem(mediaItem)
        prepare()
    }

    fun stop() {
        exoPlayer.stop()
    }

    private fun release() {
        exoPlayer.release()
    }
}