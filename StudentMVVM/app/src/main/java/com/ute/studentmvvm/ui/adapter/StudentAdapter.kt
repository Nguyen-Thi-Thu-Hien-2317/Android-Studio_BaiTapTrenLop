package com.ute.studentmvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ute.studentmvvm.databinding.ItemStudentRoomBinding
import com.ute.studentmvvm.model.Student
import java.util.Locale

/** RecyclerView + ListAdapter + DiffUtil, theo trang 10 của PDF. */
class StudentAdapter(
    private val onStudentClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentRoomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(
        private val binding: ItemStudentRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student) = with(binding) {
            tvFullName.text = student.fullName
            tvStudentCode.text = "MSSV: ${student.studentCode}"
            tvEmail.text = student.email
            tvGpa.text = "GPA: ${String.format(Locale.US, "%.2f", student.gpa)}"
            tvMajor.text = student.major
            tvAvatar.text = student.fullName.trim().take(1).uppercase(Locale.getDefault())
            root.setOnClickListener { onStudentClick(student) }
            btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem == newItem
    }
}
