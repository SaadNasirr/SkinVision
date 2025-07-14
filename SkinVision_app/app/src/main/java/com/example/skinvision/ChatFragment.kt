package com.example.skinvision

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skinvision.adapters.ChatAdapter
import com.example.skinvision.data.ChatMessage
import com.example.skinvision.databinding.FragmentChatBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private val SERVER_IP = "10.4.73.51"
    private val CHAT_API_URL = "http://$SERVER_IP:5000/chat"
    private val TAG = "ChatFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "=== CHAT FRAGMENT LOADED ===")
        requireContext().toast("Chat loaded!")

        setupRecyclerView()
        setupClickListeners()

        // Add welcome message using your ChatMessage format
        addBotMessage("Hello! I'm your AI health assistant. Ask me anything about skin conditions!")
    }

    private fun setupRecyclerView() {
        try {
            chatAdapter = ChatAdapter(messages)
            binding.recyclerViewChat.apply {
                adapter = chatAdapter
                layoutManager = LinearLayoutManager(context)
            }
            Log.d(TAG, "✅ RecyclerView setup successful")
        } catch (e: Exception) {
            Log.e(TAG, "❌ RecyclerView setup failed", e)
            requireContext().toast("RecyclerView error: ${e.message}")
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            Log.d(TAG, "Send button clicked")
            sendMessage()
        }

        binding.editTextMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun sendMessage() {
        val messageText = binding.editTextMessage.text.toString().trim()

        if (messageText.isEmpty()) {
            addBotMessage("Please type a message first!")
            return
        }

        Log.d(TAG, "📤 Sending message: '$messageText'")

        // Add user message using your ChatMessage format
        addUserMessage(messageText)
        binding.editTextMessage.text.clear()

        showTypingIndicator()
        sendToChatbot(messageText)
    }

    private fun addUserMessage(message: String) {
        try {
            val chatMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                message = message,
                isFromUser = true,  // Using your property name
                timestamp = System.currentTimeMillis()
            )
            chatAdapter.addMessage(chatMessage)
            scrollToBottom()
            Log.d(TAG, "✅ User message added")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to add user message", e)
        }
    }

    private fun addBotMessage(message: String) {
        try {
            val chatMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                message = message,
                isFromUser = false,  // Using your property name
                timestamp = System.currentTimeMillis()
            )
            chatAdapter.addMessage(chatMessage)
            scrollToBottom()
            Log.d(TAG, "✅ Bot message added")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to add bot message", e)
        }
    }

    private fun showTypingIndicator() {
        binding.typingIndicator.visibility = View.VISIBLE
    }

    private fun hideTypingIndicator() {
        binding.typingIndicator.visibility = View.GONE
    }

    private fun scrollToBottom() {
        if (messages.isNotEmpty()) {
            binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        }
    }

    private fun sendToChatbot(message: String) {
        Log.d(TAG, "=== SENDING TO CHATBOT ===")
        Log.d(TAG, "URL: $CHAT_API_URL")
        Log.d(TAG, "Message: '$message'")

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                val json = JSONObject()
                json.put("message", message)

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url(CHAT_API_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                Log.d(TAG, "🔄 Making HTTP request...")

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "❌ HTTP REQUEST FAILED", e)
                        activity?.runOnUiThread {
                            hideTypingIndicator()
                            addBotMessage("❌ Connection failed: ${e.message}")
                            requireContext().toast("Connection failed!")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        Log.d(TAG, "📥 Response code: ${response.code}")
                        Log.d(TAG, "📥 Response body: $responseBody")

                        activity?.runOnUiThread {
                            hideTypingIndicator()

                            if (response.isSuccessful && responseBody != null) {
                                try {
                                    val result = JSONObject(responseBody)
                                    if (result.has("answer")) {
                                        val answer = result.getString("answer")
                                        addBotMessage("🤖 $answer")
                                        requireContext().toast("✅ Response received!")
                                        Log.d(TAG, "✅ SUCCESS: Bot responded")
                                    } else {
                                        addBotMessage("❌ No answer in response")
                                        Log.e(TAG, "❌ No 'answer' field in response")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ JSON parsing error", e)
                                    addBotMessage("❌ Response parsing error: ${e.message}")
                                }
                            } else {
                                Log.e(TAG, "❌ HTTP error: ${response.code}")
                                addBotMessage("❌ Server error: HTTP ${response.code}")
                            }
                        }
                    }
                })

            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception in sendToChatbot", e)
                activity?.runOnUiThread {
                    hideTypingIndicator()
                    addBotMessage("❌ Exception: ${e.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
