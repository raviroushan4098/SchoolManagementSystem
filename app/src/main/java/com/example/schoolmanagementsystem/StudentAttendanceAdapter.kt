import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.schoolmanagementsystem.R
import com.example.schoolmanagementsystem.models.Student


data class Student(val name: String, val registrationNumber: String)

class StudentAttendanceAdapter(
    private val context: Context,
    private val students: List<Student>
) : BaseAdapter() {

    private val attendanceMap: MutableMap<String, Boolean> = mutableMapOf() // Stores attendance status

    override fun getCount(): Int = students.size

    override fun getItem(position: Int): Any = students[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.student_attendance_item, parent, false)

        val student = students[position]
        val nameTextView: TextView = view.findViewById(R.id.studentName)
        val presentCheckBox: CheckBox = view.findViewById(R.id.checkBoxPresent)
        val absentCheckBox: CheckBox = view.findViewById(R.id.checkBoxAbsent)

        // Set student name
        nameTextView.text = student.name

        // Load previous state if available
        presentCheckBox.isChecked = attendanceMap[student.registrationNumber] == true
        absentCheckBox.isChecked = attendanceMap[student.registrationNumber] == false

        // When "Present" is checked, uncheck "Absent" and mark the student as present
        presentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceMap[student.registrationNumber] = true
                absentCheckBox.isChecked = false // Ensure "Absent" is unchecked
            } else if (!absentCheckBox.isChecked) {
                attendanceMap.remove(student.registrationNumber) // Clear attendance if neither is checked
            }
        }

        // When "Absent" is checked, uncheck "Present" and mark the student as absent
        absentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                attendanceMap[student.registrationNumber] = false
                presentCheckBox.isChecked = false // Ensure "Present" is unchecked
            } else if (!presentCheckBox.isChecked) {
                attendanceMap.remove(student.registrationNumber) // Clear attendance if neither is checked
            }
        }

        return view
    }

    // Method to retrieve attendance status for all students
    fun getAttendanceStatus(): Map<String, Boolean> = attendanceMap
}
