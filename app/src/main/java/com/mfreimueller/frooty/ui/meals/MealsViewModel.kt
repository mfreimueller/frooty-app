package com.mfreimueller.frooty.ui.meals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mfreimueller.frooty.FrootyApplication
import com.mfreimueller.frooty.data.MealRepository
import com.mfreimueller.frooty.model.Meal

class MealsViewModel(private val mealRepository: MealRepository) : ViewModel() {

    var meals: List<Meal> = mutableListOf()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FrootyApplication)
                val mealRepository = application.container.mealRepository
                MealsViewModel(mealRepository = mealRepository)
            }
        }
    }

    fun getAllMeals(): LiveData<Result<List<Meal>>> {
        return mealRepository.getAll()
    }

}