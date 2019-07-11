package com.example.gamebox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamebox.R
import com.example.gamebox.models.Game

class GamerAdapter(
    val context: Context, val data: MutableList<Game>,
    private val onItemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<GamerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pictureImageView: ImageView = itemView.findViewById(R.id.game_picture)
        val nameTextView: TextView = itemView.findViewById(R.id.game_name)
        val iconImageView: ImageView = itemView.findViewById(R.id.game_icon_playable)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater
            .from(context)
            .inflate(R.layout.gamelist_item, parent, false)
        rowView.setOnClickListener(onItemClickListener)
        val viewHolder = ViewHolder(rowView)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]
        Glide.with(context)
            .load(currentItem.picture)
            .into(holder.pictureImageView)
        holder.nameTextView.text = currentItem.name
        if (currentItem.playable)
            holder.iconImageView.visibility = View.VISIBLE
        holder.itemView.tag = position
    }

}