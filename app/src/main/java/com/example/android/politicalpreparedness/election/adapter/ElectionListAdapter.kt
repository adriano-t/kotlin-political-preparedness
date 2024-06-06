package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(
            ListItemElectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ElectionViewHolder, pos: Int) {
        val election = getItem(pos)
        viewHolder.bind(election)
        viewHolder.itemView.setOnClickListener{
            clickListener.onClick(election)
        }
    }

}

class ElectionViewHolder(private var binding: ListItemElectionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(election: Election) {
        binding.election = election
        // Call binding.executePendingBindings(), which causes the update to execute immediately.
        binding.executePendingBindings()
    }
}

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(p0: Election, p1: Election): Boolean {
        return p0.id == p1.id
    }

    override fun areContentsTheSame(p0: Election, p1: Election): Boolean {
        return p0.id == p1.id
    }
}

class ElectionListener(val callback: (election: Election) -> Unit) {
    fun onClick(election: Election) = callback(election)
}