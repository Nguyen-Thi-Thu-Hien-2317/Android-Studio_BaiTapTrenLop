package com.ute.studentmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel theo trang 8: mọi thao tác CSDL dùng Dispatchers.IO. */
class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    val allStudents: LiveData<List<Student>> = repository.allStudents.asLiveData()
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    fun insert(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (repository.getStudentByCode(student.studentCode) != null) {
                _statusMessage.postValue("MSSV ${student.studentCode} đã tồn tại.")
                return@launch
            }
            repository.insert(student)
            _statusMessage.postValue("Đã thêm sinh viên: ${student.fullName}")
        } catch (e: Exception) {
            _statusMessage.postValue("Không thể thêm sinh viên: ${e.message}")
        }
    }

    fun update(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val sameCode = repository.getStudentByCode(student.studentCode)
            if (sameCode != null && sameCode.id != student.id) {
                _statusMessage.postValue("MSSV ${student.studentCode} đã tồn tại.")
                return@launch
            }
            repository.update(student)
            _statusMessage.postValue("Đã cập nhật sinh viên: ${student.fullName}")
        } catch (e: Exception) {
            _statusMessage.postValue("Không thể cập nhật sinh viên: ${e.message}")
        }
    }

    fun delete(student: Student) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.delete(student)
            _statusMessage.postValue("Đã xóa sinh viên: ${student.fullName}")
        } catch (e: Exception) {
            _statusMessage.postValue("Lỗi khi xóa: ${e.message}")
        }
    }

    fun syncFromCloud() = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.syncFromCloud()
        _statusMessage.postValue(result.fold(
            onSuccess = { "Đã đồng bộ $it sinh viên từ máy chủ vào Room." },
            onFailure = { "Chưa thể đồng bộ: ${it.message}. Dữ liệu cục bộ vẫn được giữ." }
        ))
    }
}
