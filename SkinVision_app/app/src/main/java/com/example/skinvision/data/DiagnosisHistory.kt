package com.example.skinvision.data

import android.graphics.Bitmap

data class DiagnosisHistory(
    val id: String,
    val image: Bitmap?,
    val imageBase64: String?,
    val prediction: String,
    val confidence: Double,
    val timestamp: Long,
    val recommendations: String? = null
) {
    fun getConfidenceLevel(): String {
        return when {
            confidence >= 80 -> "HIGH"
            confidence >= 60 -> "MEDIUM"
            else -> "LOW"
        }
    }

    fun getConfidenceBadgeColor(): Int {
        return when {
            confidence >= 80 -> android.R.color.holo_green_dark
            confidence >= 60 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_red_dark
        }
    }
}
