
package com.example.dangkytuongtac

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dangkytuongtac.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_STUDENT_ID = "extra_student_id"
        const val EXTRA_CLASS = "extra_class"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận dữ liệu từ MainActivity
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        val phone = intent.getStringExtra(EXTRA_PHONE).orEmpty()

        val studentId =
            intent.getStringExtra(EXTRA_STUDENT_ID).orEmpty()

        val className =
            intent.getStringExtra(EXTRA_CLASS).orEmpty()

        // Kiểm tra dữ liệu nhận được
        if (
            listOf(
                name, email, phone, studentId, className
            ).any { it.isBlank() }
        ) {
            Toast.makeText(
                this,
                R.string.missing_data,
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Hiển thị thông tin
        with(binding) {
            tvDetailName.text = name
            tvDetailEmail.text = email
            tvDetailPhone.text = phone
            tvDetailStudentId.text = studentId
            tvDetailClass.text = className

            // Quay lại Activity Đăng ký bằng Intent
            btnBack.setOnClickListener {

                val backIntent = Intent(
                    this@DetailActivity,
                    MainActivity::class.java
                ).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }

                startActivity(backIntent)
                finish()
            }
        }

        if (savedInstanceState == null) {
            Snackbar.make(
                binding.root,
                R.string.register_success,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
