package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.CivicsApiService

//Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(
    private val app: Application,
    private val apiService: CivicsApiService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ElectionsViewModel(app, apiService) as T
        }
        throw IllegalArgumentException("modelClass is not assignable from ElectionsViewModel")
    }
}