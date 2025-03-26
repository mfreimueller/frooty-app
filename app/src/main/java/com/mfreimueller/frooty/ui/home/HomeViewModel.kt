package com.mfreimueller.frooty.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mfreimueller.frooty.FrootyApplication
import com.mfreimueller.frooty.data.FamilyRepository
import com.mfreimueller.frooty.model.Family
class HomeViewModel(private val familyRepository: FamilyRepository) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FrootyApplication)
                val familyRepository = application.container.familyRepository
                HomeViewModel(familyRepository = familyRepository)
            }
        }
    }

    fun getAllFamilies(): LiveData<Result<List<Family>>> {
        return familyRepository.getAll()
    }

}

