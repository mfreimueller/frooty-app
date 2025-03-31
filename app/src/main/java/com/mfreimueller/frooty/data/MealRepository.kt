package com.mfreimueller.frooty.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mfreimueller.frooty.model.Meal
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger

class MealRepository(baseUrl: String, dataStore: DataStore<Preferences>, requestQueue: RequestQueue) : Repository(baseUrl, dataStore, requestQueue) {

    companion object {
        private const val GET_ALL_URL = "/meals"
    }

    fun getAll(): LiveData<Result<List<Meal>>> {
        val result = MutableLiveData<Result<List<Meal>>>()

        val request = object : JsonObjectRequest(Method.GET, baseUrl + GET_ALL_URL, null,
            Response.Listener { response ->
                val meals: MutableList<Meal> = mutableListOf()

                val mealsJson = response.getJSONArray("meals")
                for (i in 0 until mealsJson.length()) {
                    val mealJson = mealsJson.getJSONObject(i)

                    try {
                        val meal = Json.decodeFromString<Meal>(mealJson.toString())
                        meals.add(meal)
                    } catch (ex: Exception) {
                        Logger.getLogger("MealRepository").log(Level.SEVERE, ex.toString())
                    }
                }

                result.value = Result.success(meals)
            },
            Response.ErrorListener { error ->
                result.value = Result.failure<List<Meal>>(Exception("Invalid credentials!"))
            }) {

            override fun getHeaders(): Map<String?, String?>? {
                return setAuthHeaders(super.getHeaders())
            }
        }

        addRequest(request)

        return result
    }
}