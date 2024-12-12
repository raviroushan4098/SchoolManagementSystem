package com.example.schoolmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the splash screen layout
        setContentView(R.layout.activity_splash)

        // Use a handler to delay for a few seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Start main activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish SplashActivity so it can't be navigated back to
        }, 2000) // Adjust the delay as needed (e.g., 2000 ms = 2 seconds)
    }
}
