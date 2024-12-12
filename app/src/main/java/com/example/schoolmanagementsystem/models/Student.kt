package com.example.schoolmanagementsystem.models



data class Student(
    val id: Int,
    val name: String,
    val registrationNumber: String,
    val phoneNumber: String,
    val rollNumber: String,
    val section: String,
    val className: String, // Included className
    val profileImage: ByteArray?  // profileImage as ByteArray for image data
)

