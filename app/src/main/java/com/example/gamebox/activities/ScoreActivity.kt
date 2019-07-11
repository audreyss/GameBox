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
import com.example.gamebox.adapters.ScoreAdapter
import com.example.gamebox.models.ScoreItem
import com.example.gamebox.network.GameWebService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_score.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScoreActivity : AppCompatActivity(), View.OnClickListener {

    private var listScore: ArrayList<ScoreItem> = arrayListOf()
    private lateinit var result: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        result = intent.getStringExtra("result")
        val word = intent.getStringExtra("word")
        when (result) {
            "win" -> score_message.text = resources.getString(R.string.win_msg)
            "loose" -> {
                score_message.text = resources.getString(R.string.loose_msg)
                if (intent.getIntExtra("Game_Id", 0) == 2 && word != "")
                    score_message.text = score_message.text.toString() + "\nThe expected word was $word"
            }
            else -> score_message.text = resources.getString(R.string.draw_msg)
        }

        val url = "https://androidlessonsapi.herokuapp.com/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GameWebService = retrofit.create(GameWebService::class.java)
        val wsCallback: Callback<List<ScoreItem>> = object : Callback<List<ScoreItem>> {
            override fun onFailure(call: Call<List<ScoreItem>>, t: Throwable) {
                Log.w("WebService", "WebService call failed")
                Log.e("WebService", "Error code : " + t.message)
            }

            override fun onResponse(call: Call<List<ScoreItem>>, response: Response<List<ScoreItem>>) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("WebService", "WebService success " + responseData.size)
                        listScore = responseData as ArrayList<ScoreItem>
                        filterList(intent.getIntExtra("Game_Id", 0))
                        listScore.reverse()
                        initRecycler()
                    }
                }
            }
        }
        service.getScores().enqueue(wsCallback)
        score_button_menu.setOnClickListener(this@ScoreActivity)
    }

    fun filterList(game_id: Int) {
        val tmp: ArrayList<ScoreItem> = arrayListOf()
        for (elt in listScore) {
            if (elt.game_id == game_id) {
                tmp.add(elt)
            }
        }

        listScore = tmp
    }

    fun initRecycler() {

        score_list.setHasFixedSize(true)
        score_list.layoutManager = LinearLayoutManager(
            this@ScoreActivity,
            RecyclerView.VERTICAL,
            false
        )
        val recyclerAdapter = ScoreAdapter(
            this@ScoreActivity,
            listScore
        )
        score_list.adapter = recyclerAdapter
        val dividerItemDecoration = DividerItemDecoration(
            score_list.context,
            RecyclerView.VERTICAL
        )
        score_list.addItemDecoration(dividerItemDecoration)
    }

    override fun onClick(clickedView: View?) {
        if (clickedView != null) {
            when (clickedView.id) {
                R.id.score_button_menu -> {
                    val intent = Intent(this@ScoreActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}