package com.example.skinvision.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skinvision.data.ChatMessage
import com.example.skinvision.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = timeFormat.format(Date(message.timestamp))

        with(holder.binding) {
            if (message.isFromUser) {
                userMessageLayout.visibility = View.VISIBLE
                aiMessageLayout.visibility = View.GONE
                tvUserMessage.text = message.message
                tvUserTime.text = timeString
            } else {
                aiMessageLayout.visibility = View.VISIBLE
                userMessageLayout.visibility = View.GONE
                tvAiMessage.text = message.message
                tvAiTime.text = timeString
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
