package com.example.appmusic_mediastyle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic_mediastyle.R
import com.example.musicappmock.model.Photo

class PhotoAdapter(private val listPhoto:List<Photo>): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgTopMusic: ImageView = itemView.findViewById(R.id.img_top_music)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_item,parent,false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imgTopMusic.setImageResource(listPhoto[position].resourceId)
    }

    override fun getItemCount(): Int {
        return listPhoto.size
    }

}