package com.example.schoolmanagementsystem.models

data class Teacher(
    val id: Int,
    val name: String,
    val uid: String,
    val subject: String,
    val phoneNumber: String,
    val password: String,
    val profileImage: ByteArray?  // Changed to ByteArray? to store image data
)
