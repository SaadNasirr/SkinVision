package com.example.skinvision

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skinvision.adapters.DiagnosisHistoryAdapter
import com.example.skinvision.data.DiagnosisHistory
import com.example.skinvision.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: DiagnosisHistoryAdapter
    private lateinit var historyManager: HistoryManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyManager = HistoryManager(requireContext())
        setupRecyclerView()
        loadHistory()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnClearHistory.setOnClickListener {
            showClearHistoryDialog()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = DiagnosisHistoryAdapter(
            historyList = mutableListOf(),
            onItemClick = { diagnosis ->
                showDiagnosisDetails(diagnosis)
            },
            onDeleteClick = { diagnosis, position ->
                showDeleteConfirmation(diagnosis, position)
            }
        )

        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun loadHistory() {
        val history = historyManager.getAllHistory()
        if (history.isEmpty()) {
            showEmptyState()
        } else {
            showHistoryList()
            historyAdapter.updateHistory(history)
        }
    }

    private fun showEmptyState() {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
    }

    private fun showHistoryList() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.recyclerViewHistory.visibility = View.VISIBLE
    }

    private fun showDiagnosisDetails(diagnosis: DiagnosisHistory) {
        val message = """
            Diagnosis: ${diagnosis.prediction}
            Confidence: ${diagnosis.confidence.toInt()}%
            Date: ${java.text.SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", java.util.Locale.getDefault()).format(java.util.Date(diagnosis.timestamp))}
            
            ${getRecommendationText(diagnosis.prediction, diagnosis.confidence)}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Diagnosis Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNeutralButton("Find Doctors") { _, _ ->
                try {
                    findNavController().navigate(R.id.action_historyFragment_to_doctorsFragment)
                } catch (e: Exception) {
                    requireContext().toast("Doctors feature coming soon!")
                }
            }
            .show()
    }

    private fun getRecommendationText(prediction: String, confidence: Double): String {
        return when {
            prediction.lowercase().contains("normal") -> "✅ Good news! No concerning conditions detected."
            confidence >= 70 -> "⚠️ Recommendation: Consider consulting a dermatologist for proper diagnosis and treatment."
            else -> "📋 Recommendation: Retake photo with better lighting or consult a skin specialist."
        }
    }

    private fun showDeleteConfirmation(diagnosis: DiagnosisHistory, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Diagnosis")
            .setMessage("Are you sure you want to delete this diagnosis record?")
            .setPositiveButton("Delete") { _, _ ->
                historyManager.deleteHistory(diagnosis.id)
                historyAdapter.removeItem(position)

                if (historyAdapter.itemCount == 0) {
                    showEmptyState()
                }

                requireContext().toast("Diagnosis deleted")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearHistoryDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All History")
            .setMessage("Are you sure you want to delete all diagnosis history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                historyManager.clearAllHistory()
                historyAdapter.updateHistory(emptyList())
                showEmptyState()
                requireContext().toast("All history cleared")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
