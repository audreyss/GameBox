package com.example.gamebox.network

import com.example.gamebox.models.*
import retrofit2.Call
import retrofit2.http.*

interface GameWebService {
    @GET("game/list")
    fun listGames(): Call<List<Game>>

    @GET("game/details")
    fun gameDetails(@Query("game_id") game_id: Int): Call<GameDetail>

    @POST("game/score")
    fun sendScore(@Body score: Score): Call<Boolean>

    @GET("randomWord")
    fun getWord(
        @Query("api_key") api_key: String,
        @Query("minLength") minLength: Int,
        @Query("maxLength") maxLength: Int
    ): Call<ResultWord>

    @GET("game/scores")
    fun getScores(): Call<List<ScoreItem>>
}