package com.example.myapplication.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonDataDB(
    val name: String?,
    val number: String?,
    val image: String?,
    val classification: String?,
    val types: List<String?>?,
    val maxCP: Int?,
    val maxHP: Int?
): Parcelable