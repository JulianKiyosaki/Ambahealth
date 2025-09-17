package com.dicoding.asclepius.room

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val historyList: List<PredictionHistory>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: PredictionHistory) {
            Glide.with(binding.root.context)
                .load(Uri.parse(history.imageUri))
                .placeholder(R.drawable.ic_place_holder)
                .into(binding.historyImage)

            binding.historyTitle.text = "Check-Up ${adapterPosition + 1}"
            binding.historyDescription.text = if (history.prediction) {
                "Cancer Percentage: ${(history.confidenceScore * 100).toInt()}%"
            } else {
                "No Cancer, Percentage: ${(history.confidenceScore * 100).toInt()}%"
            }

            binding.historyDate.text = "Date: ${formatDate(history.createdAt)}"

            binding.root.setOnLongClickListener {
                onDeleteClick(history.id)
                true
            }

            binding.deleteHistoryButton.setOnClickListener {
                onDeleteClick(history.id)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}