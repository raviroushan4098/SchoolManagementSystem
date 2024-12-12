package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StudentMarksActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var marksTextView: TextView
    private var registrationNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_marks)

        db = DatabaseHelper(this)
        marksTextView = findViewById(R.id.marksTextView)

        // Retrieve the registration number from the intent
        registrationNumber = intent.getStringExtra("registration_number")

        if (registrationNumber != null) {
            loadAndDisplayMarks(registrationNumber!!)
        } else {
            Toast.makeText(this, "Error: Registration number not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAndDisplayMarks(registrationNo: String) {
        // Fetch the marks using the provided registration number
        val marksList = db.getMarksForStudent(registrationNo)

        if (marksList.isNotEmpty()) {
            // Display the marks
            val marksString = marksList.joinToString("\n") { "${it.first}: ${it.second} Marks" }
            marksTextView.text = marksString
        } else {
            marksTextView.text = "No marks found for registration number $registrationNo."
        }
    }
}
