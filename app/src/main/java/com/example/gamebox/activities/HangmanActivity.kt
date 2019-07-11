package com.example.gamebox.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebox.network.GameWebService
import com.example.gamebox.R
import com.example.gamebox.models.ResultWord
import com.example.gamebox.models.Score
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_hangman.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HangmanActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var nickname: String
    private lateinit var word: String
    private var gameId: Int = 0
    private var calledletters: MutableSet<Char> = mutableSetOf()
    private var gameWord = StringBuilder("________")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nickname = intent.getStringExtra("nickname")
        gameId = intent.getIntExtra("game_id", 0)
        val url = "http://api.wordnik.com/v4/words.json/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GameWebService = retrofit.create(GameWebService::class.java)
        val wsCallback: Callback<ResultWord> = object : Callback<ResultWord> {
            override fun onFailure(call: Call<ResultWord>, t: Throwable) {
                Log.w("WebService", "Word: WebService call failed")
            }

            override fun onResponse(call: Call<ResultWord>, response: Response<ResultWord>) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        setContentView(R.layout.activity_hangman)
                        button_ok.setOnClickListener(this@HangmanActivity)
                        Log.d("WebService", "Word: WebService word success " + responseData.word)
                        word = responseData.word.toUpperCase()
                    }
                }
            }
        }
        service.getWord("a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5", 8, 8).enqueue(wsCallback)

    }

    override fun onClick(clickedView: View?) {
        if (clickedView != null) {
            when (clickedView.id) {
                R.id.button_ok -> {
                    val chosen = chosen_letter.text.toString()
                    if (chosen == "" || chosen.length != 1)
                        Toast.makeText(this, "Incorrect input for the letter", Toast.LENGTH_SHORT)
                            .show()
                    else {
                        addLetter(chosen)
                    }
                }
            }
        }
    }

    private fun addLetter(letter: String) {
        val c = letter[0].toUpperCase()
        if (calledletters.contains(c)) {
            Toast.makeText(this, "You have already chosen this letter", Toast.LENGTH_SHORT)
                .show()
            chosen_letter.text.clear()
            return
        }
        calledletters.add(c)
        var changed = false
        var index: Int = word.indexOf(c, 0)
        while (index != -1) {
            gameWord[index] = c
            index = word.indexOf(c, index + 1)
            changed = true
        }

        if (changed) {
            val str = StringBuilder("")
            var i = 0
            while (i < 8) {
                str.append(gameWord[i++])
                if (i != 8) {
                    str.append("  ")
                }
            }
            word_display.text = str.toString()
            Log.d("Hangman", "Real word $word game word : $gameWord")
            if (gameWord.toString() == word)
                endGame("win")
        } else {
            val i: Int = lives.text.toString().toInt() - 1
            lives.text = i.toString()
            hangman_icon.setImageResource(
                resources.getIdentifier(
                    "hangman$i",
                    "drawable", packageName
                )
            )
            val str = StringBuilder(tested_letters.text.toString())
            tested_letters.text = (str.append("$c  ")).toString()
            if (i == 0)
                endGame("loose")
        }
        chosen_letter.text.clear()

    }

    private fun endGame(result: String) {
        val url = "https://androidlessonsapi.herokuapp.com/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
        val service: GameWebService = retrofit.create(GameWebService::class.java)
        val wsCallback: Callback<Boolean> = object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.w("WebService", "Post score: WebService call failed")
                Log.e("WebService", "Error code : " + t.message)
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("WebService", "Post score: WebService word success ")
                        val intent = Intent(this@HangmanActivity, ScoreActivity::class.java)

                        intent.putExtra("Game_Id", gameId)
                        intent.putExtra("result", result)
                        intent.putExtra("word", word)
                        startActivity(intent)
                    }
                }
            }
        }
        service.sendScore(Score(gameId, result, nickname)).enqueue(wsCallback)
    }
}