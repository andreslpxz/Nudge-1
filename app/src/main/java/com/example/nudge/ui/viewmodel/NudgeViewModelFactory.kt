package com.example.nudge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nudge.data.repository.NudgeRepository

class NudgeViewModelFactory(private val repository: NudgeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NudgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NudgeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
