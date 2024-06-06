package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(
    private val electionDao: ElectionDao,
    private val context: Context,
) : ViewModel() {


    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _targetUrl = MutableLiveData<String?>()
    val targetUrl: LiveData<String?>
        get() = _targetUrl


    //TODO: Add var and methods to populate voter info

    fun getVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                val address = "${division.state.ifEmpty { "dc" }}, ${division.country}"
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(
                    address, electionId
                )
                _address.value =
                    _voterInfo.value?.state?.first()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
                        ?: context.getString(R.string.not_available)

                Timber.tag(javaClass.name).d("voterInfo value: ${_voterInfo.value}")
            } catch (ex: Exception) {
                Timber.tag(javaClass.name).e(ex);
                Timber.tag(javaClass.name).e("Error retrieving voter info")
            }
        }
    }

    //TODO: Add var and methods to support loading URLs
    fun stateLocationsClick() {
        _targetUrl.value =
            _voterInfo.value?.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun stateBallotClick() {
        _targetUrl.value =
            _voterInfo.value?.state?.first()?.electionAdministrationBody?.ballotInfoUrl
    }

    fun setNavigationComplete() {
        _targetUrl.value = null
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    fun getElectionFollowedText(): String {
        return "Follow";
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways.
     * It is directly related to how elections are saved/removed from the database.
     */

}