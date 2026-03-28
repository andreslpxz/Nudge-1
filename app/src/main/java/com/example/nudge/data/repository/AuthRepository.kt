package com.example.nudge.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.nudge.BuildConfig
import com.example.nudge.data.models.Profile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class AuthRepository(
    private val supabaseClient: SupabaseClient,
    private val context: Context
) {
    val sessionStatus: Flow<SessionStatus> = supabaseClient.auth.sessionStatus

    val currentUser = supabaseClient.auth.currentUserOrNull()

    suspend fun signUpWithEmail(email: String, password: String, username: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = kotlinx.serialization.json.buildJsonObject {
                put("username", kotlinx.serialization.json.JsonPrimitive(username))
            }
        }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(context)

        // We'll use the Web Client ID for Supabase
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("822756700682-phoa446k7j1lqvm5p8mm12dkmcblv3ih.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (credential is GoogleIdTokenCredential) {
            supabaseClient.auth.signInWith(Google) {
                idToken = credential.idToken
            }
        }
    }

    suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    suspend fun getProfile(): Profile? {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return supabaseClient.postgrest["profiles"]
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<Profile>()
    }

    suspend fun updateUsername(username: String) {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return
        supabaseClient.postgrest["profiles"].update(
            {
                Profile::username eq username
            }
        ) {
            filter {
                eq("id", userId)
            }
        }
    }

    suspend fun uploadAvatar(file: File): String? {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        val fileName = "$userId/${UUID.randomUUID()}.jpg"
        val bucket = supabaseClient.storage.from("avatars")

        bucket.upload(fileName, file.readBytes())
        val url = bucket.publicUrl(fileName)

        supabaseClient.postgrest["profiles"].update(
            {
                Profile::avatar_url eq url
            }
        ) {
            filter {
                eq("id", userId)
            }
        }
        return url
    }
}
