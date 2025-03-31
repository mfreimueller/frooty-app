package com.mfreimueller.frooty.ui.home

sealed class HomeListItem {
    data class Meal(val name: String, val weekday: String): HomeListItem()
    data class Week(val name: String): HomeListItem()
}