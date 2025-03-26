package com.mfreimueller.frooty.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mfreimueller.frooty.databinding.FragmentHomeBinding
import com.mfreimueller.frooty.model.Family
import com.mfreimueller.frooty.ui.login.LoginViewModel
import kotlin.getValue

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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

        viewLifecycleOwner.lifecycle.addObserver(object: DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)

                homeViewModel.getAllFamilies().observe(viewLifecycleOwner, Observer<Result<List<Family>>> { result ->
                    if (result.isSuccess) {
                        homeViewModel.families = result.getOrNull()!!
                    }
                })
            }
        })



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}