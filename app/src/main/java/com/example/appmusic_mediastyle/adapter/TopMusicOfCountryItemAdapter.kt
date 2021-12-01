package com.example.appmusic_mediastyle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic_mediastyle.R
import com.example.appmusic_mediastyle.model.Top100
import com.example.musicappmock.model.Top100OfCountry

class TopMusicOfCountryItemAdapter(private val listTopMusicOfCountryItem: List<Top100OfCountry>, private val mIClickItemListener: IClickSongItemListener) :
    RecyclerView.Adapter<TopMusicOfCountryItemAdapter.CategoryMusicItemViewHolder>() {

    class CategoryMusicItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTopMusicCountry: TextView = itemView.findViewById(R.id.tv_top_music_country)
        val recyclerCategoryMusic: RecyclerView = itemView.findViewById(R.id.recycler_category_music)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMusicItemViewHolder {
        return CategoryMusicItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.top_music_of_country_item,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryMusicItemViewHolder, position: Int) {
        holder.tvTopMusicCountry.text = listTopMusicOfCountryItem[position].musicOfCountry
        setCategoryMusicOfCountry(holder.recyclerCategoryMusic,listTopMusicOfCountryItem[position].top100Songs,holder)
    }

    private fun setCategoryMusicOfCountry(recyclerCategoryMusic: RecyclerView, listCategoryMusic: List<Top100>, holder: CategoryMusicItemViewHolder) {
        val itemCategoryMusicAdapter = CategoryMusicItemAdapter(listCategoryMusic,mIClickItemListener)
        recyclerCategoryMusic.layoutManager = LinearLayoutManager(holder.itemView.context)
        recyclerCategoryMusic.adapter = itemCategoryMusicAdapter
    }

    override fun getItemCount(): Int {
        return listTopMusicOfCountryItem.size
    }
}
