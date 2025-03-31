package com.mfreimueller.frooty.ui.home

import android.view.View

sealed class HomeListItem {
    data class Meal(val name: String, val weekday: String): HomeListItem()
    data class Week(val name: String): HomeListItem()
    data class Accept(val listener: View.OnClickListener?): HomeListItem()
}