package com.example.myapplication.graphQL

import com.apollographql.apollo3.api.ApolloResponse
import com.example.myapplication2.GetAllPokemonsQuery
import com.example.myapplication2.GetPokemonQuery

interface PokemonRepository {
    suspend fun getPokemon(): ApolloResponse<GetAllPokemonsQuery.Data>

    suspend fun getPokemon(namePokemon: String): ApolloResponse<GetPokemonQuery.Data>

}