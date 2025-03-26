package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Header
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mfreimueller.frooty.model.Family
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.logging.Level
import java.util.logging.Logger

class FamilyRepository(baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    private val GET_ALL_URL = "/families"

    fun getAll(): LiveData<Result<List<Family>>> {
        val result = MutableLiveData<Result<List<Family>>>()

        val request = object : JsonObjectRequest(Method.GET, baseUrl + GET_ALL_URL, null,
            Response.Listener { response ->
                val families: MutableList<Family> = mutableListOf()

                val familiesJson = response.getJSONArray("families")
                for (i in 0 until familiesJson.length()) {
                    val familyJson = familiesJson.getJSONObject(i)

                    val family = Json.decodeFromString<Family>(familyJson.toString())
                    families.add(family)
                }

                result.value = Result.success(families)
            },
            Response.ErrorListener { error ->
                result.value = Result.failure<List<Family>>(Exception("Invalid credentials!"))
            }) {

            override fun getHeaders(): Map<String?, String?>? {
                return setAuthHeaders(super.getHeaders())
            }
        }

        request.setRetryPolicy(DefaultRetryPolicy(5 * 60 * 1000, // 5 min
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        requestQueue.add(request)

        return result

        return result
    }

}