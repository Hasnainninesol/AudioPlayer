package com.example.audioplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.audioplayer.modelclass.Songs
import com.example.audioplayer.repository.AudioRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(application: Application, private val audioRepo: AudioRepo) :
    AndroidViewModel(application) {
    var songList: MutableLiveData<ArrayList<Songs>> = MutableLiveData()
    fun getAllSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            audioRepo.loadFiles()
        }.invokeOnCompletion {
            songList.postValue(audioRepo.getSongsList())
        }
    }
}