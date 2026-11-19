package com.ute.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.studentmvvm.R
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.ui.adapter.StudentAdapter
import com.ute.studentmvvm.viewmodel.StudentViewModel
import java.util.Locale

class StudentListFragment : Fragment() {
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentViewModel by activityViewModels {
        (requireActivity() as MainActivity).studentViewModelFactory
    }
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onStudentClick = { showStudentDetailDialog(it) },
            onDeleteClick = { confirmDelete(it) }
        )
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
            binding.layoutEmpty.visibility = if (students.isEmpty()) View.VISIBLE else View.GONE
            binding.tvStudentCountBadge.text = "${students.size} sinh viên"
        }
        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener { openAddDialog() }
        binding.btnAddFirst.setOnClickListener { openAddDialog() }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sync -> { viewModel.syncFromCloud(); true }
                else -> false
            }
        }
    }

    private fun openAddDialog(student: Student? = null) {
        AddStudentDialogFragment.newInstance(student)
            .show(childFragmentManager, "ADD_STUDENT_DIALOG")
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa sinh viên ${student.fullName} khỏi CSDL Room?")
            .setPositiveButton("Xóa") { _, _ -> viewModel.delete(student) }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showStudentDetailDialog(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Thông tin sinh viên")
            .setMessage(
                "ID: ${student.id}\nHọ tên: ${student.fullName}" +
                    "\nMSSV: ${student.studentCode}\nEmail: ${student.email}" +
                    "\nGPA: ${String.format(Locale.US, "%.2f", student.gpa)}" +
                    "\nNgành: ${student.major}"
            )
            .setPositiveButton("Đóng", null)
            // Phần bổ sung hợp lý: tận dụng @Update DAO của trang 4 để sửa sinh viên.
            .setNeutralButton("Sửa") { _, _ -> openAddDialog(student) }
            .show()
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
