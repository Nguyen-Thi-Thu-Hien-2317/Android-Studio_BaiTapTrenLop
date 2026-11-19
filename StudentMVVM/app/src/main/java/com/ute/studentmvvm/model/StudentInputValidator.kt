package com.ute.studentmvvm.model

/** Kiểm tra biểu mẫu ở UI trước khi gửi xuống ViewModel. */
object StudentInputValidator {
    private val emailPattern = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validate(name: String, code: String, email: String, gpaInput: String): String? {
        if (name.isBlank()) return "Vui lòng nhập họ tên sinh viên."
        if (code.isBlank()) return "Vui lòng nhập mã số sinh viên."
        if (email.isNotBlank() && !emailPattern.matches(email)) return "Email không đúng định dạng."
        val gpa = if (gpaInput.isBlank()) 0.0 else
            gpaInput.replace(',', '.').toDoubleOrNull() ?: return "GPA phải là số từ 0 đến 4."
        if (gpa !in 0.0..4.0) return "GPA phải nằm trong khoảng 0 đến 4."
        return null
    }
}
