package com.mfreimueller.frooty

import android.app.Application
import com.mfreimueller.frooty.data.AppContainer
import com.mfreimueller.frooty.data.DefaultAppContainer

class FrootyApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}