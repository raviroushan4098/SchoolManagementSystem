package com.example.schoolmanagementsystem

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var btnAddMarks: Button
    private lateinit var btnAddAttendance: Button
    private lateinit var ivTeacherProfileImage: ImageView
    private lateinit var teacherRegistrationNumber: String
    private lateinit var studentRegNoEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        // Apply the fade in animation to the entire layout
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        findViewById<LinearLayout>(R.id.parentLayout).startAnimation(fadeIn)
        val btnViewStudentDetails: Button = findViewById(R.id.btnViewStudentDetails)
        studentRegNoEditText = findViewById(R.id.editTextStudentRegNo)

        // Initialize buttons and ImageView
        btnAddMarks = findViewById(R.id.btnAddMarks)
        btnAddAttendance = findViewById(R.id.btnAddAttendance)
        ivTeacherProfileImage = findViewById(R.id.teacherProfileImage)

        // Retrieve the teacher's registration number from the Intent
        teacherRegistrationNumber = intent.getStringExtra("username") ?: ""

        // Display teacher name
        displayTeacherName()

        // Set up buttons to perform specific actions with slide-in animation
        btnAddMarks.setOnClickListener {
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)
            it.startAnimation(slideIn)
            startActivity(Intent(this, AddMarksActivity::class.java))
        }

        btnAddAttendance.setOnClickListener {
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)
            it.startAnimation(slideIn)
            val intent = Intent(this, AddAttendanceActivity::class.java)
            intent.putExtra("username", teacherRegistrationNumber)
            startActivity(intent)
        }
        btnViewStudentDetails.setOnClickListener {
            val regNo = studentRegNoEditText.text.toString().trim()
            if (regNo.isNotEmpty()) {
                val intent = Intent(this, StudentDetailsActivity::class.java).apply {
                    putExtra("REG_NO", regNo)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Custom transition
            } else {
                Toast.makeText(this, "Please enter a valid student registration number", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup the logout button
        setupLogoutButton()
    }

    private fun displayTeacherName() {
        try {
            val profile = DatabaseHelper(this).getTeacherProfile(teacherRegistrationNumber)
            val nameTextView: TextView = findViewById(R.id.teacherName)

            if (profile != null) {
                val teacherName = profile.first
                val registrationNumber = profile.second
                nameTextView.text = "Welcome, $teacherName\nReg No: $registrationNumber"

                // Load teacher profile image if available
                val profileImage = DatabaseHelper(this).getTeacherProfileImage(teacherRegistrationNumber)
                if (profileImage != null) {
                    val bitmap = BitmapFactory.decodeByteArray(profileImage, 0, profileImage.size)
                    ivTeacherProfileImage.setImageBitmap(bitmap)
                } else {
                    // Set a default image if no profile image is found
                    ivTeacherProfileImage.setImageResource(R.drawable.ic_default_profile_image)
                }
            } else {
                nameTextView.text = "Welcome, Teacher"
                ivTeacherProfileImage.setImageResource(R.drawable.ic_default_profile_image)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load teacher profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLogoutButton() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Clear saved credentials from SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Apply the fade out animation to the layout before logout
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            findViewById<LinearLayout>(R.id.parentLayout).startAnimation(fadeOut)

            // Wait for animation to finish before going to LoginActivity
            fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}

                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    val intent = Intent(this@TeacherDashboardActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }
    }
}
