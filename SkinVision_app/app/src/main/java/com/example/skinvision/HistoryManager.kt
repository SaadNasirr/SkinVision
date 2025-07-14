package com.example.skinvision

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.skinvision.data.DiagnosisHistory
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class HistoryManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("DiagnosisHistory", Context.MODE_PRIVATE)

    fun saveHistory(bitmap: Bitmap?, prediction: String, confidence: Double) {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val imageBase64 = bitmap?.let { bitmapToBase64(it) }

        val diagnosis = DiagnosisHistory(
            id = id,
            image = bitmap,
            imageBase64 = imageBase64,
            prediction = prediction,
            confidence = confidence,
            timestamp = timestamp
        )

        val currentHistory = getAllHistory().toMutableList()
        currentHistory.add(0, diagnosis) // Add to beginning

        // Keep only last 50 records
        if (currentHistory.size > 50) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveHistoryList(currentHistory)
    }

    fun getAllHistory(): List<DiagnosisHistory> {
        val historyJson = sharedPreferences.getString("history_list", "[]")
        val historyArray = JSONArray(historyJson)
        val historyList = mutableListOf<DiagnosisHistory>()

        for (i in 0 until historyArray.length()) {
            try {
                val item = historyArray.getJSONObject(i)
                val imageBase64 = item.optString("imageBase64", null)
                val bitmap = imageBase64?.let { base64ToBitmap(it) }

                val diagnosis = DiagnosisHistory(
                    id = item.getString("id"),
                    image = bitmap,
                    imageBase64 = imageBase64,
                    prediction = item.getString("prediction"),
                    confidence = item.getDouble("confidence"),
                    timestamp = item.getLong("timestamp")
                )
                historyList.add(diagnosis)
            } catch (e: Exception) {
                // Skip corrupted entries
                continue
            }
        }

        return historyList.sortedByDescending { it.timestamp }
    }

    fun deleteHistory(id: String) {
        val currentHistory = getAllHistory().toMutableList()
        currentHistory.removeAll { it.id == id }
        saveHistoryList(currentHistory)
    }

    fun clearAllHistory() {
        sharedPreferences.edit().remove("history_list").apply()
    }

    private fun saveHistoryList(historyList: List<DiagnosisHistory>) {
        val historyArray = JSONArray()

        historyList.forEach { diagnosis ->
            val item = JSONObject().apply {
                put("id", diagnosis.id)
                put("prediction", diagnosis.prediction)
                put("confidence", diagnosis.confidence)
                put("timestamp", diagnosis.timestamp)
                diagnosis.imageBase64?.let { put("imageBase64", it) }
            }
            historyArray.put(item)
        }

        sharedPreferences.edit()
            .putString("history_list", historyArray.toString())
            .apply()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream) // Lower quality for storage
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
