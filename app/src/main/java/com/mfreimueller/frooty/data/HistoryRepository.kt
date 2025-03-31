package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mfreimueller.frooty.model.History
import kotlinx.serialization.json.Json

class HistoryRepository(baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    companion object {
        private const val GET_ALL_URL = "/history"
    }

    fun getAllForFamily(familyId: Int): LiveData<Result<List<History>>> {
        val result = MutableLiveData<Result<List<History>>>()
        val requestUrl = "$baseUrl$GET_ALL_URL?family_id=$familyId"

        val request = object : JsonObjectRequest(Method.GET,
            requestUrl, null,
            Response.Listener { response ->
                val historyList: MutableList<History> = mutableListOf()

                val historyJson = response.getJSONArray("history")
                for (i in 0 until historyJson.length()) {
                    val historyJson = historyJson.getJSONObject(i)

                    val history = Json.decodeFromString<History>(historyJson.toString())
                    historyList.add(history)
                }

                result.value = Result.success(historyList)
            },
            Response.ErrorListener { error ->
                result.value = Result.failure<List<History>>(Exception("Invalid credentials!")) // TODO better exc. handling!!!
            }) {

            override fun getHeaders(): Map<String?, String?>? {
                return setAuthHeaders(super.getHeaders())
            }
        }

        addRequest(request)

        return result
    }

}