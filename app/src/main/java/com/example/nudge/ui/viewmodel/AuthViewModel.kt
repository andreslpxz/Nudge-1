package com.example.nudge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nudge.data.models.Profile
import com.example.nudge.data.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _authState.value = AuthState.Authenticated
                        fetchProfile()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _authState.value = AuthState.Unauthenticated
                        _userProfile.value = null
                    }
                    else -> _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = repository.getProfile()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.signInWithEmail(email, pass)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, user: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.signUpWithEmail(email, pass, user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al registrarse")
            }
        }
    }

    fun signInWithPhone(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.signInWithPhone(phone)
                _authState.value = AuthState.Idle // Ready for OTP
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al enviar código")
            }
        }
    }

    fun verifyPhoneOtp(phone: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.verifyPhoneOtp(phone, token)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Código inválido")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.signInWithGoogle()
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error con Google Sign-In")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

    fun updateUsername(name: String) {
        viewModelScope.launch {
            try {
                repository.updateUsername(name)
                fetchProfile()
            } catch (e: Exception) {
                // Error
            }
        }
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            try {
                repository.uploadAvatar(file)
                fetchProfile()
            } catch (e: Exception) {
                // Error
            }
        }
    }
}
