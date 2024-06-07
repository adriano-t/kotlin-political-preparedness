package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale


class DetailFragment : Fragment() {

    lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(60000)
        mLocationRequest.setFastestInterval(5000)
        binding = FragmentRepresentativeBinding.inflate(layoutInflater)

        val viewModelFactory = RepresentativeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[RepresentativeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        // Populate recycler adapters
        binding.representativesList.adapter = RepresentativeListAdapter(RepresentativeListener {})

        binding.buttonSearch.setOnClickListener {
            viewModel.search()
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                enableLocationSettingsAndGetLocation()
            }
        }

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.location_services_required),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("VisibleForTests")
    private fun enableLocationSettingsAndGetLocation() {
        val locationRequest: LocationRequest =
            LocationRequest.create().setInterval(10000).setFastestInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(builder.build())
            .addOnSuccessListener(requireActivity()) { getLocation() }
            .addOnFailureListener(requireActivity()) { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                        startIntentSenderForResult(
                            ex.resolution.intentSender,
                            REQUEST_TURN_DEVICE_LOCATION_ON,
                            null,
                            0,
                            0,
                            0,
                            null
                        )

                    } catch (sendEx: IntentSender.SendIntentException) {
                        Timber.e(javaClass.name, "error asking the permission")
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted
        if (grantResults.isNotEmpty()) {
            if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                enableLocationSettingsAndGetLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.pls_enable_location_permissions),
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val address = geoCodeLocation(location)
                if (address != null) {
                    viewModel.setAddress(address)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.could_not_find_location), Snackbar.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener {
            Snackbar.make(
                binding.root,
                getString(R.string.could_not_find_location), Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        return geocoder?.getFromLocation(location.latitude, location.longitude, 1)?.map { address ->
            Address(
                address.thoroughfare,
                address.subThoroughfare,
                address.locality,
                address.adminArea,
                address.postalCode
            )
        }?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}