package com.example.skinvision

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkTestFragment : Fragment() {

    private val SERVER_IP = "192.168.100.122"
    private val BASE_URL = "http://$SERVER_IP:5000"
    private val TAG = "NetworkTest"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val textView = TextView(requireContext()).apply {
            text = "Network Connection Test\n\n"
            textSize = 16f
        }

        val testServerButton = Button(requireContext()).apply {
            text = "1. Test Server Connection"
        }

        val testChatButton = Button(requireContext()).apply {
            text = "2. Test Chat API"
        }

        val testPredictButton = Button(requireContext()).apply {
            text = "3. Test Predict API"
        }

        layout.addView(textView)
        layout.addView(testServerButton)
        layout.addView(testChatButton)
        layout.addView(testPredictButton)

        testServerButton.setOnClickListener {
            testServerConnection(textView)
        }

        testChatButton.setOnClickListener {
            testChatAPI(textView)
        }

        testPredictButton.setOnClickListener {
            testPredictAPI(textView)
        }

        return layout
    }

    private fun testServerConnection(textView: TextView) {
        textView.text = "Testing server connection...\n\n"

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(BASE_URL)
                    .build()

                Log.d(TAG, "Testing: $BASE_URL")

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        textView.text = "SERVER CONNECTION SUCCESS!\n\nURL: $BASE_URL\nStatus: ${response.code}\n\nResponse: ${body?.take(200)}...\n\nServer is reachable!"
                    } else {
                        textView.text = "SERVER ERROR\n\nStatus: ${response.code}\nResponse: $body"
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Connection failed", e)
                activity?.runOnUiThread {
                    textView.text = "CONNECTION FAILED\n\nError: ${e.message}\nType: ${e.javaClass.simpleName}\n\nPossible issues:\n1. Wrong IP address\n2. Server not running\n3. Firewall blocking\n4. Different WiFi networks\n5. Router blocking device communication"
                }
            }
        }
    }

    private fun testChatAPI(textView: TextView) {
        textView.text = "Testing chat API...\n\n"

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val json = JSONObject()
                json.put("message", "test")

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url("$BASE_URL/chat")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                Log.d(TAG, "Testing chat: $BASE_URL/chat")

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        textView.text = "CHAT API SUCCESS!\n\nURL: $BASE_URL/chat\nStatus: ${response.code}\n\nResponse: $body\n\nChat API is working!"
                    } else {
                        textView.text = "CHAT API ERROR\n\nStatus: ${response.code}\nResponse: $body"
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Chat API failed", e)
                activity?.runOnUiThread {
                    textView.text = "CHAT API FAILED\n\nError: ${e.message}\nType: ${e.javaClass.simpleName}"
                }
            }
        }
    }

    private fun testPredictAPI(textView: TextView) {
        textView.text = "Testing predict API...\n\n"

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                // Dummy 1x1 pixel image
                val dummyImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="

                val json = JSONObject()
                json.put("image", dummyImage)

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url("$BASE_URL/predict")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                Log.d(TAG, "Testing predict: $BASE_URL/predict")

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        textView.text = "PREDICT API SUCCESS!\n\nURL: $BASE_URL/predict\nStatus: ${response.code}\n\nResponse: ${body?.take(300)}...\n\nPredict API is working!"
                    } else {
                        textView.text = "PREDICT API ERROR\n\nStatus: ${response.code}\nResponse: $body"
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Predict API failed", e)
                activity?.runOnUiThread {
                    textView.text = "PREDICT API FAILED\n\nError: ${e.message}\nType: ${e.javaClass.simpleName}"
                }
            }
        }
    }
}
