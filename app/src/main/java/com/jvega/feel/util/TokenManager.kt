package com.jvega.feel.util
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenManager {
    private var sharedPreferences: SharedPreferences? = null

    private const val PREF_FILE_NAME = "my_secure_prefs"
    private const val TOKEN_KEY = "token"

    fun initialize(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREF_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("TokenManager", "Error initializing EncryptedSharedPreferences", e)
        }
    }

    fun saveToken(token: String) {
        try {
            sharedPreferences?.edit()?.putString(TOKEN_KEY, token)?.apply()
        } catch (e: Exception) {
            Log.e("TokenManager", "Error saving token", e)
        }
    }

    fun getToken(): String? {
        return try {
            sharedPreferences?.getString(TOKEN_KEY, null)
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting token", e)
            null
        }
    }
}

