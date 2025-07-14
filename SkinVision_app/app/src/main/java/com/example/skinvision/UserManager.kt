package com.example.skinvision

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid ?: sharedPreferences.getString(Constants.KEY_USER_ID, null)
    }

    fun getUserEmail(): String? {
        return auth.currentUser?.email ?: sharedPreferences.getString(Constants.KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return auth.currentUser?.displayName ?: sharedPreferences.getString(Constants.KEY_USER_NAME, null)
    }

    fun saveUserData(userId: String, email: String, name: String? = null) {
        with(sharedPreferences.edit()) {
            putString(Constants.KEY_USER_ID, userId)
            putString(Constants.KEY_USER_EMAIL, email)
            name?.let { putString(Constants.KEY_USER_NAME, it) }
            apply()
        }
    }

    fun clearUserData() {
        with(sharedPreferences.edit()) {
            remove(Constants.KEY_USER_ID)
            remove(Constants.KEY_USER_EMAIL)
            remove(Constants.KEY_USER_NAME)
            apply()
        }
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        with(sharedPreferences.edit()) {
            putBoolean(Constants.KEY_FIRST_LAUNCH, false)
            apply()
        }
    }

    fun signOut() {
        auth.signOut()
        clearUserData()
    }
}
