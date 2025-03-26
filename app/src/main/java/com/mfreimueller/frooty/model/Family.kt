package com.mfreimueller.frooty.model

import kotlinx.serialization.Serializable

@Serializable
class Family(val id: Int, val name: String, val familyName: String, val personal: Boolean) {

}