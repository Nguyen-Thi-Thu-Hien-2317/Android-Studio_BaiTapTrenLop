package com.ute.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.model.StudentInputValidator
import com.ute.studentmvvm.viewmodel.StudentViewModel

/** DialogFragment theo trang 13; không tự đóng khi dữ liệu nhập không hợp lệ. */
class AddStudentDialogFragment : DialogFragment() {
    private var _binding: DialogAddStudentBinding? = null
    private val viewModel: StudentViewModel by activityViewModels {
        (requireActivity() as MainActivity).studentViewModelFactory
    }

    companion object {
        private const val KEY_ID = "student_id"
        private const val KEY_NAME = "student_name"
        private const val KEY_CODE = "student_code"
        private const val KEY_EMAIL = "student_email"
        private const val KEY_GPA = "student_gpa"
        private const val KEY_MAJOR = "student_major"

        fun newInstance(student: Student? = null): AddStudentDialogFragment =
            AddStudentDialogFragment().apply {
                if (student != null) arguments = Bundle().apply {
                    putInt(KEY_ID, student.id)
                    putString(KEY_NAME, student.fullName)
                    putString(KEY_CODE, student.studentCode)
                    putString(KEY_EMAIL, student.email)
                    putDouble(KEY_GPA, student.gpa)
                    putString(KEY_MAJOR, student.major)
                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val b = DialogAddStudentBinding.inflate(LayoutInflater.from(requireContext()))
        _binding = b
        val args = arguments
        val isEdit = args?.containsKey(KEY_ID) == true
        if (isEdit) {
            b.etFullName.setText(args?.getString(KEY_NAME).orEmpty())
            b.etStudentCode.setText(args?.getString(KEY_CODE).orEmpty())
            b.etEmail.setText(args?.getString(KEY_EMAIL).orEmpty())
            b.etGpa.setText(args?.getDouble(KEY_GPA, 0.0).toString())
            b.etMajor.setText(args?.getString(KEY_MAJOR).orEmpty())
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(if (isEdit) "Sửa thông tin sinh viên" else "Thêm sinh viên mới")
            .setView(b.root)
            .setPositiveButton(if (isEdit) "Lưu thay đổi" else "Lưu vào Room", null)
            .setNegativeButton("Hủy", null)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? AlertDialog ?: return
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val b = _binding ?: return@setOnClickListener
            val name = b.etFullName.text?.toString()?.trim().orEmpty()
            val code = b.etStudentCode.text?.toString()?.trim().orEmpty()
            val email = b.etEmail.text?.toString()?.trim().orEmpty()
            val gpaText = b.etGpa.text?.toString()?.trim().orEmpty()
            val major = b.etMajor.text?.toString()?.trim().orEmpty()

            val error = StudentInputValidator.validate(name, code, email, gpaText)
            if (error != null) {
                when {
                    name.isBlank() -> b.tilFullName.error = error
                    code.isBlank() -> b.tilStudentCode.error = error
                    email.isNotBlank() && error.contains("Email") -> b.tilEmail.error = error
                    else -> b.tilGpa.error = error
                }
                return@setOnClickListener
            }
            val id = arguments?.getInt(KEY_ID) ?: 0
            val student = Student(
                id = id,
                fullName = name,
                studentCode = code,
                email = email.ifBlank { "${code.lowercase()}@ute.udn.vn" },
                gpa = gpaText.replace(',', '.').toDoubleOrNull() ?: 0.0,
                major = major.ifBlank { "Công Nghệ Thông Tin" }
            )
            if (id == 0) viewModel.insert(student) else viewModel.update(student)
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
