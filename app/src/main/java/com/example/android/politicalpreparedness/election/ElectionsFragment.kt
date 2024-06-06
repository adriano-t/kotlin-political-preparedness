package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import timber.log.Timber

class ElectionsFragment: Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View {
        binding = FragmentElectionBinding.inflate(inflater, container, false)
        Timber.i("bound inflated layout")

        val application = requireNotNull(this.activity).application
        val apiService = CivicsApi.retrofitService

        val viewModelFactory = ElectionsViewModelFactory(application, apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        Timber.tag(ElectionsViewModel.TAG).i("created viewmodel")
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Populate recycler adapters
        binding.upcomingElections.adapter = ElectionListAdapter(ElectionListener { election ->
            // Link elections to voter info
            this.findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election.id,
                    election.division
                )
            )
        })

        return binding.root

    }

    // Refresh adapters when fragment loads
    override fun onResume() {
        super.onResume()
        viewModel.fetchUpcomingElections()
    }
}