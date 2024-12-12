package com.example.schoolmanagementsystem

import StudentAttendanceAdapter
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.view.View


class AddAttendanceActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var classSpinner: Spinner
    private lateinit var sectionSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var studentListView: ListView
    private lateinit var saveAttendanceButton: Button
    private lateinit var studentAdapter: StudentAttendanceAdapter
    private var selectedDate: String = ""
    private lateinit var teacherRegistrationNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attendance)

        db = DatabaseHelper(this)
        teacherRegistrationNumber = intent.getStringExtra("username") ?: ""


        classSpinner = findViewById(R.id.spinnerClass)
        sectionSpinner = findViewById(R.id.spinnerSection)
        dateEditText = findViewById(R.id.etDate)
        studentListView = findViewById(R.id.studentListView)
        saveAttendanceButton = findViewById(R.id.btnSaveAttendance)

        loadClasses()

        // Update sections when a class is selected
        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                loadSectionsForClass(classSpinner.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Load students when a section is selected
        sectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                loadStudentsForSection(sectionSpinner.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Show DatePicker when the date EditText is clicked
        dateEditText.setOnClickListener {
            showDatePicker()
        }

        // Save attendance when the "Save Attendance" button is clicked
        saveAttendanceButton.setOnClickListener {
            saveAttendance()
        }
    }

    private fun loadClasses() {
        val classNames = db.getAllStudentClasses() // Fetch all unique class names
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classNames)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = classAdapter
    }


    private fun loadSectionsForClass(className: String) {
        val sections = db.getSectionsByClassName(className)
        val sectionNames = sections.map { it.name }
        val sectionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sectionNames)
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sectionSpinner.adapter = sectionAdapter
    }

    private fun loadStudentsForSection(sectionName: String) {
        val className = classSpinner.selectedItem.toString()  // Get selected class
        val students = db.getStudentsByClassAndSection(className, sectionName)  // Fetch students by class and section

        studentAdapter = StudentAttendanceAdapter(this, students)  // Pass the list of students to the adapter
        studentListView.adapter = studentAdapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dateEditText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun saveAttendance() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }

        val attendanceMap = studentAdapter.getAttendanceStatus()
        var allSuccess = true

        for ((registrationNumber, isPresent) in attendanceMap) {
            val success = db.addAttendance(registrationNumber, selectedDate, isPresent)
            if (!success) allSuccess = false
        }

        if (allSuccess) {
            Toast.makeText(this, "Attendance saved successfully!", Toast.LENGTH_SHORT).show()
            dateEditText.text.clear()
            val intent = Intent(this, TeacherDashboardActivity::class.java)
            intent.putExtra("username",teacherRegistrationNumber )
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error saving attendance. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
