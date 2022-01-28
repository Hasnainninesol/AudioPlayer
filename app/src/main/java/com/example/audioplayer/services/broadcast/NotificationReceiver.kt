package com.example.audioplayer.services.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.audioplayer.ACTION_NEXT
import com.example.audioplayer.ACTION_PLAY
import com.example.audioplayer.ACTION_PREV
import com.example.audioplayer.services.MusicService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val actionname: String = intent.action.toString()
        val serviceIntent = Intent(context, MusicService::class.java)
        when (actionname) {
            ACTION_PLAY -> {
                serviceIntent.putExtra("ActionName", "playPause")
                context.startService(serviceIntent)
            }
            ACTION_NEXT -> {
                serviceIntent.putExtra("ActionName", "next")
                context.startService(serviceIntent)
            }
            ACTION_PREV -> {
                serviceIntent.putExtra("ActionName", "previous")
                context.startService(serviceIntent)
            }
        }
    }
}