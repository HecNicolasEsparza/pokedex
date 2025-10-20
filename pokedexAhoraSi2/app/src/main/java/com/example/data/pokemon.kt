package com.example.data

class pokemon (
    var name : String,
    var id: Int,
    var heighy: Double,
    var base_experience: Int,
    var weight: Double,
    var height: Double,
    var  abilities: List <abilities>,
    var  sprites: sprites,
    var types: List <types>,
    var species: PokemonSpecies
){
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