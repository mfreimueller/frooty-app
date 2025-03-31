package com.mfreimueller.frooty.ui.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _historyItems = MutableLiveData<List<History>>()
    val historyItems: LiveData<List<History>>
        get() = _historyItems

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
            if (historyItems.value == null || historyItems.value!!.isEmpty()) return true

            val mostRecentDate = historyItems.value!![0].date
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
            if (historyItems.value == null || historyItems.value!!.isEmpty()) return false

            val today = LocalDate.now()
            val found = historyItems.value!!.binarySearch { today.compareWeek(it.date) }

            return found >= 0
        }

    val nextUnplannedWeek: LocalDate
        get() {
            val today = LocalDate.now()

            // if we have no history yet, we generate it for the current week
            if (historyItems.value == null || historyItems.value!!.isEmpty()) {
                return today.lastStartOfWeek
            }

            val mostRecentDate = historyItems.value!![0].date

            // If the last planned meal is older than 7 days, we skipped at least one
            // week. Thus we need to use the current week
            return if (mostRecentDate.until(today).days > 7) {
                today.lastStartOfWeek
            } else {
                mostRecentDate.nextStartOfWeek
            }
        }

    /**
     * Adds new items to the history, dropping any unsaved items.
     */
    fun addToHistoryAndDropUnsaved(items: List<History>) {
        var newHistory = items.toMutableList()

        if (historyItems.value != null && !historyItems.value!!.isEmpty()) {

            val savedItems = historyItems.value!!.toMutableList()
            val iterator = savedItems.iterator()

            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.id == null) {
                    iterator.remove()
                } else {
                    // we know that null ids are only found at the beginning
                    // of the list. hence we can exit here
                    break
                }
            }

            newHistory.addAll(savedItems)
        }

        _historyItems.value = newHistory.toList()
    }

    fun getAdapterListItems(listener: View.OnClickListener): List<HomeListItem> {
        var homeListItems: MutableList<HomeListItem> = mutableListOf()

        if (historyItems.value == null || historyItems.value!!.isEmpty()) {
            return homeListItems
        }

        var lastItem: History? = null

        // we can assume that history is ordered by date in descending order
        historyItems.value!!.forEach { history ->
            // if we have new items (which don't have ids yet), we add
            // an accept button below the last one
            if (lastItem != null && lastItem.id == null && history.id != null) {
                homeListItems.add(HomeListItem.Accept(listener))
            }

            // we insert a header for each new week
            if (lastItem == null || !history.date.isSameWeek(lastItem.date)) {
                homeListItems.add(HomeListItem.Week(history.date.weekAndYearString))
            }

            homeListItems.add(HomeListItem.Meal(history.meal, history.date.weekday.take(2)))
            lastItem = history
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

    fun saveNewItems(): LiveData<Result<List<History>>> {
        if (historyItems.value == null || historyItems.value!!.isEmpty()) {
            return MutableLiveData(Result.success(listOf()))
        }

        val newHistoryItems = historyItems.value!!.filter { it.id == null }
        if (newHistoryItems.isEmpty()) {
            return MutableLiveData(Result.success(listOf()))
        }

        return historyRepository.createHistory(newHistoryItems)
    }
}
