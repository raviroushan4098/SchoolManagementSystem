package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddMarksActivity : AppCompatActivity() {

    private lateinit var etRegistrationNo: EditText
    private lateinit var etSubject: EditText
    private lateinit var etMarks: EditText
    private lateinit var btnAddMarks: Button
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_marks)

        db = DatabaseHelper(this)
        etRegistrationNo = findViewById(R.id.etRegistrationNumber)
        etSubject = findViewById(R.id.etSubject)
        etMarks = findViewById(R.id.etMarks)
        btnAddMarks = findViewById(R.id.btnAddMarks)

        btnAddMarks.setOnClickListener {
            val registrationNo = etRegistrationNo.text.toString()
            val subject = etSubject.text.toString()
            val marks = etMarks.text.toString().toIntOrNull()

            if (registrationNo.isNotEmpty() && subject.isNotEmpty() && marks != null) {
                db.addMarks(registrationNo, subject, marks)
                Toast.makeText(this, "Marks added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter valid data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
