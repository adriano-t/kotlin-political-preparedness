package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

//Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(
    private val electionDao: ElectionDao,
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(electionDao, context) as T
        }
        throw IllegalArgumentException("modelClass is not assignable from VoterInfoViewModel")
    }
}