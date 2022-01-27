package com.example.audioplayer.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.databinding.SongitemBinding
import com.example.audioplayer.modelclass.Songs

class SongAdaptor(val context: Context) :
    RecyclerView.Adapter<SongAdaptor.MyViewHolder>() {
    private var mData = ArrayList<Songs>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SongitemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder)
        {
            with(binder)
            {
                tvSongName.text = mData[position].name
                btnPlay.setOnClickListener {
                    it.visibility = View.GONE
                    btnPause.visibility = View.VISIBLE
                }
                btnPause.setOnClickListener {
                    it.visibility = View.GONE
                    btnPlay.visibility = View.VISIBLE
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return if (mData.isNotEmpty()) mData.size else 0
    }

    fun updateList(it: java.util.ArrayList<Songs>) {
        this.mData = it
        notifyDataSetChanged()
    }

    class MyViewHolder(val binder: SongitemBinding) : RecyclerView.ViewHolder(binder.root)
}