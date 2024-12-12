package com.example.schoolmanagementsystem

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class TeacherSignUpActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etName: EditText
    private lateinit var etUID: EditText
    private lateinit var etSubject: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnSelectImage: Button
    private lateinit var ivProfileImage: ImageView

    private var profileImageByteArray: ByteArray? = null

    companion object {
        const val IMAGE_PICK_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_sign_up)

        db = DatabaseHelper(this)
        etName = findViewById(R.id.etName)
        etUID = findViewById(R.id.etUID)
        etSubject = findViewById(R.id.etSubject)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        ivProfileImage = findViewById(R.id.ivProfileImage)

        // Set click listener to open image picker
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        // Set click listener for Sign Up button
        btnSignUp.setOnClickListener {
            val name = etName.text.toString()
            val uid = etUID.text.toString()
            val subject = etSubject.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()
            val password = etPassword.text.toString()

            // Validate that all fields are filled and profile image is selected
            if (name.isBlank() || uid.isBlank() || subject.isBlank() || phoneNumber.isBlank() || password.isBlank() || profileImageByteArray == null) {
                Toast.makeText(this, "All fields and profile image are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Store the new user in the database
            val isSuccess = db.addTeacher(name, uid, subject, phoneNumber, password, profileImageByteArray)
            if (isSuccess) {
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Sign Up Failed. UID might already exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the result of the image picker intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Set the selected image to the ImageView
            ivProfileImage.setImageBitmap(bitmap)

            // Convert the image to a byte array for storage in the database
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            profileImageByteArray = stream.toByteArray()
        }
    }
}
