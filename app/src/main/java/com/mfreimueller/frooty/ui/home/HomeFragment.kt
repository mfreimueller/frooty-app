package com.mfreimueller.frooty.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mfreimueller.frooty.R
import com.mfreimueller.frooty.databinding.FragmentHomeBinding
import com.mfreimueller.frooty.model.Family
import com.mfreimueller.frooty.model.History
import com.mfreimueller.frooty.ui.login.LoginViewModel
import com.mfreimueller.frooty.ui.meals.MealAdapter
import kotlinx.coroutines.runBlocking
import kotlin.getValue

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels { HomeViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        recyclerView = view.findViewById(R.id.recyclerViewSchedule)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout.setOnRefreshListener {
            // TODO: check if new week is viable and then ask for suggestions
            loadSuggestions()
        }

        viewLifecycleOwner.lifecycle.addObserver(object: DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)

                homeViewModel.getAllFamilies().observe(viewLifecycleOwner, Observer<Result<List<Family>>> { result ->
                    if (result.isSuccess) {
                        homeViewModel.families = result.getOrNull()!!

                        homeViewModel.getHistoryForCurrentFamily().observe(viewLifecycleOwner,
                            Observer<Result<List<History>>> { result ->
                                if (result.isSuccess) {
                                    homeViewModel.historyItems = result.getOrNull()!!
                                    recyclerView.adapter = HomeAdapter(homeViewModel.getAdapterListItems())
                                }
                            })
                    }
                })
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSuggestions() {
        // bail out if we are already too far in the future
        if (!homeViewModel.newWeekViable) {
            swipeRefreshLayout.isRefreshing = false
            return
        }

        // if the next week hasn't been created, we ask the user for permission
        if (homeViewModel.isCurrentWeekPlanned) {
            askForEarlyGeneration(requireContext()).observe(viewLifecycleOwner, Observer<Boolean> { shouldGenerate ->
                if (!shouldGenerate) {
                    swipeRefreshLayout.isRefreshing = false
                } else {
                    generateSuggestions()
                }
            })
        } else {
            generateSuggestions()
        }
    }

    private fun generateSuggestions() {
        homeViewModel.generateNextWeek().observe(viewLifecycleOwner, Observer<Result<List<History>>> { result ->
            if (result.isSuccess) {
                homeViewModel.addToHistory(result.getOrNull()!!)
                (recyclerView.adapter as HomeAdapter).update(homeViewModel.getAdapterListItems())

                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun askForEarlyGeneration(context: Context): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        AlertDialog.Builder(context)
            .setTitle("Neue Vorschläge")
            .setMessage("Wollen Sie die kommende Woche planen?")
            .setPositiveButton("Ja") { dialog, _ ->
                result.value = true
            }
            .setNegativeButton("Nein") { dialog, _ ->
                result.value = false
            }
            .show()

        return result
    }
}