package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Header
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class LoginRepository(private val serverUrl: String, baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    companion object {
        const val LOGIN_URL = "/auth/token/"
    }

    fun login(username: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        val credentialsMap: MutableMap<String, String> = mutableMapOf()
        credentialsMap.put("username", username)
        credentialsMap.put("password", password)

        val requestObject = JSONObject(credentialsMap)

        val request = JsonObjectRequest(Request.Method.POST, serverUrl + LOGIN_URL, requestObject,
            Response.Listener { response ->
                val accessToken = response.get("token") as String
                storeAccessToken(accessToken)

                result.value = Result.success(true)
            },
            Response.ErrorListener { error ->
                result.value = Result.failure<Boolean>(Exception("Invalid credentials!"))
            })

        addRequest(request)

        return result
    }

}