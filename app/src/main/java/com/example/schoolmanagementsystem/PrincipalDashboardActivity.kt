package com.example.schoolmanagementsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrincipalDashboardActivity : AppCompatActivity() {

    private lateinit var studentRegNoEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_dashboard)

        // Set the welcome message
        val welcomeMessageTextView: TextView = findViewById(R.id.tvWelcomeMessage)
        welcomeMessageTextView.text = "Hello Sir \n this is your Admin Portal"

        // Add fade-in animation to the welcome message
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500 // 1.5 seconds
        welcomeMessageTextView.startAnimation(fadeIn)

        // Initialize buttons and EditTexts
        val btnAddTimetable: Button = findViewById(R.id.btnAddTimetable)
        val btnViewStudentDetails: Button = findViewById(R.id.btnViewStudentDetails)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        studentRegNoEditText = findViewById(R.id.editTextStudentRegNo)

        // Apply scale animation to buttons
        applyButtonAnimation(btnAddTimetable)
        applyButtonAnimation(btnViewStudentDetails)
        applyButtonAnimation(btnLogout)

        // Set up listeners
        btnAddTimetable.setOnClickListener {
            startActivity(Intent(this, AddTimetableActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Custom transition
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

        setupLogoutButton()
    }

    private fun setupLogoutButton() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isPrincipalLoggedIn", false).apply()

            // Redirect to login screen with slide-in transition
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close current activity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // Custom transition
        }
    }

    private fun applyButtonAnimation(button: Button) {
        val scaleUp = android.view.animation.ScaleAnimation(
            1f, 1.1f, // Scale from 100% to 110%
            1f, 1.1f, // Scale from 100% to 110%
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleUp.duration = 200
        scaleUp.fillAfter = true

        val scaleDown = android.view.animation.ScaleAnimation(
            1.1f, 1f, // Scale from 110% back to 100%
            1.1f, 1f, // Scale from 110% back to 100%
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleDown.duration = 200
        scaleDown.fillAfter = true

        button.setOnClickListener {
            button.startAnimation(scaleUp)
            button.postDelayed({ button.startAnimation(scaleDown) }, 200)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(false)
    }
}
