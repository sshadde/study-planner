package ru.studyplanner.mobile.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.studyplanner.mobile.remote.CurrentUserDto
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenStore(private val context: Context) {
    private val tokenKey = stringPreferencesKey("encrypted_access_token")
    private val legacyTokenKey = stringPreferencesKey("access_token")
    private val userIdKey = longPreferencesKey("current_user_id")
    private val userEmailKey = stringPreferencesKey("current_user_email")
    private val userRoleKey = stringPreferencesKey("current_user_role")
    private val userFullNameKey = stringPreferencesKey("current_user_full_name")
    private val userGroupNameKey = stringPreferencesKey("current_user_group_name")

    var cachedToken: String? = null
        private set

    var cachedUser: CurrentUserDto? = null
        private set

    init {
        runBlocking {
            val preferences = context.dataStore.data.first()
            cachedToken = preferences[tokenKey]?.let(::decryptToken) ?: preferences[legacyTokenKey]
            cachedUser = readCachedUser(preferences)
        }
    }

    fun hasToken(): Boolean = !cachedToken.isNullOrBlank()

    suspend fun save(token: String) {
        cachedToken = token
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = encryptToken(token)
            preferences.remove(legacyTokenKey)
        }
    }

    suspend fun saveUser(user: CurrentUserDto) {
        cachedUser = user
        context.dataStore.edit { preferences ->
            writeCachedUser(preferences, user)
        }
    }

    suspend fun saveSession(token: String, user: CurrentUserDto) {
        cachedToken = token
        cachedUser = user
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = encryptToken(token)
            preferences.remove(legacyTokenKey)
            writeCachedUser(preferences, user)
        }
    }

    suspend fun clear() {
        cachedToken = null
        cachedUser = null
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(legacyTokenKey)
            preferences.remove(userIdKey)
            preferences.remove(userEmailKey)
            preferences.remove(userRoleKey)
            preferences.remove(userFullNameKey)
            preferences.remove(userGroupNameKey)
        }
    }

    private fun writeCachedUser(preferences: androidx.datastore.preferences.core.MutablePreferences, user: CurrentUserDto) {
        preferences[userIdKey] = user.id
        preferences[userEmailKey] = user.email
        preferences[userRoleKey] = user.role
        preferences[userFullNameKey] = user.fullName
        preferences[userGroupNameKey] = user.groupName
    }

    private fun readCachedUser(preferences: Preferences): CurrentUserDto? {
        val id = preferences[userIdKey] ?: return null
        val email = preferences[userEmailKey] ?: return null
        val role = preferences[userRoleKey] ?: return null
        val fullName = preferences[userFullNameKey] ?: return null
        val groupName = preferences[userGroupNameKey] ?: return null
        return CurrentUserDto(
            id = id,
            email = email,
            role = role,
            fullName = fullName,
            groupName = groupName
        )
    }

    private fun encryptToken(token: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val encrypted = Base64.encodeToString(cipher.doFinal(token.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
        return "$iv:$encrypted"
    }

    private fun decryptToken(encryptedToken: String): String? {
        return runCatching {
            val parts = encryptedToken.split(":", limit = 2)
            if (parts.size != 2) {
                return@runCatching null
            }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val payload = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
            String(cipher.doFinal(payload), Charsets.UTF_8)
        }.getOrNull()
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey ?: generateSecretKey()
    }

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val GCM_TAG_LENGTH = 128
        const val KEY_ALIAS = "study_planner_token_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}