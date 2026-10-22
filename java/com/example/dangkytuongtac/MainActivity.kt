
package com.example.dangkytuongtac

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.dangkytuongtac.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Các quy tắc kiểm tra dữ liệu
    private val nameRegex =
        Regex("^[\\p{L}\\p{M}]+(?:[ .'-][\\p{L}\\p{M}]+)*$")

    private val phoneRegex = Regex("^0[35789]\\d{8}$")
    private val studentIdRegex = Regex("^\\d{8,15}$")

    private val classRegex =
        Regex("^[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N}._ -]{1,19}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kiểm tra ngay khi nhập
        setupRealtimeValidation()

        // Nhấn ĐĂNG KÝ NGAY
        binding.btnRegister.setOnClickListener {
            registerStudent()
        }
    }

    private fun setupRealtimeValidation() = with(binding) {
        watch(edtName, tilName, ::validateName)
        watch(edtEmail, tilEmail, ::validateEmail)
        watch(edtPhone, tilPhone, ::validatePhone)
        watch(edtStudentId, tilStudentId, ::validateStudentId)
        watch(edtClass, tilClass, ::validateClass)
    }

    // Theo dõi thay đổi của EditText
    private fun watch(
        input: EditText,
        container: TextInputLayout,
        validator: (String) -> String?
    ) {
        input.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                container.error =
                    validator(s?.toString()?.trim().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun validateName(value: String): String? = when {
        value.isBlank() ->
            getString(R.string.error_name_required)

        value.length < 2 || !nameRegex.matches(value) ->
            getString(R.string.error_name_invalid)

        else -> null
    }

    private fun validateEmail(value: String): String? = when {
        value.isBlank() ->
            getString(R.string.error_email_required)

        !Patterns.EMAIL_ADDRESS.matcher(value).matches() ->
            getString(R.string.error_email_invalid)

        else -> null
    }

    private fun validatePhone(value: String): String? = when {
        value.isBlank() ->
            getString(R.string.error_phone_required)

        !phoneRegex.matches(value) ->
            getString(R.string.error_phone_invalid)

        else -> null
    }

    private fun validateStudentId(value: String): String? = when {
        value.isBlank() ->
            getString(R.string.error_id_required)

        !studentIdRegex.matches(value) ->
            getString(R.string.error_id_invalid)

        else -> null
    }

    private fun validateClass(value: String): String? = when {
        value.isBlank() ->
            getString(R.string.error_class_required)

        !classRegex.matches(value) ->
            getString(R.string.error_class_invalid)

        else -> null
    }

    private fun registerStudent() = with(binding) {

        val name = edtName.text?.toString()
            ?.trim().orEmpty()
            .replace(Regex("\\s+"), " ")

        val email = edtEmail.text?.toString()?.trim().orEmpty()
        val phone = edtPhone.text?.toString()?.trim().orEmpty()

        val studentId =
            edtStudentId.text?.toString()?.trim().orEmpty()

        val className =
            edtClass.text?.toString()?.trim().orEmpty()

        // Kiểm tra toàn bộ trường
        val checks = listOf(
            tilName to validateName(name),
            tilEmail to validateEmail(email),
            tilPhone to validatePhone(phone),
            tilStudentId to validateStudentId(studentId),
            tilClass to validateClass(className)
        )

        checks.forEach { (field, errorText) ->
            field.error = errorText
        }

        val firstInvalid = checks.indexOfFirst {
            it.second != null
        }

        if (firstInvalid != -1) {
            val inputs = listOf(
                edtName,
                edtEmail,
                edtPhone,
                edtStudentId,
                edtClass
            )

            inputs[firstInvalid].requestFocus()

            Snackbar.make(
                root,
                R.string.form_invalid,
                Snackbar.LENGTH_SHORT
            ).show()

            return@with
        }

        // Dữ liệu hợp lệ: chuyển sang màn hình Chi tiết
        val detailIntent = Intent(
            this@MainActivity,
            DetailActivity::class.java
        ).apply {
            putExtra(DetailActivity.EXTRA_NAME, name)
            putExtra(DetailActivity.EXTRA_EMAIL, email)
            putExtra(DetailActivity.EXTRA_PHONE, phone)
            putExtra(DetailActivity.EXTRA_STUDENT_ID, studentId)
            putExtra(DetailActivity.EXTRA_CLASS, className)
        }

        startActivity(detailIntent)
    }
}
