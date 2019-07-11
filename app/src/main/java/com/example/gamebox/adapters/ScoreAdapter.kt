package com.example.gamebox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebox.R
import com.example.gamebox.models.ScoreItem

class ScoreAdapter(
    val context: Context, val data: MutableList<ScoreItem>
) :
    RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.scoreitem_date)
        val playerTextView: TextView = itemView.findViewById(R.id.scoreitem_player)
        val gameTextView: TextView = itemView.findViewById(R.id.scoreitem_game)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreitem_result)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = LayoutInflater
            .from(context)
            .inflate(R.layout.scorelist_item, parent, false)
        val viewHolder = ViewHolder(rowView)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]
        holder.dateTextView.text = (currentItem.date).replace('T', 'C')
            .replace('C', ' ')
            .subSequence(0, currentItem.date.indexOf('.'))

        holder.gameTextView.text = currentItem.game
        holder.playerTextView.text = currentItem.player
        holder.scoreTextView.text = currentItem.score
    }

}