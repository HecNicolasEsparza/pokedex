package com.example.myapplication.utils

import android.graphics.Color

object PokemonTypeColors {

    fun getTypeColor(types: List<String>?): Pair<String, String> {
        if (types.isNullOrEmpty()) return Pair("#667eea", "#764ba2")

        val primaryType = types.first().lowercase()

        return when (primaryType) {
            "grass" -> Pair("#4CAF50", "#2E7D32")
            "fire" -> Pair("#FF5722", "#D32F2F")
            "water" -> Pair("#2196F3", "#1565C0")
            "electric" -> Pair("#FFEB3B", "#F57F17")
            "psychic" -> Pair("#E91E63", "#AD1457")
            "ice" -> Pair("#00BCD4", "#0097A7")
            "dragon" -> Pair("#673AB7", "#512DA8")
            "dark" -> Pair("#424242", "#212121")
            "fairy" -> Pair("#F8BBD9", "#E91E63")
            "fighting" -> Pair("#FF5722", "#BF360C")
            "poison" -> Pair("#9C27B0", "#7B1FA2")
            "ground" -> Pair("#FF9800", "#F57C00")
            "flying" -> Pair("#03A9F4", "#0288D1")
            "bug" -> Pair("#8BC34A", "#689F38")
            "rock" -> Pair("#795548", "#5D4037")
            "ghost" -> Pair("#9C27B0", "#7B1FA2")
            "steel" -> Pair("#607D8B", "#455A64")
            "normal" -> Pair("#9E9E9E", "#616161")
            else -> Pair("#667eea", "#764ba2")
        }
    }

    fun getTypeColorInt(types: List<String>?): Int {
        val (startColor, _) = getTypeColor(types)
        return Color.parseColor(startColor)
    }
}
