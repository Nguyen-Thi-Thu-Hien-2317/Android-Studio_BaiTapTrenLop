package com.ute.studentmvvm.network

import com.ute.studentmvvm.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Thay bằng URL HTTPS thực tế của server, ví dụ https://domain-cua-ban.vn/api/
    // PDF không nêu địa chỉ API, vì vậy mặc định không gọi mạng.
    private const val BASE_URL = "https://example.invalid/"

    val isConfigured: Boolean
        get() = BASE_URL != "https://example.invalid/"

    val apiService: StudentApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                    else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApiService::class.java)
    }
}
