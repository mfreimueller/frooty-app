package com.mfreimueller.frooty.model

import android.annotation.SuppressLint
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Family(val id: Int, val name: String, val familyName: String, val personal: Boolean) {

}