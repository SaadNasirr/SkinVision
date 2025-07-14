package com.example.skinvision

object Constants {

    // API Configuration - Updated IP address
    const val BASE_URL = "http://10.4.72.33:5000"
    const val PREDICT_ENDPOINT = "/predict"
    const val API_TIMEOUT = 30L // seconds

    // Request Codes
    const val CAMERA_REQUEST_CODE = 1888
    const val GALLERY_REQUEST_CODE = 1889
    const val CAMERA_PERMISSION_REQUEST = 100
    const val STORAGE_PERMISSION_REQUEST = 101

    // Image Processing
    const val IMAGE_QUALITY = 80
    const val MAX_IMAGE_WIDTH = 1024
    const val MAX_IMAGE_HEIGHT = 1024

    // Confidence Thresholds
    const val HIGH_CONFIDENCE_THRESHOLD = 80
    const val MODERATE_CONFIDENCE_THRESHOLD = 60

    // Skin Conditions
    val SKIN_CONDITIONS = listOf(
        "Acne",
        "Eczema",
        "Psoriasis",
        "Dermatitis",
        "Rosacea",
        "Normal Skin"
    )

    // Shared Preferences Keys
    const val PREFS_NAME = "SkinVisionPrefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name"
    const val KEY_FIRST_LAUNCH = "first_launch"

    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val DIAGNOSES_COLLECTION = "diagnoses"
    const val HISTORY_COLLECTION = "history"
}
