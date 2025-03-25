package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

abstract class Repository(protected val baseUrl: String, private val dataStore: DataStore<Preferences>, protected val requestQueue: RequestQueue) {

    private val CSRF_TOKEN_KEY = "csrftoken"
    private val SESSION_ID_KEY = "sessionid"

    fun getAuthCookies(): Pair<String, String> {
        val csrfToken: String
        val sessionId: String

        runBlocking {
            csrfToken = dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(CSRF_TOKEN_KEY)] ?: ""
            }.first()

            sessionId = dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(SESSION_ID_KEY)] ?: ""
            }.first()

        }

        return Pair<String, String>(csrfToken, sessionId)
    }

    fun setAuthCookies(csrfToken: String, sessionId: String) {
        runBlocking {
            dataStore.edit { settings ->
                settings[stringPreferencesKey(CSRF_TOKEN_KEY)] = csrfToken
                settings[stringPreferencesKey(SESSION_ID_KEY)] = sessionId
            }
        }
    }

}