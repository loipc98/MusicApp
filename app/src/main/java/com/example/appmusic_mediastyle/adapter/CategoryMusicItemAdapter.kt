package com.example.appmusic_mediastyle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic_mediastyle.R
import com.example.musicappmock.model.Song
import com.example.appmusic_mediastyle.model.Top100

class CategoryMusicItemAdapter(private val listCategoryMusicItem: List<Top100>, private val mIClickItemListener: IClickSongItemListener):
    RecyclerView.Adapter<CategoryMusicItemAdapter.CategoryMusicItemViewHolder>() {

    class CategoryMusicItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvCategoryMusic: TextView = itemView.findViewById(R.id.tv_category_music)
        val recyclerListSongs: RecyclerView = itemView.findViewById(R.id.recycler_list_songs)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMusicItemViewHolder {
        return CategoryMusicItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_music_item,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryMusicItemViewHolder, position: Int) {

        holder.tvCategoryMusic.text = listCategoryMusicItem[position].name
        setListSongsAdapter(holder.recyclerListSongs, listCategoryMusicItem[position].songs,holder)

    }

    private fun setListSongsAdapter(recyclerListSongs: RecyclerView, listSong: List<Song>, holder: CategoryMusicItemViewHolder) {
        val adapter = SongsItemAdapter(listSong,mIClickItemListener)
        recyclerListSongs.layoutManager = LinearLayoutManager(holder.itemView.context,RecyclerView.HORIZONTAL,false)
        recyclerListSongs.adapter  = adapter
    }

    override fun getItemCount(): Int {
        return listCategoryMusicItem.size
    }
}