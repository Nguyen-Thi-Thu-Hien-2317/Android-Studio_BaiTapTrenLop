package com.ute.studentmvvm

import com.ute.studentmvvm.model.StudentInputValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StudentInputValidatorTest {
    @Test fun validStudent() {
        assertNull(StudentInputValidator.validate("Nguyễn Văn An", "20110001", "an@ute.udn.vn", "3.85"))
    }
    @Test fun emptyName() {
        assertEquals("Vui lòng nhập họ tên sinh viên.",
            StudentInputValidator.validate("", "20110001", "", "3.2"))
    }
    @Test fun invalidGpa() {
        assertEquals("GPA phải nằm trong khoảng 0 đến 4.",
            StudentInputValidator.validate("Trần Thị Mai", "20110002", "", "5.5"))
    }
    @Test fun invalidEmail() {
        assertEquals("Email không đúng định dạng.",
            StudentInputValidator.validate("Lê Hoàng Phúc", "20110003", "wrong-email", "3.4"))
    }
}
