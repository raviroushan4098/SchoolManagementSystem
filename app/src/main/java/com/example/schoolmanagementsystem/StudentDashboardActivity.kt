package com.example.schoolmanagementsystem

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentRegistrationNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Apply fade-in animation when activity starts
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        findViewById<LinearLayout>(R.id.parentLayout).startAnimation(fadeIn)

        // Initialize DatabaseHelper instance
        dbHelper = DatabaseHelper(this)

        // Retrieve the student's registration number from the Intent
        studentRegistrationNumber = intent.getStringExtra("username") ?: ""

        // Display student name and profile image
        displayStudentProfile()

        // Set up buttons to load specific data on click
        setupButtonListeners()

        // Set up the Logout button listener
        setupLogoutButton()
    }

    private fun displayStudentProfile() {
        try {
            val profile = dbHelper.getStudentProfile(studentRegistrationNumber)
            val nameTextView: TextView = findViewById(R.id.studentName)
            val classTextView: TextView = findViewById(R.id.studentClass)  // Class TextView
            val sectionTextView: TextView = findViewById(R.id.studentSection) // Section TextView
            val profileImageView: ImageView = findViewById(R.id.profileImageView)

            if (profile != null) {
                val studentName = profile.first
                val studentClass = profile.second.first // Class Name
                val studentSection = profile.second.second // Section Name
                val imageBytes = profile.third

                // Set the name, class, and section
                nameTextView.text = "Welcome, $studentName"
                classTextView.text = "Class: $studentClass"
                sectionTextView.text = "Section: $studentSection"  // Display the section

                // Set profile image if available
                if (imageBytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    profileImageView.setImageBitmap(bitmap)
                } else {
                    profileImageView.setImageResource(R.drawable.ic_default_profile_image) // Default image
                }
            } else {
                nameTextView.text = "Welcome, Student"
                classTextView.text = "Class: N/A"  // Default or empty class name
                sectionTextView.text = "Section: N/A"  // Default or empty section name
                profileImageView.setImageResource(R.drawable.ic_default_profile_image) // Default image
            }
        } catch (e: Exception) {
            Log.e("StudentDashboard", "Error fetching student profile", e)
            Toast.makeText(this, "Failed to load student profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonListeners() {
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        findViewById<Button>(R.id.btnViewMarks).setOnClickListener {
            it.startAnimation(slideIn)  // Apply slide-in animation on button click
            val intent = Intent(this, StudentMarksActivity::class.java)
            intent.putExtra("registration_number", studentRegistrationNumber)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnViewAttendance).setOnClickListener {
            it.startAnimation(slideIn)
            val intent = Intent(this, ViewAttendanceActivity::class.java)
            intent.putExtra("registration_number", studentRegistrationNumber)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnViewTimetable).setOnClickListener {
            it.startAnimation(slideIn)
            val profile = dbHelper.getStudentProfile(studentRegistrationNumber)
            if (profile != null) {
                val studentClass = profile.second.first
                val studentSection = profile.second.second

                val intent = Intent(this, ViewTimetableActivity::class.java)
                intent.putExtra("student_class", studentClass)
                intent.putExtra("student_section", studentSection)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to load student details for timetable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLogoutButton() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Apply fade-out animation when logging out
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            findViewById<LinearLayout>(R.id.parentLayout).startAnimation(fadeOut)

            // Clear saved credentials from SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Redirect to LoginActivity and clear the back stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        // Apply back-slide-out animation when navigating back
        val backSlideOut = AnimationUtils.loadAnimation(this, R.anim.back_slide_out)
        findViewById<LinearLayout>(R.id.parentLayout).startAnimation(backSlideOut)
        super.onBackPressed()
    }
}
