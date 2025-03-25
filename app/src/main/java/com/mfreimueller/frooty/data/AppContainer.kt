package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.mfreimueller.frooty.MainActivity
import com.mfreimueller.frooty.dataStore

interface AppContainer {
    val loginRepository: LoginRepository
    val requestQueue: RequestQueue
}

class DefaultAppContainer : AppContainer {
    private val BASE_URL = "http://192.168.178.136:8000/api"

    override val requestQueue: RequestQueue
        get() {
            val requestQueue = Volley.newRequestQueue(MainActivity.applicationContext().applicationContext)
            requestQueue.start()

            return requestQueue
        }

    override val loginRepository: LoginRepository
        get() = LoginRepository(BASE_URL, MainActivity.applicationContext().dataStore, requestQueue)
}