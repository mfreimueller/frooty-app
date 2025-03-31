package com.mfreimueller.frooty.model

import android.annotation.SuppressLint
import com.mfreimueller.frooty.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class History(
    val id: Int? = null,
    val meal: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val familyId: Int
)