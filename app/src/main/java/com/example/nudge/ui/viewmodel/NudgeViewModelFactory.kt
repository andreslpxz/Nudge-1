package com.example.nudge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nudge.data.repository.AuthRepository
import com.example.nudge.data.repository.NudgeRepository

class NudgeViewModelFactory(
    private val repository: NudgeRepository? = null,
    private val authRepository: AuthRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NudgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NudgeViewModel(repository!!) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
