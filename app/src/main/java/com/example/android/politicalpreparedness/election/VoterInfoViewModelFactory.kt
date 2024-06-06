package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

//Create Factory to generate VoterInfoViewModel with provided voter info datasource
class VoterInfoViewModelFactory(
    private val electionDao: ElectionDao,
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(electionDao, application) as T
        }
        throw IllegalArgumentException("modelClass is not assignable from VoterInfoViewModel")
    }
}