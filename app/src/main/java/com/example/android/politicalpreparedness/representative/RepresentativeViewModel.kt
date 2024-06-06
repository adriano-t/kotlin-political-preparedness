package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.provider.Telephony.Mms.Addr
import androidx.databinding.ObservableField
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
    private val application: Application,
) : ViewModel() {

    //Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val line1 = ObservableField<String>()
    val line2 = ObservableField<String?>()
    val city = ObservableField<String>()
    val state = ObservableField<String>()
    val zip = ObservableField<String>()

    fun buildAddress(): Address {
       return Address(
            line1 = line1.get() ?: "",
            line2 = line2.get(),
            city = city.get() ?: "",
            state = state.get() ?: "",
            zip = zip.get() ?: ""
        )
    }

    // fetch representatives from API from a provided address
    private fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            try {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.toFormattedString())
                _representatives.value = offices.flatMap { office: Office ->
                    office.getRepresentatives(officials)
                }
            } catch (e: Exception) {
                Timber.tag(javaClass.name).e(e.localizedMessage)
            }
        }
    }

    fun search() {
        getRepresentatives(buildAddress())
    }

    //TODO: Create function get address from geo location
    fun getAddressFromGeolocation() {

    }

    //TODO: Create function to get address from individual fields
    fun getAddressFromFields() {

    }

}
