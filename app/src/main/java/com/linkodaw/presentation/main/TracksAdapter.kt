package com.linkodaw.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkodaw.domain.model.Track
import com.linkodaw.R

class TracksAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onDeleteClick: (Track) -> Unit
) : ListAdapter<Track, TracksAdapter.TrackViewHolder>(TrackDiffCallback()) {

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTrackName)
        val tvDuration: TextView = view.findViewById(R.id.tvTrackDuration)
        val tvDate: TextView = view.findViewById(R.id.tvTrackDate)
        val btnDelete: View = view.findViewById(R.id.btnDeleteTrack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.tvName.text = track.name
        holder.tvDuration.text = track.formattedDuration
        holder.tvDate.text = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", track.createdAt)
        holder.itemView.setOnClickListener { onTrackClick(track) }
        holder.btnDelete.setOnClickListener { onDeleteClick(track) }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem == newItem
    }
}