package com.example.audioplayer.activites

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioplayer.R
import com.example.audioplayer.REQUEST_WRITE_EXTERNAL_STORAGE
import com.example.audioplayer.adaptor.SongAdaptor
import com.example.audioplayer.appclass.BaseActivity
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.extension.isPermission
import com.example.audioplayer.extension.showToast
import com.example.audioplayer.interfaces.OptionClickListener
import com.example.audioplayer.services.broadcast.AlarmReceiver
import com.example.audioplayer.viewmodel.ViewModel.Companion.songList
import java.util.*


class MainActivity : BaseActivity(), OptionClickListener {
    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private lateinit var mAdaptor: SongAdaptor
    private lateinit var progressDialog: com.example.audioplayer.utils.ProgressDialog
    private lateinit var timePickerDialog: TimePickerDialog
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
        binding.btnSetAlarm.setOnClickListener { setAlarm() }
    }

    private fun initAdaptor() {
        with(binding) {
            audioRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
            mAdaptor = SongAdaptor(this@MainActivity, this@MainActivity)
            audioRecycler.adapter = mAdaptor
        }
    }

    private fun settingProgress() {
        progressDialog = com.example.audioplayer.utils.ProgressDialog(this)
    }

    private fun initObserver() {
        songList.observe(this@MainActivity) {
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

    override fun setOptions(position: Int, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.apply {
            menuInflater.inflate(R.menu.options, popupMenu.menu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setForceShowIcon(true)
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.btnSetAsRingTone -> {
                        setRingTone(position)
                    }
                    R.id.btnSetAsMessage -> {
                        setMessageTone(position)
                    }
                    R.id.btnDelete -> {
                        deleteSong(position)
                    }
                }
                true
            }
        }.show()


    }

    private fun deleteSong(position: Int) {
        val alertDialog = AlertDialog.Builder(this).apply {
            title = "Delete"
            setMessage("Are your sure to delete?")
            setIcon(R.drawable.ic_baseline_delete_24)
            setCancelable(true)
            setPositiveButton("Yes") { dialogInterface, i ->
                songList.value!!.removeAt(position)
                mAdaptor.notifyItemRemoved(position)
                mAdaptor.notifyItemRangeChanged(position, songList.value!!.size)
            }
        }.create()
        alertDialog.show()
    }

    private fun setAlarm() {
        openTimePickerDialog()
    }

    private fun setMessageTone(position: Int) {
        RingtoneManager.setActualDefaultRingtoneUri(
            this,
            RingtoneManager.TYPE_NOTIFICATION,
            songList.value!![position].uri
        )
        showToast("Notification Tone set successful")
    }

    private fun setRingTone(position: Int) {
        RingtoneManager.setActualDefaultRingtoneUri(
            this,
            RingtoneManager.TYPE_RINGTONE,
            songList.value!![position].uri
        )
        showToast("Ringtone set successful")
    }

    private fun openTimePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        timePickerDialog = TimePickerDialog(
            this@MainActivity,
            onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.setTitle("Set Alarm Time")
        timePickerDialog.show()
    }

    private var onTimeSetListener =
        OnTimeSetListener { view, hourOfDay, minute ->
            val calNow: Calendar = Calendar.getInstance()
            val calSet: Calendar = calNow.clone() as Calendar
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calSet.set(Calendar.MINUTE, minute)
            calSet.set(Calendar.SECOND, 0)
            calSet.set(Calendar.MILLISECOND, 0)
            if (calSet <= calNow) {
                calSet.add(Calendar.DATE, 1)
            }
            setAlarm(calSet)
        }

    private fun setAlarm(targetCal: Calendar) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1, intent, 0
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, pendingIntent)
        showToast("Alarm Set Successful")
    }
}