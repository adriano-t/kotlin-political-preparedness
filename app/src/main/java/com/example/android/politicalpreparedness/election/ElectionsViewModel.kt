package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class ElectionsViewModel(
    private val application: Application,
    private val apiService: CivicsApiService,
) : ViewModel() {
    private val database = ElectionDatabase.getInstance(application)

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
        Timber.tag(javaClass.name).i("Viewmodel initialized")
    }

    fun fetchUpcomingElections() {
        viewModelScope.launch {
            try {
                Timber.tag(javaClass.name).i("trying to get elections")
                val response = apiService.getElections()
                Timber.tag(javaClass.name).i("got elections")
                _upcomingElections.value = response.elections
            }
            catch (ex: Exception) {
                Timber.e(ex)
            }

        }
    }

    fun loadSavedElections() {
        viewModelScope.launch {
            try {
                _savedElections.value = database.electionDao.getElections()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}