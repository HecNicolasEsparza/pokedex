package com.example.myapplication.graphQL

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient


class GraphQL {
    fun getApolloClient(): ApolloClient{
        val okHttpClient = OkHttpClient.Builder().build()

        return ApolloClient.builder()
            .serverUrl("https://graphql-pokemon2.vercel.app/")
            .okHttpClient(okHttpClient)
            .build()
    }
}