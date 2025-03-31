package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mfreimueller.frooty.model.History
import com.mfreimueller.frooty.util.formattedString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.time.LocalDate

class SuggestRepository(baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    companion object {
        private const val SUGGEST_URL = "/suggest"
    }

    fun suggestWeek(familyId: Int, startOfWeek: LocalDate): LiveData<Result<List<History>>> {
        val result = MutableLiveData<Result<List<History>>>()
        val requestUrl = "$baseUrl$SUGGEST_URL/"

        val requestMap: MutableMap<String, Any> = mutableMapOf()
        requestMap.put("familyId", familyId)
        requestMap.put("startDate", startOfWeek.formattedString)

        val requestObject = JSONObject(requestMap)

        val request = object : JsonObjectRequest(Method.POST,
            requestUrl, requestObject,
            Response.Listener { response ->
                val suggestList: MutableList<History> = mutableListOf()

                val suggestionsJson = response.getJSONArray("suggestions")
                for (i in 0 until suggestionsJson.length()) {
                    val suggestionJson = suggestionsJson.getJSONObject(i)

                    val history = Json.decodeFromString<History>(suggestionJson.toString())
                    suggestList.add(history)
                }

                result.value = Result.success(suggestList)
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