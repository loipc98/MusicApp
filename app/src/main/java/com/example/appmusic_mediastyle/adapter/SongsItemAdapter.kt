package com.example.appmusic_mediastyle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmusic_mediastyle.R
import com.example.musicappmock.model.Song

class SongsItemAdapter(private val listSongItem: List<Song>,private val mIClickItemListener: IClickSongItemListener) : RecyclerView.Adapter<SongsItemAdapter.SongItemViewHolder>() {

    class SongItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSong: ImageView = itemView.findViewById(R.id.img_song)
        val tvSongName: TextView = itemView.findViewById(R.id.tv_song_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        return SongItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.song_row_item,parent,false))
    }

    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        holder.tvSongName.text = listSongItem[position].title
        Glide.with(holder.itemView).load(listSongItem[position].avatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loaderror)
            .into(holder.imgSong)

        holder.itemView.setOnClickListener {
            mIClickItemListener.onClickItemSong(listSongItem,position)
        }
    }

    override fun getItemCount(): Int {
        return listSongItem.size
    }

}

interface IClickSongItemListener {
    fun onClickItemSong(listSongs: List<Song>, position: Int)
}