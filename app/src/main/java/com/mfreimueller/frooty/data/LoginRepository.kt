package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject

class LoginRepository(baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    companion object {
        const val LOGIN_URL = "/users/login"
    }

    fun login(username: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        val credentialsMap: MutableMap<String, String> = mutableMapOf()
        credentialsMap.put("username", username)
        credentialsMap.put("password", password)

        val requestObject = JSONObject(credentialsMap)

        val request = object : JsonObjectRequest(Request.Method.POST, baseUrl + LOGIN_URL, requestObject,
            Response.Listener { response ->
                val success = response.get("success") as Boolean
                result.value = Result.success(success)
            },
            Response.ErrorListener { error ->
                result.value = Result.failure<Boolean>(Exception("Invalid credentials!"))
            }) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject?>? {
                val headers = response?.allHeaders ?: listOf<Header>()

                val csrfTokenCookie = headers.find { it.value.startsWith("csrftoken") }
                val sessionIdCookie = headers.find { it.value.startsWith("sessionid") }

                if (csrfTokenCookie != null && sessionIdCookie != null) {
                    setAuthCookies(csrfTokenCookie.value, sessionIdCookie.value)
                }

                return super.parseNetworkResponse(response)
            }
        }

        request.setRetryPolicy(DefaultRetryPolicy(5 * 60 * 1000, // 5 min
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request)

        return result
    }

}