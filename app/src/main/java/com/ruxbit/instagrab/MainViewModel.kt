package com.ruxbit.instagrab

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application): AndroidViewModel(application) {
    val usersFlow = AppDatabase.getInstance().userDao().getAll()
}