package com.example.audioplayer.activites

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioplayer.REQUEST_WRITE_EXTERNAL_STORAGE
import com.example.audioplayer.adaptor.SongAdaptor
import com.example.audioplayer.appclass.BaseActivity
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.extension.isPermission

class MainActivity : BaseActivity() {
    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private lateinit var mAdaptor: SongAdaptor
    private lateinit var progressDialog: com.example.audioplayer.utils.ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingProgress()
        isPermission()
        initAdaptor()
        initObserver()
        if (isPermission) {
            loadSongs()
        }
    }

    private fun initAdaptor() {
        with(binding) {
            audioRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
            mAdaptor = SongAdaptor(this@MainActivity)
            audioRecycler.adapter = mAdaptor
        }
    }

    private fun settingProgress() {
        progressDialog = com.example.audioplayer.utils.ProgressDialog(this)
    }

    private fun initObserver() {
        mViewModel.songList.observe(this@MainActivity) {
            binding.tvSize.text = "Total Songs: ${it.size}"
            progressDialog.dismiss()
            mAdaptor.updateList(it)
        }
    }

    private fun loadSongs() {
        progressDialog.showWithMessage("Loading Songs ... ")
        mViewModel.getAllSongs()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs()
            } else {
                isPermission()
            }
        }
    }

}