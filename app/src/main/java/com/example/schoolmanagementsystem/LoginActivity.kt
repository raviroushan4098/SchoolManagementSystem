package com.example.schoolmanagementsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnStudentSignUp: Button
    private lateinit var btnTeacherSignUp: Button
    private lateinit var tvSchoolName: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        // Special credentials for the principal
        const val PRINCIPAL_USERNAME = "bodhi"
        const val PRINCIPAL_PASSWORD = "123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvSchoolName = findViewById(R.id.tvSchoolName)

        db = DatabaseHelper(this)

        // Initialize UI components
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnStudentSignUp = findViewById(R.id.btnStudentSignUp)
        btnTeacherSignUp = findViewById(R.id.btnTeacherSignUp)
        progressBar = findViewById(R.id.progressBar)


        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        val layout = findViewById<LinearLayout>(R.id.parentLayout)
        layout.startAnimation(fadeIn)
        tvSchoolName.startAnimation(fadeIn)

        etUsername.startAnimation(slideIn)
        etPassword.startAnimation(slideIn)
        btnLogin.startAnimation(slideIn)
        btnStudentSignUp.startAnimation(slideIn)
        btnTeacherSignUp.startAnimation(slideIn)

        // Check if credentials are saved in SharedPreferences
        checkSavedCredentials()

        // Set click listeners
        setClickListeners()
    }

    private fun setClickListeners() {
        // Set click listener for Student Sign Up button
        btnStudentSignUp.setOnClickListener {
            startActivity(Intent(this, StudentSignUpActivity::class.java))
        }

        // Set click listener for Teacher Sign Up button
        btnTeacherSignUp.setOnClickListener {
            startActivity(Intent(this, TeacherSignUpActivity::class.java))
        }

        // Set click listener for Login button
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            showLoading(true)

            // Check for special principal credentials
            if (username == PRINCIPAL_USERNAME && password == PRINCIPAL_PASSWORD) {
                navigateToPrincipalDashboard()
            } else {
                authenticateUser(username, password)
            }
        }
    }
    private fun showLoading(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            btnStudentSignUp.isEnabled = false
            btnTeacherSignUp.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            btnStudentSignUp.isEnabled = true
            btnTeacherSignUp.isEnabled = true
        }
    }

    private fun authenticateUser(username: String, password: String) {
        // Authenticate user with the provided credentials
        val (isAuthenticated, role) = db.authenticateUser(username, password)

        if (isAuthenticated) {
            // Save credentials to SharedPreferences
            saveCredentials(username, password, role)

            // Navigate to appropriate dashboard based on user role
            navigateToDashboard(username, role)
        } else {
            // Show error message if authentication fails
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            showLoading(false)
        }
    }

    private fun navigateToPrincipalDashboard() {
        // Save the principal login state in SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean("isPrincipalLoggedIn", true)  // Set the principal logged in state to true
            apply()
        }

        // Redirect to Principal Dashboard
        val intent = Intent(this, PrincipalDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun navigateToDashboard(username: String, role: String) {
        // Navigate to the respective dashboard based on the role
        when (role) {
            "teacher" -> {
                val intent = Intent(this, TeacherDashboardActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }
            "student" -> {
                val intent = Intent(this, StudentDashboardActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }
        }
        finish() // Close LoginActivity
    }

    private fun checkSavedCredentials() {
        // Access SharedPreferences to check for saved credentials
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val role = sharedPreferences.getString("role", null)
        val isPrincipalLoggedIn = sharedPreferences.getBoolean("isPrincipalLoggedIn", false)

        // If the principal is logged in, skip the login screen
        if (isPrincipalLoggedIn) {
            navigateToPrincipalDashboard()
        } else {
            // If credentials are saved, authenticate and go to dashboard
            if (savedUsername != null && savedPassword != null && role != null) {
                navigateToDashboard(savedUsername, role)
            }
        }
    }


    private fun saveCredentials(username: String, password: String, role: String) {
        // Save credentials in SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("role", role)
        editor.apply()
    }


}
