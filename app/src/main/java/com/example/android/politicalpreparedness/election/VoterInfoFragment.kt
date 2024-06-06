package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import timber.log.Timber

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)

        // TODO: Add ViewModel values and create ViewModel
        val electionDatabase = ElectionDatabase.getInstance(requireActivity())
        viewModel = ViewModelProvider(
            this, VoterInfoViewModelFactory(
                electionDatabase.electionDao,
                requireActivity().application
            )
        )[VoterInfoViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // TODO: Add binding values

        // TODO: Populate voter info -- hide views without provided data.
        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
        if (args.argDivision.country.isNotEmpty()) {
            viewModel.getVoterInfo(args.argElectionId, args.argDivision)
        } else {
            Timber.tag(javaClass.simpleName).e("Country and state should be valued $args")
            Toast.makeText(
                requireContext(), "Country and state should be valued", Toast.LENGTH_LONG
            ).show()
        }

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */

        // Handle loading of URLs
        viewModel.targetUrl.observe(viewLifecycleOwner, Observer { url ->
            if (url != null) openUrl(url)
        })

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }


    // Create method to load URL intents
    private fun openUrl(url: String) {
        val intentUrl = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        viewModel.setNavigationComplete()
        startActivity(intentUrl)
    }
}