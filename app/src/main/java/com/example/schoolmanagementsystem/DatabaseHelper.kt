
package com.example.schoolmanagementsystem

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.schoolmanagementsystem.models.Section
import com.example.schoolmanagementsystem.models.Class
import com.example.schoolmanagementsystem.models.Teacher

import com.example.schoolmanagementsystem.models.Student

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "school_management.db"
        private const val DATABASE_VERSION = 31
       const val TABLE_STUDENTS = "students"
        private const val TABLE_TEACHERS = "teachers"
        private const val TABLE_MARKS = "marks_table"
        const val TABLE_ATTENDANCE = "attendance_table"
        private const val TABLE_SECTIONS = "sections"
        private const val TABLE_CLASSES = "classes"
        private const val TABLE_TIMETABLE = "timetable_table"


        // Columns for students table
        private const val COLUMN_STUDENT_ID = "id"
        private const val COLUMN_STUDENT_NAME = "name"
        private const val COLUMN_SECTION_ID = "section_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE $TABLE_STUDENTS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    registrationNumber TEXT,
    phoneNumber TEXT,  
    password TEXT,
    className TEXT,
    section TEXT,
    rollNumber TEXT,
    profileImage BLOB,
    section_id INTEGER  -- Each student is tied to a section ID
);

    """)


        db.execSQL("""
    CREATE TABLE $TABLE_TEACHERS (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        uid TEXT UNIQUE,
        subject TEXT,
        phone_number TEXT,
        password TEXT,
        profile_image BLOB
    );
""")


        db.execSQL("""
            CREATE TABLE $TABLE_MARKS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                registration_no TEXT,
                subject TEXT,
                marks INTEGER,
                FOREIGN KEY (registration_no) REFERENCES $TABLE_STUDENTS(registrationNumber)
            );
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_ATTENDANCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                registration_no TEXT,
                date TEXT,
                is_present INTEGER,
                FOREIGN KEY (registration_no) REFERENCES $TABLE_STUDENTS(registrationNumber)
            );
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_SECTIONS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                class_id INTEGER
                
            );
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_TIMETABLE (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    section_id INTEGER,  -- Foreign key to students table section_id
    day TEXT,
    time TEXT,
    subject TEXT,
    FOREIGN KEY (section_id) REFERENCES $TABLE_STUDENTS(section_id)
);

        """)

        db.execSQL("""
            CREATE TABLE $TABLE_CLASSES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
        """)
//        db.execSQL("""
//            CREATE TABLE timetable_assignments (
//    id INTEGER PRIMARY KEY AUTOINCREMENT,
//    section_id INTEGER,
//    teacher_id INTEGER,
//    FOREIGN KEY (section_id) REFERENCES sections(id),
//    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
//);
//
//        """)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MARKS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TIMETABLE")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_SECTIONS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
            //db.execSQL("ALTER TABLE $TABLE_STUDENTS ADD COLUMN section_id INTEGER")
            onCreate(db)
        }
    }


    // Method to add student with image
    fun addStudentWithImage(
        name: String,
        registrationNumber: String,
        phoneNumber: String,
        password: String,
        className: String,
        section: String,
        rollNumber: String,
        sectionId: Int,
        profileImage: ByteArray?
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", name)
            put("registrationNumber", registrationNumber)
            put("phoneNumber", phoneNumber)
            put("password", password)
            put("className", className)
            put("section", section)
            put("rollNumber", rollNumber)
            put("section_id", sectionId)
            put("profileImage", profileImage)
        }
        val result = db.insert("students", null, contentValues)
        db.close()
        return result != -1L
    }


    // Method to add teacher
    fun addTeacher(
        name: String,
        uid: String,
        subject: String,
        phoneNumber: String,
        password: String,
        profileImage: ByteArray?
    ): Boolean {
        val db = this.writableDatabase

        // Check for duplicate UID
        val existingTeacherCursor = db.query(
            TABLE_TEACHERS, arrayOf("uid"),
            "uid = ?", arrayOf(uid), null, null, null
        )
        if (existingTeacherCursor.moveToFirst()) {
            existingTeacherCursor.close()
            db.close()
            return false // UID already exists
        }

        // Insert the teacher record
        val values = ContentValues().apply {
            put("name", name)
            put("uid", uid)
            put("subject", subject)
            put("phone_number", phoneNumber)
            put("password", password)
            put("profile_image", profileImage)
        }
        val result = db.insert(TABLE_TEACHERS, null, values)
        existingTeacherCursor.close()
        db.close()
        return result != -1L
    }


    // Method to authenticate user based on username and password
    fun authenticateUser(username: String, password: String): Pair<Boolean, String> {
        val db = this.readableDatabase
        try {
            var cursor = db.rawQuery(
                "SELECT 'student' AS role FROM students WHERE registrationNumber = ? AND password = ?",
                arrayOf(username, password)
            )
            if (cursor.moveToFirst()) {
                cursor.close()
                return Pair(true, "student")
            }

            cursor = db.rawQuery(
                "SELECT 'teacher' AS role FROM teachers WHERE uid = ? AND password = ?",
                arrayOf(username, password)
            )
            if (cursor.moveToFirst()) {
                cursor.close()
                return Pair(true, "teacher")
            }

            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error authenticating user", e)
        }
        return Pair(false, "")
    }

    // Register a new student or teacher
    fun registerUser(username: String, password: String, role: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply { put("password", password) }

        try {
            // Log the input values for debugging
            Log.d(
                "DatabaseHelper",
                "Registering user with username: $username, role: $role, password: $password"
            )

            if (role == "student") {
                // Check if the student already exists
                if (isUserExists("students", "registration_number", username)) {
                    Log.d(
                        "DatabaseHelper",
                        "Registration number $username already exists for student."
                    )
                    return false
                }
                values.put("registration_number", username)
                return db.insert("students", null, values) != -1L
            } else if (role == "teacher") {
                // Check if the teacher already exists
                if (isUserExists("teachers", "uid", username)) {
                    Log.d("DatabaseHelper", "UID $username already exists for teacher.")
                    return false
                }
                values.put("uid", username)
                return db.insert("teachers", null, values) != -1L
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error registering user", e)
        } finally {
            db.close()
        }

        return false
    }

    // Helper method to check if a user exists in the specified table and column
    private fun isUserExists(tableName: String, column: String, value: String): Boolean {
        val db = this.readableDatabase
        // Check if the value is not empty or null
        if (value.isEmpty()) {
            Log.e("DatabaseHelper", "Value for $column is empty or null")
            return true
        }

        Log.d("DatabaseHelper", "Checking if $value exists in $tableName table for $column column")
        val cursor = db.query(
            tableName, arrayOf(column),
            "$column = ?", arrayOf(value), null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()

        if (exists) {
            Log.d("DatabaseHelper", "$value already exists in $tableName")
        } else {
            Log.d("DatabaseHelper", "$value does not exist in $tableName")
        }

        return exists
    }


    // Method to add marks
    fun addMarks(studentRegistrationNumber: String, subject: String, marks: Int): Boolean {
        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("registration_no", studentRegistrationNumber)
                put("subject", subject)
                put("marks", marks)
            }
            db.insert("marks_table", null, values) != -1L
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding marks", e)
            false
        } finally {
            db.close()
        }
    }

    // Method to add attendance
    fun addAttendance(registrationNumber: String, date: String, isPresent: Boolean): Boolean {
        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("registration_no", registrationNumber)
                put("date", date)
                put("is_present", if (isPresent) 1 else 0)
            }
            db.insert("attendance_table", null, values) != -1L
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding attendance", e)
            false
        } finally {
            db.close()
        }
    }

    // Method to get marks for student
    fun getMarksForStudent(registrationNumber: String): List<Pair<String, Int>> {
        val marksList = mutableListOf<Pair<String, Int>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT subject, marks FROM marks_table WHERE registration_no = ?",
            arrayOf(registrationNumber)
        )

        if (cursor.moveToFirst()) {
            do {
                val subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                val marks = cursor.getInt(cursor.getColumnIndexOrThrow("marks"))
                marksList.add(Pair(subject, marks))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return marksList
    }

    // Method to get timetable for student
    fun getTimetableForStudent(className: String, sectionName: String): List<Pair<String, String>> {
        val timetableList = mutableListOf<Pair<String, String>>()
        val db = this.readableDatabase
        try {
            // Fetch section_id from students table using className and section
            val sectionIdQuery = """
            SELECT section_id FROM $TABLE_STUDENTS 
            WHERE className = ? AND section = ?
        """
            val sectionIdCursor = db.rawQuery(sectionIdQuery, arrayOf(className, sectionName))
            if (sectionIdCursor.moveToFirst()) {
                val sectionId = sectionIdCursor.getInt(0) // Get the section ID

                // Query timetable_table using the fetched section_id
                val timetableCursor = db.query(
                    "timetable_table", arrayOf("day", "subject"),
                    "section_id = ?", arrayOf(sectionId.toString()), null, null, "day ASC"
                )
                if (timetableCursor.moveToFirst()) {
                    do {
                        val day = timetableCursor.getString(timetableCursor.getColumnIndexOrThrow("day"))
                        val subject = timetableCursor.getString(timetableCursor.getColumnIndexOrThrow("subject"))
                        timetableList.add(Pair(day, subject))
                    } while (timetableCursor.moveToNext())
                }
                timetableCursor.close()
            }
            sectionIdCursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching timetable", e)
        } finally {
            db.close()
        }
        return timetableList
    }


    // Method to get teacher profile
    fun getTeacherProfile(uid: String): Pair<String, String>? {
        val db = this.readableDatabase
        var profile: Pair<String, String>? = null
        try {
            val cursor = db.query(
                "teachers", arrayOf("name", "uid"),
                "uid = ?", arrayOf(uid), null, null, null
            )
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val uid = cursor.getString(cursor.getColumnIndexOrThrow("uid"))
                profile = Pair(name, uid)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching teacher profile", e)
        } finally {
            db.close()
        }
        return profile
    }

    // Method to get student profile
    fun getStudentProfile(registrationNumber: String): Triple<String, Pair<String, String>, ByteArray?>? {
        val db = this.readableDatabase
        var profile: Triple<String, Pair<String, String>, ByteArray?>? = null
        try {
            val cursor = db.query(
                "students", arrayOf("name", "className", "section", "profileImage"),
                "registrationNumber = ?", arrayOf(registrationNumber),
                null, null, null
            )
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val className = cursor.getString(cursor.getColumnIndexOrThrow("className"))
                val section = cursor.getString(cursor.getColumnIndexOrThrow("section"))
                val profileImage = cursor.getBlob(cursor.getColumnIndexOrThrow("profileImage"))
                profile = Triple(name, Pair(className, section), profileImage)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching student profile", e)
        } finally {
            db.close()
        }
        return profile
    }



    // Update student profile
    fun updateStudentProfile(
        registrationNumber: String,
        newName: String,
        newPhoneNumber: String,
        newImageBytes: ByteArray?
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", newName)
            put("phoneNumber", newPhoneNumber)
            if (newImageBytes != null) put("profileImage", newImageBytes)
        }

        return try {
            val result = db.update(
                "students",
                contentValues,
                "registration_number = ?",
                arrayOf(registrationNumber)
            )
            result > 0 // Returns true if the update was successful
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating student profile", e)
            false
        } finally {
            db.close()
        }
    }

    fun getAllStudents(): List<Triple<String, String, String>> {
        val studentsList = mutableListOf<Triple<String, String, String>>()
        val db = this.readableDatabase
        try {
            val cursor = db.query(
                TABLE_STUDENTS, arrayOf("name", "registrationNumber", "phoneNumber"),
                null, null, null, null, "name ASC"
            )
            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val registrationNumber =
                        cursor.getString(cursor.getColumnIndexOrThrow("registrationNumber"))
                    val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
                    studentsList.add(Triple(name, registrationNumber, phoneNumber))
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching all students", e)
        } finally {
            db.close()
        }
        return studentsList
    }

    fun getAttendanceForStudent(registrationNumber: String): List<Pair<String, Boolean>> {
        val attendanceList = mutableListOf<Pair<String, Boolean>>()
        val db = this.readableDatabase
        try {
            val cursor = db.query(
                "attendance_table", arrayOf("date", "is_present"),
                "registration_no = ?", arrayOf(registrationNumber), null, null, null
            )
            if (cursor.moveToFirst()) {
                do {
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val isPresent = cursor.getInt(cursor.getColumnIndexOrThrow("is_present")) == 1
                    attendanceList.add(Pair(date, isPresent))
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching attendance", e)
        } finally {
            db.close()
        }
        return attendanceList
    }

    fun addSection(sectionName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", sectionName)
        }
        val result = db.insert("sections", null, contentValues)
        db.close()
        return result != -1L
    }

    fun addTimetable(sectionId: Int, day: String, time: String, subject: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("section_id", sectionId)
            put("day", day)
            put("time", time)
            put("subject", subject)
        }
        return db.insert("timetable_table", null, contentValues) != -1L
    }


    fun getAllSections(): List<Section> {
        val sections = mutableListOf<Section>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM sections", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                sections.add(Section(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return sections
    }


    @SuppressLint("Range")
    fun getAllClasses(): List<Class> {
        val classes = mutableListOf<Class>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, name FROM classes", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                classes.add(Class(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return classes
    }

    fun getSectionsByClassId(classId: Int): List<Section> {
        val sections = mutableListOf<Section>()
        val db = this.readableDatabase

        // Query the sections table for rows with the given classId
        val cursor = db.rawQuery("SELECT id, name FROM sections WHERE class_id = ?", arrayOf(classId.toString()))

        // Check if cursor retrieves any columns named 'id' or 'name'
        if (cursor.columnNames.contains("id") && cursor.columnNames.contains("name")) {
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve section id and name
                    val sectionId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val sectionName = cursor.getString(cursor.getColumnIndexOrThrow("name"))

                    sections.add(Section(sectionId, sectionName))  // Add Section object
                } while (cursor.moveToNext())
            }
        } else {
            // Log an error if the expected columns are missing
            Log.e("Database Error", "Columns 'id' or 'name' not found in 'sections' table")
        }

        cursor.close()
        return sections
    }




    fun getAllStudentClasses(): List<String> {
        val classesList = mutableListOf<String>()
        val db = this.readableDatabase
        // Query to fetch distinct class names
        val query = "SELECT DISTINCT className FROM $TABLE_STUDENTS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val className = cursor.getString(cursor.getColumnIndexOrThrow("className"))
                classesList.add(className) // Add the class name to the list
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return classesList
    }



    fun getSectionsByClassName(className: String): List<Section> {
        val sections = mutableListOf<Section>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT section_id, section FROM $TABLE_STUDENTS WHERE className = ?",
            arrayOf(className)
        )
        if (cursor.moveToFirst()) {
            do {
                val sectionId = cursor.getInt(cursor.getColumnIndexOrThrow("section_id"))
                val sectionName = cursor.getString(cursor.getColumnIndexOrThrow("section"))

                // Add section to the list
                sections.add(Section(id = sectionId, name = sectionName))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sections
    }
// DatabaseHelper.kt




    fun assignTimetableToSection(sectionId: Int, teacherId: Int): Boolean {
        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("section_id", sectionId)
                put("teacher_id", teacherId)
            }
            db.insert("timetable_assignments", null, values) != -1L
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error assigning timetable", e)
            false
        } finally {
            db.close()
        }
    }


    fun getStudentsByClassAndSection(className: String, sectionName: String): List<Student> {
        val students = mutableListOf<Student>()
        val db = this.readableDatabase

        // SQL query to select students by class and section
        val cursor = db.rawQuery(
            "SELECT id, name, registrationNumber, phoneNumber, rollNumber, section,className, profileImage FROM students WHERE className = ? AND section = ?",
            arrayOf(className, sectionName)
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val registrationNumber = cursor.getString(cursor.getColumnIndexOrThrow("registrationNumber"))
                val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
                val rollNumber = cursor.getString(cursor.getColumnIndexOrThrow("rollNumber"))
                val section = cursor.getString(cursor.getColumnIndexOrThrow("section"))
                val className = cursor.getString(cursor.getColumnIndexOrThrow("className"))
                val profileImage = cursor.getBlob(cursor.getColumnIndexOrThrow("profileImage"))
                students.add(Student(id, name, registrationNumber, phoneNumber, rollNumber, section, className, profileImage))

                // Add the student to the list
                students.add(Student(id, name, registrationNumber, phoneNumber, rollNumber, section, className,profileImage))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return students
    }

    // In DatabaseHelper.kt
    fun getTeacherProfileImage(teacherRegistrationNumber: String): ByteArray? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TEACHERS,
            arrayOf("profile_image"),
            "uid = ?",
            arrayOf(teacherRegistrationNumber),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val profileImage = cursor.getBlob(cursor.getColumnIndex("profile_image"))
            cursor.close()
            db.close()
            return profileImage
        }

        cursor.close()
        db.close()
        return null
    }



    // Closing the database connection in all methods ensures database stability
}






