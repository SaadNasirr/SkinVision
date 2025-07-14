package com.example.skinvision

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

data class ProfileData(
    val fullName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val gender: String,
    val profileImageBitmap: Bitmap? = null
)

class ProfileManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

    fun saveProfileData(fullName: String, email: String, phone: String, dateOfBirth: String, gender: String) {
        with(sharedPreferences.edit()) {
            putString("full_name", fullName)
            putString("email", email)
            putString("phone", phone)
            putString("date_of_birth", dateOfBirth)
            putString("gender", gender)
            apply()
        }
    }

    fun getProfileData(): ProfileData? {
        val fullName = sharedPreferences.getString("full_name", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val dateOfBirth = sharedPreferences.getString("date_of_birth", "") ?: ""
        val gender = sharedPreferences.getString("gender", "") ?: ""

        if (fullName.isEmpty() && email.isEmpty()) return null

        val profileImageBitmap = getProfileImage()

        return ProfileData(fullName, email, phone, dateOfBirth, gender, profileImageBitmap)
    }

    fun saveProfileImage(bitmap: Bitmap) {
        val base64Image = bitmapToBase64(bitmap)
        sharedPreferences.edit()
            .putString("profile_image", base64Image)
            .apply()
    }

    fun getProfileImage(): Bitmap? {
        val base64Image = sharedPreferences.getString("profile_image", null)
        return base64Image?.let { base64ToBitmap(it) }
    }

    fun removeProfileImage() {
        sharedPreferences.edit()
            .remove("profile_image")
            .apply()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
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
