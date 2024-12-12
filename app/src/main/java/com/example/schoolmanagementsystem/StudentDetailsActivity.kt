package com.example.schoolmanagementsystem


import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.schoolmanagementsystem.models.Attendance
import com.example.schoolmanagementsystem.models.Student

class StudentDetailsActivity : AppCompatActivity() {

    private lateinit var txtStudentDetails: TextView
    private lateinit var dbHelper: DatabaseHelper // Assuming you have a DatabaseHelper class for handling DB operations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        txtStudentDetails = findViewById(R.id.txtStudentDetails)
        dbHelper = DatabaseHelper(this) // Initialize your database helper

        // Get the student registration number from the intent
        val regNo = intent.getStringExtra("REG_NO") ?: ""

        if (regNo.isNotEmpty()) {
            // Fetch and display student details from the database
            val studentDetails = getStudentDetails(regNo)
            val attendanceDetails = getAttendanceDetails(regNo)

            val detailsText = buildString {
                append("Name: ${studentDetails.name}\n")
                append("Registration Number: ${studentDetails.registrationNumber}\n")
                append("Phone Number: ${studentDetails.phoneNumber}\n")
                append("Class: ${studentDetails.className}\n")
                append("Section: ${studentDetails.section}\n")
                append("Roll Number: ${studentDetails.rollNumber}\n")
                append("Attendance:\n")

                if (attendanceDetails.isNotEmpty()) {
                    attendanceDetails.forEach {
                        append("Date: ${it.date} - Status: ${if (it.isPresent == 1) "Present" else "Absent"}\n")
                    }
                } else {
                    append("No attendance records available.")
                }
            }

            txtStudentDetails.text = detailsText
        } else {
            txtStudentDetails.text = "No student details available"
        }
    }

    private fun getStudentDetails(regNo: String): Student {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_STUDENTS} WHERE registrationNumber = ?",
            arrayOf(regNo)
        )

        if (cursor.moveToFirst()) {
            // Retrieve all necessary fields
            val id = cursor.getInt(cursor.getColumnIndex("id"))  // Get the ID field
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val registrationNumber = cursor.getString(cursor.getColumnIndex("registrationNumber"))
            val phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"))
            val className = cursor.getString(cursor.getColumnIndex("className"))
            val section = cursor.getString(cursor.getColumnIndex("section"))
            val rollNumber = cursor.getString(cursor.getColumnIndex("rollNumber"))
            val profileImage = cursor.getBlob(cursor.getColumnIndex("profileImage"))  // Get profile image as ByteArray

            cursor.close()

            // Return the Student object with the correct fields
            return Student(id, name, registrationNumber, phoneNumber, rollNumber, section, className, profileImage)
        }

        cursor.close()
        // If no student found, return an empty Student object (or handle this case appropriately)
        return Student(0, "", "", "", "", "", "", null)  // You can return a placeholder or throw an exception
    }

    private fun getAttendanceDetails(regNo: String): List<Attendance> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ATTENDANCE} WHERE registration_no = ?", arrayOf(regNo))

        val attendanceList = mutableListOf<Attendance>()

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val isPresent = cursor.getInt(cursor.getColumnIndex("is_present"))
            attendanceList.add(Attendance(regNo, date, isPresent))
        }
        cursor.close()

        return attendanceList
    }

}
