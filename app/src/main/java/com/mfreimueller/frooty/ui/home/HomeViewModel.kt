package com.mfreimueller.frooty.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mfreimueller.frooty.FrootyApplication
import com.mfreimueller.frooty.data.FamilyRepository
import com.mfreimueller.frooty.data.HistoryRepository
import com.mfreimueller.frooty.data.SuggestRepository
import com.mfreimueller.frooty.model.Family
import com.mfreimueller.frooty.model.History
import com.mfreimueller.frooty.util.compareWeek
import com.mfreimueller.frooty.util.isSameWeek
import com.mfreimueller.frooty.util.lastStartOfWeek
import com.mfreimueller.frooty.util.nextStartOfWeek
import com.mfreimueller.frooty.util.weekAndYearString
import com.mfreimueller.frooty.util.weekday
import java.time.LocalDate

class HomeViewModel(private val familyRepository: FamilyRepository, private val historyRepository: HistoryRepository, private val suggestRepository: SuggestRepository) : ViewModel() {

    var families: List<Family> = mutableListOf()
    var historyItems: List<History> = mutableListOf()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FrootyApplication)
                val familyRepository = application.container.familyRepository
                val historyRepository = application.container.historyRepository
                val suggestRepository = application.container.suggestRepository
                HomeViewModel(familyRepository = familyRepository, historyRepository = historyRepository, suggestRepository = suggestRepository)
            }
        }
    }

    /**
     * Returns true if either this or the upcoming week hasn't
     * been generated.
     */
    val newWeekViable: Boolean
        get() {
            if (historyItems.isEmpty()) return true

            val mostRecentDate = historyItems[0].date
            val today = LocalDate.now()

            // we only planned this week so far
            if (mostRecentDate.isSameWeek(today)) {
                return true
            }

            val nextWeek = LocalDate.now().plusWeeks(1)

            // we already planned the next week
            if (mostRecentDate.isSameWeek(nextWeek)) {
                return false
            }

            // safety check to prevent dates in the future to trigger new generation
            return mostRecentDate.isBefore(today)
        }

    val isCurrentWeekPlanned: Boolean
        get() {
            if (historyItems.isEmpty()) return false

            val today = LocalDate.now()
            val found = historyItems.binarySearch { today.compareWeek(it.date) }

            return found >= 0
        }

    val nextUnplannedWeek: LocalDate
        get() {
            val today = LocalDate.now()

            // if we have no history yet, we generate it for the current week
            if (historyItems.isEmpty()) return today.lastStartOfWeek

            val mostRecentDate = historyItems[0].date

            // If the last planned meal is older than 7 days, we skipped at least one
            // week. Thus we need to use the current week
            return if (mostRecentDate.until(today).days > 7) {
                today.lastStartOfWeek
            } else {
                mostRecentDate.nextStartOfWeek
            }
        }

    fun addToHistory(items: List<History>) {
        var newHistory = items.toMutableList()
        newHistory.addAll(historyItems)

        historyItems = newHistory.toList()
    }

    fun getAdapterListItems(): List<HomeListItem> {
        var homeListItems: MutableList<HomeListItem> = mutableListOf()
        var lastDate: LocalDate = LocalDate.MIN

        // we can assume that history is ordered by date in descending order
        historyItems.forEach { history ->
            // we insert a header for each new week
            if (!history.date.isSameWeek(lastDate)) {
                homeListItems.add(HomeListItem.Week(history.date.weekAndYearString))
            }

            homeListItems.add(HomeListItem.Meal(history.meal, history.date.weekday.take(2)))
            lastDate = history.date
        }

        return homeListItems
    }

    fun getAllFamilies(): LiveData<Result<List<Family>>> {
        return familyRepository.getAll()
    }

    fun getHistoryForCurrentFamily(): LiveData<Result<List<History>>> {
        return historyRepository.getAllForFamily(families[0].id)
    }

    fun generateNextWeek(): LiveData<Result<List<History>>> {
        return suggestRepository.suggestWeek(families[0].id, nextUnplannedWeek)
    }
}
