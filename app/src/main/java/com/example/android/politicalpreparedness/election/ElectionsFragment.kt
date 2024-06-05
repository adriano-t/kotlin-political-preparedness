package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApi
import timber.log.Timber

class ElectionsFragment: Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var viewModel: ElectionsViewModel
    // TODO: Declare ViewModel

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
        val adapter = ElectionListAdapter(ElectionListener { election ->
            // Handle election item click
        })

        binding.upcomingElections.adapter = adapter

        return binding.root
        // TODO: Add binding values

        // TODO: Link elections to voter info

        // TODO: Initiate recycler adapters

        // TODO: Populate recycler adapters
    }

    // TODO: Refresh adapters when fragment loads
}