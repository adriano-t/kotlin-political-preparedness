package com.example.android.politicalpreparedness.election.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("electionList")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}