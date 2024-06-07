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

        val electionDatabase = ElectionDatabase.getInstance(requireActivity())
        viewModel = ViewModelProvider(
            this, VoterInfoViewModelFactory(
                electionDatabase.electionDao,
                requireActivity().application
            )
        )[VoterInfoViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())
        if (args.argDivision.country.isNotEmpty()) {
            viewModel.setElectonId(args.argElectionId);
            viewModel.getVoterInfo(args.argElectionId, args.argDivision)
        } else {
            Timber.tag(javaClass.simpleName).e("Country and state should be valued $args")
            Toast.makeText(
                requireContext(), "Country and state should be valued", Toast.LENGTH_LONG
            ).show()
        }

        // Handle loading of URLs
        viewModel.targetUrl.observe(viewLifecycleOwner) { url ->
            if (url != null) openUrl(url)
        }

        viewModel.isBallotInfoAvailable.observe(viewLifecycleOwner) { isAvailable ->
            binding.stateBallot.visibility = if (isAvailable) View.VISIBLE else View.GONE
        }

        viewModel.isVotingLocationAvailable.observe(viewLifecycleOwner) { isAvailable ->
            binding.stateLocations.visibility = if (isAvailable) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return binding.root
    }


    // Create method to load URL intents
    private fun openUrl(url: String) {
        val intentUrl = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        viewModel.setNavigationComplete()
        startActivity(intentUrl)
    }
}