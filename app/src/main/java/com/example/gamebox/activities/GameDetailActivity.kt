package com.example.gamebox.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gamebox.network.GameWebService
import com.example.gamebox.R
import com.example.gamebox.models.GameDetail
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_gamedetails.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var details: GameDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamedetails)

        val url = "https://androidlessonsapi.herokuapp.com/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GameWebService = retrofit.create(GameWebService::class.java)
        val wsCallback: Callback<GameDetail> = object : Callback<GameDetail> {
            override fun onFailure(call: Call<GameDetail>, t: Throwable) {
                Log.w("WebService", "GameDetail: WebService call failed")
            }

            override fun onResponse(call: Call<GameDetail>, response: Response<GameDetail>) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("WebService", "GameDetail: WebService success")
                        details = responseData

                        Glide
                            .with(this@GameDetailActivity)
                            .load(details.picture)
                            .into(game_image)
                        game_name.text = details.name
                        game_type.text = details.type
                        game_nb.text = details.players.toString()
                        game_year.text = details.year.toString()
                        game_description.text = details.description_en
                        if (details.name == "SlidingPuzzle" || details.name == "Hangman") {
                            input_name.visibility = View.VISIBLE
                            icon_run.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        service.gameDetails(intent.getIntExtra("Game_Id", 0)).enqueue(wsCallback)
        button_bottom.setOnClickListener(this@GameDetailActivity)
        icon_run.setOnClickListener(this@GameDetailActivity)
    }

    private fun checkInputNickname(): Boolean {
        if (input_name.text.toString() == "") {
            Toast.makeText(this, "Please enter a nickname to play", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    override fun onClick(clickedView: View?) {
        if (clickedView != null) {
            when (clickedView.id) {
                R.id.button_bottom -> {
                    val implicitIntent = Intent(Intent.ACTION_VIEW)
                    implicitIntent.data = Uri.parse(details.url)
                    startActivity(implicitIntent)
                }
                R.id.icon_run -> {
                    if (details.name == "Hangman" && checkInputNickname()) {
                        val intent = Intent(this@GameDetailActivity, HangmanActivity::class.java)
                        intent.putExtra("nickname", input_name.text.toString())
                        intent.putExtra("game_id", details.id)
                        startActivity(intent)

                    } else if (details.name == "SlidingPuzzle" && checkInputNickname()) {
                        val intent = Intent(this@GameDetailActivity, SlidingPuzzleActivity::class.java)
                        intent.putExtra("nickname", input_name.text.toString())
                        intent.putExtra("game_id", details.id)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}