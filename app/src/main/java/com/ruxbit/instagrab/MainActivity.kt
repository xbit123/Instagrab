package com.ruxbit.instagrab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_main_launch.setOnClickListener {
            packageManager.getLaunchIntentForPackage("com.instagram.android")
                ?.let { startActivity(it) } ?: Toast.makeText(
                this,
                "Instagram не найден на устройстве",
                Toast.LENGTH_LONG
            ).show()
        }

        lifecycleScope.launchWhenResumed {
            mainViewModel.usersFlow.collect { users ->
                tv_main_result.text = users.joinToString("\n") { "Логин: ${it.login} ФИО: ${it.nameSurname}" }
            }
        }
    }
}