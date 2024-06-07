package com.example.android.politicalpreparedness.election

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(
    private val electionDao: ElectionDao,
    private val application: Application,
) : ViewModel() {

    private val database = ElectionDatabase.getInstance(application)
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _electionId = MutableLiveData<Int>()

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _targetUrl = MutableLiveData<String?>()
    val targetUrl: LiveData<String?>
        get() = _targetUrl

    private val _followedText = MutableLiveData<String>(application.getString(R.string.loading))
    val followedText: LiveData<String>
        get() = _followedText

    private val _isBallotInfoAvailable = MutableLiveData<Boolean>()
    val isBallotInfoAvailable: LiveData<Boolean>
        get() = _isBallotInfoAvailable

    private val _isVotingLocationAvailable = MutableLiveData<Boolean>()
    val isVotingLocationAvailable: LiveData<Boolean>
        get() = _isVotingLocationAvailable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun setElectonId(electionId: Int) {
        viewModelScope.launch {
            _electionId.value = electionId
            val isElectionFollowed = database.electionDao.getElection(electionId) != null
            if (isElectionFollowed) {
                _followedText.value = application.getString(R.string.follow)
            } else {
                _followedText.value = application.getString(R.string.unfollow)
            }
        }
    }


    fun getVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val address = "${division.state.ifEmpty { "dc" }}, ${division.country}"
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(
                    address, electionId
                )
                _address.value =
                    _voterInfo.value?.state?.first()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
                        ?: application.getString(R.string.not_available)
                _isBallotInfoAvailable.value = _voterInfo.value?.state?.first()?.electionAdministrationBody?.ballotInfoUrl != null
                _isVotingLocationAvailable.value = _voterInfo.value?.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl != null
                Timber.tag(javaClass.name).d("voterInfo value: ${_voterInfo.value}")
            } catch (ex: Exception) {
                Timber.tag(javaClass.name).e(ex);
                Timber.tag(javaClass.name).e("Error retrieving voter info")
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun stateLocationsClick() {
        val url =
            _voterInfo.value?.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl;
        if (url != null) {
            _targetUrl.value = url
        } else {
            Toast.makeText(
                application,
                application.getString(R.string.voting_information_page_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun stateBallotClick() {
        val url = _voterInfo.value?.state?.first()?.electionAdministrationBody?.ballotInfoUrl;
        if (url != null) {
            _targetUrl.value = url
        } else {
            Toast.makeText(
                application,
                application.getString(R.string.ballot_information_page_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setNavigationComplete() {
        _targetUrl.value = null
    }

    fun toggleFollowing() {
        viewModelScope.launch {
            try {
                val electionId = _electionId.value ?: return@launch
                val election = _voterInfo.value?.election

                val isElectionFollowed = database.electionDao.getElection(electionId) != null

                if (isElectionFollowed) {
                    database.electionDao.deleteElection(electionId)
                    _followedText.value = application.getString(R.string.follow)
                } else if (election != null) {
                    database.electionDao.insert(election)
                    _followedText.value = application.getString(R.string.unfollow)
                }

            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle following state")
                Toast.makeText(
                    application, application.getString(R.string.retry), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}