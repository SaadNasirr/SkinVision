package com.example.skinvision.data

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val location: String,
    val distance: String,
    val experience: String,
    val isAvailable: Boolean,
    val phone: String,
    val imageUrl: String? = null,
    val consultationFee: String,
    val languages: List<String>,
    val education: String,
    val about: String
)
