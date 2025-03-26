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

    companion object {
        val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"

        fun getAccessToken(dataStore: DataStore<Preferences>): String? {
            val accessToken: String?

            runBlocking {
                accessToken = dataStore.data.map { preferences ->
                    preferences[stringPreferencesKey(ACCESS_TOKEN_KEY)] ?: ""
                }.first()
            }

            return if (accessToken!!.isEmpty()) null else accessToken
        }
    }

    fun setAuthHeaders(headers: Map<String?, String?>): Map<String?, String?> {
        val accessToken = getAccessToken(dataStore)
        if (accessToken == null) {
            return headers
        }

        val mutableHeaders = headers.toMutableMap()
        mutableHeaders.put("Authorization", "Token ${getAccessToken(dataStore)}")
        return mutableHeaders.toMap()
    }

    protected fun storeAccessToken(accessToken: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(ACCESS_TOKEN_KEY)] = accessToken
            }
        }
    }

}