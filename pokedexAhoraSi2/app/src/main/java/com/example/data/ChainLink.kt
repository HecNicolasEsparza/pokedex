package com.example.data

class ChainLink(
    val species: PokemonSpecies,
    val evolves_to: List<ChainLink>
) {
    override fun equals(other: Any?): Boolean{
        return super.equals(other)
    }
    override fun hashCode(): Int{
        return super.hashCode()
    }
    override fun toString(): String{
        return super.toString()
    }
}