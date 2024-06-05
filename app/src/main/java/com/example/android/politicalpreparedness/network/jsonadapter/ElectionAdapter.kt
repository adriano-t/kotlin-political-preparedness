package com.example.android.politicalpreparedness.network.jsonadapter

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson


class ElectionAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        Log.d("ElectionAdapter", "Parsing ocdDivisionId: $ocdDivisionId")
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "").substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter, "").substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        Log.d("ElectionAdapter", "returning division: $division")
        return division.id
    }
}
