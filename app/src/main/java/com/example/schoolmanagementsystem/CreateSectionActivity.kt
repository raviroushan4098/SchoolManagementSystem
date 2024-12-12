package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateSectionActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_section)

        db = DatabaseHelper(this)

        val btnSaveSection: Button = findViewById(R.id.btnSaveSection)
        val etSectionName: EditText = findViewById(R.id.etSectionName)

        btnSaveSection.setOnClickListener {
            val sectionName = etSectionName.text.toString()
            if (sectionName.isNotBlank()) {
                db.addSection(sectionName)
                Toast.makeText(this, "Section created successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Section name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
