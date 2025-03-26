package com.mfreimueller.frooty.ui.meals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mfreimueller.frooty.R
import com.mfreimueller.frooty.model.Meal

class MealAdapter(private val meals: List<Meal>): RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewMealName: TextView = itemView.findViewById(R.id.textViewMealName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.textViewMealName.text = meals[position].name
    }

    override fun getItemCount(): Int = meals.size
}