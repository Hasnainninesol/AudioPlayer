package com.example.audioplayer.services.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Toast.makeText(p0, "Alarm Received", Toast.LENGTH_LONG).show()
        val ringTone =
            RingtoneManager.getActualDefaultRingtoneUri(p0, RingtoneManager.TYPE_RINGTONE)
        val mediaPlayer = MediaPlayer.create(p0, ringTone)
        CoroutineScope(Dispatchers.Default).launch {
            mediaPlayer.start()
            delay(5000L)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
    }
}