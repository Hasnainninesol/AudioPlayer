package com.example.audioplayer.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.audioplayer.modelclass.Songs

class AudioRepo(private val context: Context) {
    private val songList = ArrayList<Songs>()
    fun loadFiles() {
        songList.clear()
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.DATA
        )
        val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
        cursor?.let {
            it.moveToFirst()
            while (it.moveToNext()) {
                var songModel: Songs
                val songID =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID))
                var songName =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE))
                val id = Uri.withAppendedPath(contentUri, songID)
                var songPath =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))
                if (songName == null) songName = "Null"
                if (songPath == null) songPath = "Null"
                songModel = Songs(songName, id, songPath)
                songList.add(songModel)
            }
        }
        cursor?.close()
    }

    fun getSongsList(): java.util.ArrayList<Songs>? {
        return songList
    }
}