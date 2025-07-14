package com.example.skinvision.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skinvision.R
import com.example.skinvision.data.DiagnosisHistory
import com.example.skinvision.databinding.ItemDiagnosisHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class DiagnosisHistoryAdapter(
    private var historyList: MutableList<DiagnosisHistory>,
    private val onItemClick: (DiagnosisHistory) -> Unit,
    private val onDeleteClick: (DiagnosisHistory, Int) -> Unit
) : RecyclerView.Adapter<DiagnosisHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemDiagnosisHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemDiagnosisHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val diagnosis = historyList[position]

        with(holder.binding) {
            // Set diagnosis info
            tvDiagnosisResult.text = diagnosis.prediction
            tvConfidenceScore.text = "Confidence: ${diagnosis.confidence.toInt()}%"

            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
            tvDiagnosisDate.text = dateFormat.format(Date(diagnosis.timestamp))

            // Set confidence badge
            val confidenceLevel = diagnosis.getConfidenceLevel()
            tvConfidenceBadge.text = confidenceLevel

            val badgeBackground = when (confidenceLevel) {
                "HIGH" -> R.drawable.confidence_badge_high
                "MEDIUM" -> R.drawable.confidence_badge_medium
                else -> R.drawable.confidence_badge_low
            }
            tvConfidenceBadge.setBackgroundResource(badgeBackground)

            // Set image
            diagnosis.image?.let { bitmap ->
                ivDiagnosisImage.setImageBitmap(bitmap)
            } ?: run {
                ivDiagnosisImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Click listeners
            btnViewDetails.setOnClickListener {
                onItemClick(diagnosis)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(diagnosis, position)
            }

            root.setOnClickListener {
                onItemClick(diagnosis)
            }
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun removeItem(position: Int) {
        historyList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, historyList.size)
    }

    fun updateHistory(newHistory: List<DiagnosisHistory>) {
        historyList.clear()
        historyList.addAll(newHistory)
        notifyDataSetChanged()
    }
}
