package com.example.schoolmanagementsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrincipalLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if principal is already logged in
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isPrincipalLoggedIn = sharedPreferences.getBoolean("isPrincipalLoggedIn", false)

        if (isPrincipalLoggedIn) {
            // Redirect directly to PrincipalDashboardActivity if logged in
            val intent = Intent(this, PrincipalDashboardActivity::class.java)
            startActivity(intent)
            finish() // Close the login activity
            return
        }

        // If not logged in, set up the login screen
        setContentView(R.layout.activity_principal_login)
        setupLogin()
    }

    private fun setupLogin() {
        val etUsername: EditText = findViewById(R.id.etUsername)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (authenticatePrincipal(username, password)) {
                // Save login state to SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isPrincipalLoggedIn", true).apply()

                // Redirect to PrincipalDashboardActivity
                val intent = Intent(this, PrincipalDashboardActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticatePrincipal(username: String, password: String): Boolean {
        // Replace with actual authentication logic if necessary
        return username == "bodhiacademy" && password == "principal123"
    }
}
