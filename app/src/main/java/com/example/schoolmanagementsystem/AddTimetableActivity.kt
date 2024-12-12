package com.example.schoolmanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolmanagementsystem.models.Section

class AddTimetableActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var sectionSpinner: Spinner
    private lateinit var classSpinner: Spinner
    private lateinit var addSubjectButton: Button
    private lateinit var saveTimetableButton: Button
    private lateinit var subjectsContainer: LinearLayout

    private var subjectCount = 0
    private val maxSubjects = 6

    private var classes: List<String> = emptyList() // Holds the list of class names
    private var sections: List<Section> = emptyList() // Holds the list of sections for the selected class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timetable)

        // Initialize views
        db = DatabaseHelper(this)
        sectionSpinner = findViewById(R.id.spinnerSection)
        classSpinner = findViewById(R.id.spinnerClass)
        addSubjectButton = findViewById(R.id.btnAddSubject)
        saveTimetableButton = findViewById(R.id.btnSaveTimetable)
        subjectsContainer = findViewById(R.id.subjectsContainer)

        // Set up listeners
        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedClass = classes[position]
                loadSectionsForClass(selectedClass)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        addSubjectButton.setOnClickListener {
            if (subjectCount < maxSubjects) {
                addSubjectFields()
                subjectCount++
            } else {
                Toast.makeText(this, "You can only add up to $maxSubjects subjects.", Toast.LENGTH_SHORT).show()
            }
        }

        saveTimetableButton.setOnClickListener {
            saveTimetable()
        }

        loadClasses()
    }

    private fun loadClasses() {
        classes = db.getAllStudentClasses() // Fetch all class names as a List<String>
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = classAdapter
    }

    private fun loadSectionsForClass(className: String) {
        sections = db.getSectionsByClassName(className)
        if (sections.isEmpty()) {
            Toast.makeText(this, "No sections found for the selected class.", Toast.LENGTH_SHORT).show()
            sectionSpinner.adapter = null
            return
        }
        val sectionNames = sections.map { it.name }
        val sectionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sectionNames)
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sectionSpinner.adapter = sectionAdapter
    }

    private fun addSubjectFields() {
        val subjectLayout = layoutInflater.inflate(R.layout.item_subject_fields, subjectsContainer, false)
        subjectsContainer.addView(subjectLayout)
    }

    private fun saveTimetable() {
        if (classes.isEmpty() || sections.isEmpty()) {
            Toast.makeText(this, "Please select a class and section before saving.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedClassPosition = classSpinner.selectedItemPosition
        val selectedSectionPosition = sectionSpinner.selectedItemPosition

        if (selectedClassPosition == AdapterView.INVALID_POSITION || selectedSectionPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select both a class and a section.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedClass = classes[selectedClassPosition]
        val selectedSection = sections[selectedSectionPosition]

        val dayAndSubjectEntries = mutableListOf<Triple<String, String, String>>()

        for (i in 0 until subjectsContainer.childCount) {
            val subjectView = subjectsContainer.getChildAt(i)
            val etDay = subjectView.findViewById<EditText>(R.id.etDay)
            val etTime = subjectView.findViewById<EditText>(R.id.etTime)
            val etSubject = subjectView.findViewById<EditText>(R.id.etSubject)

            val day = etDay.text.toString().trim()
            val time = etTime.text.toString().trim()
            val subject = etSubject.text.toString().trim()

            if (day.isBlank() || time.isBlank() || subject.isBlank()) {
                Toast.makeText(this, "All fields (day, time, subject) must be filled.", Toast.LENGTH_SHORT).show()
                return
            }

            dayAndSubjectEntries.add(Triple(day, time, subject))
        }

        for (entry in dayAndSubjectEntries) {
            db.addTimetable(selectedSection.id, entry.first, entry.second, entry.third)
        }

        Toast.makeText(this, "Timetable added successfully!", Toast.LENGTH_SHORT).show()
        resetForm()
    }

    private fun resetForm() {
        subjectsContainer.removeAllViews()
        subjectCount = 0
        classSpinner.setSelection(0)
        sectionSpinner.adapter = null
    }
}
