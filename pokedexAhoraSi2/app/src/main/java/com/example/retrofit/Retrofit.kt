package com.example.retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{
    private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon/"
    //hace una instancia para poderlo ejecutar
    val api: ApiService by lazy{
        Retrofit.Builder().
            baseUrl(BASE_URL).
            addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}