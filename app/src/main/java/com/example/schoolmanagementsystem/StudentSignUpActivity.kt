package com.example.schoolmanagementsystem

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException


class StudentSignUpActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etName: EditText
    private lateinit var etRegistrationNumber: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var etClass: EditText
    private lateinit var etSection: EditText
    private lateinit var etRollNumber: EditText
    private lateinit var etSectionId: EditText // New Section ID field
    private lateinit var btnSignUp: Button
    private lateinit var btnSelectImage: Button
    private lateinit var ivProfileImage: ImageView

    private var selectedImageUri: Uri? = null

    private val IMAGE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_sign_up)

        db = DatabaseHelper(this)
        etName = findViewById(R.id.etName)
        etRegistrationNumber = findViewById(R.id.etRegistrationNumber)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        etClass = findViewById(R.id.etClass)
        etSection = findViewById(R.id.etSection)
        etRollNumber = findViewById(R.id.etRollNumber)
        etSectionId = findViewById(R.id.etSectionId) // Initialize Section ID field
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        ivProfileImage = findViewById(R.id.ivProfileImage)

        btnSelectImage.setOnClickListener {
            // Open gallery to select an image
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString()
            val registrationNumber = etRegistrationNumber.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()
            val password = etPassword.text.toString()
            val className = etClass.text.toString()
            val section = etSection.text.toString()
            val rollNumber = etRollNumber.text.toString()
            val sectionId = etSectionId.text.toString() // Get Section ID input

            // Validate Section ID
            if (sectionId.isEmpty()) {
                Toast.makeText(this, "Please provide a Section ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the image is selected, if not use default placeholder or handle as needed
            val imageBytes = selectedImageUri?.let { uri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    byteArrayOutputStream.toByteArray()
                } catch (e: IOException) {
                    null
                }
            }

            // Store the new user with profile image and other details in the database
            val isSuccess = db.addStudentWithImage(
                name, registrationNumber, phoneNumber, password,
                className, section, rollNumber, sectionId.toInt(), imageBytes
            )

            if (isSuccess) {
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Sign Up Failed. Registration number might already exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            selectedImageUri = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                ivProfileImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
