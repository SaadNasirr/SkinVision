package com.example.skinvision.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skinvision.data.QuickAction
import com.example.skinvision.databinding.ItemQuickActionBinding

class QuickActionsAdapter(
    private val quickActions: List<QuickAction>,
    private val onActionClick: (QuickAction) -> Unit
) : RecyclerView.Adapter<QuickActionsAdapter.QuickActionViewHolder>() {

    class QuickActionViewHolder(val binding: ItemQuickActionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickActionViewHolder {
        val binding = ItemQuickActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuickActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickActionViewHolder, position: Int) {
        val action = quickActions[position]

        with(holder.binding) {
            tvActionIcon.text = action.icon
            tvActionText.text = action.text

            root.setOnClickListener {
                onActionClick(action)
            }
        }
    }

    override fun getItemCount(): Int = quickActions.size
}
