package com.mfreimueller.frooty.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mfreimueller.frooty.FrootyApplication
import com.mfreimueller.frooty.data.LoginRepository

class  LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FrootyApplication)
                val loginRepository = application.container.loginRepository
                LoginViewModel(loginRepository = loginRepository)
            }
        }
    }

    fun login(username: String, password: String): LiveData<Result<Boolean>> {
        return loginRepository.login(username, password)
    }
}