package com.example.practica04_tsp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("customsearch/v1")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("cx") cx: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = "image",
        @Query("num") num: Int = 1,
    ): SearchResponse
}

object Api{
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

data class SearchResponse(
    val items: List<ImageItem>?
)

data class ImageItem(
    val link: String
)