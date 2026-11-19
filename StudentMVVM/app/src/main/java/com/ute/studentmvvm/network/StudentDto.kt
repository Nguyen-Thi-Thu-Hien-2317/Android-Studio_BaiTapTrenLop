package com.ute.studentmvvm.network

import com.google.gson.annotations.SerializedName
import com.ute.studentmvvm.model.Student

/** Mẫu JSON REST tương ứng các thuộc tính của bảng Room. */
data class StudentDto(
    @SerializedName("id") val remoteId: Int? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("student_code") val studentCode: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("gpa") val gpa: Double? = null,
    @SerializedName("major") val major: String? = null
)

/** ID Room là cục bộ, không dùng ID trên server để tránh đè sinh viên khác. */
fun StudentDto.toEntity(localId: Int = 0): Student {
    val code = studentCode.orEmpty().trim()
    return Student(
        id = localId,
        fullName = fullName.orEmpty().trim(),
        studentCode = code,
        email = email?.trim().orEmpty().ifBlank { "${code.lowercase()}@ute.udn.vn" },
        gpa = gpa ?: 0.0,
        major = major?.trim().orEmpty().ifBlank { "Công Nghệ Thông Tin" }
    )
}
