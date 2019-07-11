package com.example.gamebox.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebox.R
import com.example.gamebox.adapters.GamerAdapter
import com.example.gamebox.models.Game
import com.example.gamebox.network.GameWebService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var listGames: ArrayList<Game> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // call API to get the list of games
        val url = "https://androidlessonsapi.herokuapp.com/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GameWebService = retrofit.create(GameWebService::class.java)
        val wsCallback: Callback<List<Game>> = object : Callback<List<Game>> {
            override fun onFailure(call: Call<List<Game>>, t: Throwable) {
                Log.w("WebService", "ListGames: WebService call failed")
                Log.e("WebService", "Error code : " + t.message)
            }

            override fun onResponse(call: Call<List<Game>>, response: Response<List<Game>>) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        listGames = responseData as ArrayList<Game>
                        changePlayable()
                        initRecycler()
                        Log.d("WebService", "ListGames: WebService success : " + responseData.size)
                    }
                }
            }
        }
        service.listGames().enqueue(wsCallback)
    }

    fun initRecycler() {
        val myItemClickListener = View.OnClickListener {
            val intent = Intent(this@MainActivity, GameDetailActivity::class.java)
            val position = it.tag as Int
            val clickedItem = listGames[position]

            intent.putExtra("Game_Id", clickedItem.id)
            startActivity(intent)


        }
        activity_main_list_game.setHasFixedSize(true)
        activity_main_list_game.layoutManager = LinearLayoutManager(
            this@MainActivity,
            RecyclerView.VERTICAL,
            false
        )
        val recyclerAdapter = GamerAdapter(
            this@MainActivity,
            listGames,
            myItemClickListener
        )
        activity_main_list_game.adapter = recyclerAdapter
        val dividerItemDecoration = DividerItemDecoration(
            activity_main_list_game.context,
            RecyclerView.VERTICAL
        )
        activity_main_list_game.addItemDecoration(dividerItemDecoration)
    }

    fun changePlayable() {
        for (game in listGames) {
            if (game.name == "SlidingPuzzle" || game.name == "Hangman") {
                game.playable = true
            }
        }
    }


}
