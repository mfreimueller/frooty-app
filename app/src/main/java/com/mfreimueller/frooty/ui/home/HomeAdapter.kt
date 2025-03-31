package com.mfreimueller.frooty.ui.home
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.mfreimueller.frooty.R

class HomeAdapter(private var items: List<HomeListItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MealViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textViewMealName: TextView = itemView.findViewById(R.id.textViewMealName)
        private val textViewWeekdayName: TextView = itemView.findViewById(R.id.textViewWeekdayName)

        fun bind(meal: HomeListItem.Meal) {
            textViewMealName.text = meal.name
            textViewWeekdayName.text = meal.weekday
        }
    }

    class WeekdayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textViewWeekdayName: TextView = itemView.findViewById<TextView>(R.id.textViewWeekName)

        fun bind(week: HomeListItem.Week) {
            textViewWeekdayName.text = week.name
        }
    }

    class AcceptViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAccept)

        fun bind(accept: HomeListItem.Accept) {
            buttonAccept.setOnClickListener(accept.listener)
        }
    }

    companion object {
        private const val ITEM = 0
        private const val WEEKDAY = 1
        private const val ACCEPT = 2
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(items: List<HomeListItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeListItem.Meal ->  ITEM
            is HomeListItem.Week -> WEEKDAY
            is HomeListItem.Accept -> ACCEPT
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
            MealViewHolder(view)
        } else if (viewType == WEEKDAY) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_week, parent, false)
            WeekdayViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_accept, parent, false)
            AcceptViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = items[position]) {
            is HomeListItem.Meal -> (holder as MealViewHolder).bind(item)
            is HomeListItem.Week -> (holder as WeekdayViewHolder).bind(item)
            is HomeListItem.Accept -> (holder as AcceptViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}