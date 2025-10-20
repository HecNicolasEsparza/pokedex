package com.example.myapplication.utils

import org.json.JSONObject

object PokemonImageExtractor {

    fun extractImageUrls(spritesJson: String?): PokemonImages {
        if (spritesJson.isNullOrEmpty()) {
            return PokemonImages()
        }

        return try {
            val json = JSONObject(spritesJson)
            PokemonImages(
                frontDefault = json.optString("front_default").takeIf { it.isNotEmpty() },
                frontShiny = json.optString("front_shiny").takeIf { it.isNotEmpty() },
                backDefault = json.optString("back_default").takeIf { it.isNotEmpty() },
                backShiny = json.optString("back_shiny").takeIf { it.isNotEmpty() },
                frontFemale = json.optString("front_female").takeIf { it.isNotEmpty() },
                backFemale = json.optString("back_female").takeIf { it.isNotEmpty() },
                officialArtwork = json.optJSONObject("other")
                    ?.optJSONObject("official-artwork")
                    ?.optString("front_default")?.takeIf { it.isNotEmpty() },
                homeDefault = json.optJSONObject("other")
                    ?.optJSONObject("home")
                    ?.optString("front_default")?.takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            PokemonImages()
        }
    }

    fun getBestImage(images: PokemonImages): String? {
        return images.officialArtwork
            ?: images.homeDefault
            ?: images.frontDefault
            ?: images.frontShiny
    }
}

data class PokemonImages(
    val frontDefault: String? = null,
    val frontShiny: String? = null,
    val backDefault: String? = null,
    val backShiny: String? = null,
    val frontFemale: String? = null,
    val backFemale: String? = null,
    val officialArtwork: String? = null,
    val homeDefault: String? = null
)
