package com.example.retrofit
import com.example.data.EvolutionChainResponse
import com.example.data.PokemonSpecies
import com.example.data.PokemonSpeciesResponse
import com.example.data.TypeRelationsResponse
import com.example.data.pokemon
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    @GET("")
    suspend fun getPokemonList(): pokemon

    @GET("{pokemon}")
    suspend fun getUser(@Path("pokemon") pokemon:String): pokemon

    @GET
    suspend fun getTypeData(@Url url: String): TypeRelationsResponse

    @GET
    suspend fun getPokemonSpecies(@Url url: String): PokemonSpeciesResponse

    @GET
    suspend fun getEvolutionChain(@Url url: String): EvolutionChainResponse


}