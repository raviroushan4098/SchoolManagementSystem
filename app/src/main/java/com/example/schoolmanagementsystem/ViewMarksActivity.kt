package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class ViewMarksActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var tvMarks: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_marks)

        db = DatabaseHelper(this)
        tvMarks = findViewById(R.id.tvMarks)

        val studentRegistrationNumber = intent.getStringExtra("registration_number")
        val marksList = studentRegistrationNumber?.let { db.getMarksForStudent(it) }

        marksList?.let {
            tvMarks.text = it.joinToString("\n") { mark -> "${mark.first}: ${mark.second}" }
        }
    }
}
