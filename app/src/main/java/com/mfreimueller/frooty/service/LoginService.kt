package com.mfreimueller.frooty.service

import androidx.lifecycle.LiveData
import com.mfreimueller.frooty.data.AppContainer
import com.mfreimueller.frooty.data.DefaultAppContainer
import com.mfreimueller.frooty.data.LoginRepository

class LoginService(private val loginRepository: LoginRepository) {

    fun login(username: String, password: String): LiveData<Result<Boolean>> {
        return loginRepository.login(username, password)
    }

}