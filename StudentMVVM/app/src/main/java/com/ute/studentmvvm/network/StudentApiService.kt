package com.ute.studentmvvm.network

import retrofit2.http.GET

/**
 * REST contract minh họa: GET {BASE_URL}/students trả về List<StudentDto>.
 * PDF không cung cấp URL hoặc backend nên chỉ kích hoạt khi URL được cấu hình.
 */
interface StudentApiService {
    @GET("students")
    suspend fun getStudents(): List<StudentDto>
}
