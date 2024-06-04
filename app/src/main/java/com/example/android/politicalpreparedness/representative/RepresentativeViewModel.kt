package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(
    private val civicApi: CivicsApi,
) : ViewModel() {

    //Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private var _queryAddress = MutableLiveData<Address>()
    val queryAddress: LiveData<Address>
        get() = _queryAddress

    // fetch representatives from API from a provided address
    fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            try {
                val (offices, officials) = civicApi.retrofitService.getRepresentatives(address.toFormattedString())
                _representatives.value = offices.flatMap { office: Office ->
                    office.getRepresentatives(officials)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e.localizedMessage)
            }
        }
    }

    //TODO: Create function get address from geo location
    fun getAddressFromGeolocation() {

    }

    //TODO: Create function to get address from individual fields
    fun getAddressFromFields() {

    }

    companion object {
        const val TAG = "RepresentativeViewModel"
    }
}
