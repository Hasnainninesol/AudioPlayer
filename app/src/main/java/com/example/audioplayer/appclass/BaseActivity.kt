package com.example.audioplayer.appclass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.mvvm.ViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {
    val mViewModel: ViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}