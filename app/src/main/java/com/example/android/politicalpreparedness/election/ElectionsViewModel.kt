package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val application: Application,
    private val apiService: CivicsApiService,
) : ViewModel() {

    //Live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //Live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    //Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init {
        Timber.tag(TAG).i("Viewmodel initialized")
       fetchUpcomingElections()
    }

    private fun fetchUpcomingElections() {
        viewModelScope.launch {

                Timber.tag(TAG).i("trying to get elections")
                val response = apiService.getElections()
                Timber.tag(TAG).i("got elections")
                _upcomingElections.value = response.elections

        }
    }

    companion object {
        const val TAG = "ElectionsViewModel"
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info

}