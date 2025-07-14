package com.example.skinvision

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task

fun EditText.textString(): String = this.text?.toString()?.trim() ?: ""
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Task<AuthResult>.uidSafe(): String = this.result?.user?.uid ?: ""