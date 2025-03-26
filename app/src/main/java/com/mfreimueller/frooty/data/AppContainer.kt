package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.mfreimueller.frooty.MainActivity
import com.mfreimueller.frooty.dataStore

interface AppContainer {
    val loginRepository: LoginRepository
    val familyRepository: FamilyRepository
    val mealRepository: MealRepository
    val requestQueue: RequestQueue
}

class DefaultAppContainer : AppContainer {
    private val SERVER_URL = "http://192.168.178.136:8000"
    private val BASE_URL = "$SERVER_URL/api"

    private var  _requestQueue: RequestQueue? = null
    override val requestQueue: RequestQueue
        get() {
            if (_requestQueue == null) {
                _requestQueue = Volley.newRequestQueue(MainActivity.applicationContext().applicationContext)
                _requestQueue!!.start()

            }

            return _requestQueue!!
        }

    override val loginRepository: LoginRepository
        get() = LoginRepository(SERVER_URL, BASE_URL, MainActivity.applicationContext().dataStore, requestQueue)

    override val familyRepository: FamilyRepository
        get() = FamilyRepository(BASE_URL, MainActivity.applicationContext().dataStore, requestQueue)

    override val mealRepository: MealRepository
        get() = MealRepository(BASE_URL, MainActivity.applicationContext().dataStore, requestQueue)
}