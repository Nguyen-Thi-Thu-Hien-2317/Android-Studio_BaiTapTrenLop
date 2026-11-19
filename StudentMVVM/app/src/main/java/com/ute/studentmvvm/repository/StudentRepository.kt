package com.ute.studentmvvm.repository

import com.ute.studentmvvm.data.StudentDao
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.network.RetrofitClient
import com.ute.studentmvvm.network.StudentApiService
import com.ute.studentmvvm.network.toEntity
import kotlinx.coroutines.flow.Flow

/** SSOT: UI luôn nhận sinh viên từ Room, không lấy trực tiếp dữ liệu Retrofit. */
class StudentRepository(
    private val studentDao: StudentDao,
    private val apiService: StudentApiService = RetrofitClient.apiService
) {
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun insert(student: Student): Long = studentDao.insertStudent(student)
    suspend fun update(student: Student) = studentDao.updateStudent(student)
    suspend fun delete(student: Student) = studentDao.deleteStudent(student)
    suspend fun getStudentByCode(code: String): Student? = studentDao.getStudentByCode(code)

    suspend fun syncFromCloud(): Result<Int> {
        if (!RetrofitClient.isConfigured) {
            return Result.failure(
                IllegalStateException("Chưa cấu hình URL API trong RetrofitClient.kt")
            )
        }
        return try {
            val remote = apiService.getStudents()
            val valid = remote.asSequence()
                .filter { !it.fullName.isNullOrBlank() && !it.studentCode.isNullOrBlank() }
                .filter { (it.gpa ?: 0.0) in 0.0..4.0 }
                .distinctBy { it.studentCode?.trim() }
                .toList()
            val entities = valid.map { dto ->
                val existing = studentDao.getStudentByCode(dto.studentCode!!.trim())
                dto.toEntity(localId = existing?.id ?: 0)
            }
            if (entities.isNotEmpty()) studentDao.insertAll(entities)
            Result.success(entities.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
