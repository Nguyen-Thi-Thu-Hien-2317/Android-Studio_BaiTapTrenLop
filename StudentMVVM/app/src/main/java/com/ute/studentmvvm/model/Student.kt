package com.ute.studentmvvm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room Entity của bảng students_table, theo trang 4–5 của tài liệu. */
@Entity(
    tableName = "students_table",
    indices = [Index(value = ["student_code"], unique = true)]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "student_code") val studentCode: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "gpa") val gpa: Double,
    @ColumnInfo(name = "major") val major: String = "Công Nghệ Thông Tin"
)
