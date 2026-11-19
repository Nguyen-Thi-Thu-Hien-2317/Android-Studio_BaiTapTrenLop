package com.ute.studentmvvm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ute.studentmvvm.R
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory

/** Single-Activity Host của StudentListFragment (PDF trang 14). */
class MainActivity : AppCompatActivity() {
    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val repository by lazy { StudentRepository(database.studentDao()) }

    // Dùng chung Factory với mọi Fragment (tránh activityViewModels() dùng factory mặc định).
    val studentViewModelFactory: StudentViewModelFactory by lazy {
        StudentViewModelFactory(repository)
    }
    private val viewModel: StudentViewModel by viewModels { studentViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Đảm bảo ViewModel khởi tạo cùng Activity.
        viewModel.allStudents
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, StudentListFragment())
                .commit()
        }
    }
}
