package com.example.gamebox.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gamebox.R
import com.example.gamebox.models.Score
import com.example.gamebox.network.GameWebService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_slidingpuzzle.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SlidingPuzzleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var nickname: String
    private var gameId: Int = 0
    private var idsArray = arrayListOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
    private var buttonsArray = arrayListOf<ImageButton>()
    private lateinit var timerGame: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slidingpuzzle)

        nickname = intent.getStringExtra("nickname")
        gameId = intent.getIntExtra("game_id", 0)

        var image: Bitmap? = null
        val t = object : Thread() {
            override fun run() {
                val valuesid = intArrayOf(1062, 1074, 1084, 168, 175, 237, 234, 1020, 1024)
                val i = valuesid.random()
                Log.d("Sliding puzzle", "Picture chosen id $i")
                image = Glide.with(this@SlidingPuzzleActivity)
                    .asBitmap()
                    .load("https://picsum.photos/id/$i/300/300")
                    .submit().get()

            }
        }
        t.start()
        t.join()

        shuffle()
        image?.let { cutImage(it) }

        image_0.setOnClickListener(this@SlidingPuzzleActivity)
        image_1.setOnClickListener(this@SlidingPuzzleActivity)
        image_2.setOnClickListener(this@SlidingPuzzleActivity)
        image_3.setOnClickListener(this@SlidingPuzzleActivity)
        image_4.setOnClickListener(this@SlidingPuzzleActivity)
        image_5.setOnClickListener(this@SlidingPuzzleActivity)
        image_6.setOnClickListener(this@SlidingPuzzleActivity)
        image_7.setOnClickListener(this@SlidingPuzzleActivity)
        image_8.setOnClickListener(this@SlidingPuzzleActivity)


        timerGame = object : CountDownTimer(60000, 1000) {

            @Suppress("DEPRECATION")
            override fun onTick(millisUntilFinished: Long) {
                val nb = millisUntilFinished / 1000
                sliding_chronometer.text = "$nb"

                if (nb <= 10)
                    sliding_chronometer.setBackgroundColor(resources.getColor(R.color.color_timer_warn))
                else if (nb <= 25)
                    sliding_chronometer.setBackgroundColor(resources.getColor(R.color.color_timer_mid))

            }

            override fun onFinish() {
                endGame("loose")
            }
        }

        timerGame.start()
    }

    override fun onPause() {
        super.onPause()

        timerGame.cancel()

    }

    override fun onClick(clickedView: View?) {
        if (clickedView != null) {
            when (clickedView.id) {
                R.id.image_0 -> slide(0)
                R.id.image_1 -> slide(1)
                R.id.image_2 -> slide(2)
                R.id.image_3 -> slide(3)
                R.id.image_4 -> slide(4)
                R.id.image_5 -> slide(5)
                R.id.image_6 -> slide(6)
                R.id.image_7 -> slide(7)
                R.id.image_8 -> slide(8)
            }
        }
    }

    private fun shuffle() {
        val tmp = arrayListOf<Int>()
        var i = 0
        while (i < 9) {
            val nb = (0 until idsArray.size).random()
            val elt = idsArray[nb]
            tmp.add(elt)
            idsArray.remove(elt)
            i++
        }
        idsArray = tmp

        buttonsArray.add(image_0)
        buttonsArray.add(image_1)
        buttonsArray.add(image_2)
        buttonsArray.add(image_3)
        buttonsArray.add(image_4)
        buttonsArray.add(image_5)
        buttonsArray.add(image_6)
        buttonsArray.add(image_7)
        buttonsArray.add(image_8)
    }

    private fun cutImage(base: Bitmap) {
        sliding_image.setImageBitmap(base)
        var i = 0
        while (i < 9) {
            val index = idsArray.indexOf(i)

            val row = i / 3
            val col = i % 3

            if (i == 8) {
                buttonsArray[index].setImageResource(
                    resources.getIdentifier(
                        "sliding_puzzle_black",
                        "drawable", packageName
                    )
                )
            } else {
                buttonsArray[index].setImageBitmap(
                    Bitmap.createBitmap(
                        base,
                        col * 100,
                        row * 100,
                        100,
                        100
                    )
                )
            }
            i++
        }
    }

    private fun checkBound(ind: Int): Int {
        if (ind - 3 >= 0 && idsArray[ind - 3] == 8 && (ind % 3) == ((ind - 3) % 3))
            return ind - 3
        if (ind - 1 >= 0 && idsArray[ind - 1] == 8 && (ind / 3) == ((ind - 1) / 3))
            return ind - 1
        if (ind + 1 < 9 && idsArray[ind + 1] == 8 && (ind / 3) == ((ind + 1) / 3))
            return ind + 1
        if (ind + 3 < 9 && idsArray[ind + 3] == 8 && (ind % 3) == ((ind + 3) % 3))
            return ind + 3
        return -1
    }

    private fun slide(ind: Int) {
        val ind_black = checkBound(ind)
        if (ind_black == -1) {
            Toast.makeText(this, "Slide not possible", Toast.LENGTH_SHORT)
                .show()
            return
        }


        val tmpDrawable = buttonsArray[ind].drawable
        buttonsArray[ind].setImageDrawable(buttonsArray[ind_black].drawable)
        buttonsArray[ind_black].setImageDrawable(tmpDrawable)

        val tmpInd = idsArray[ind]
        idsArray[ind] = idsArray[ind_black]
        idsArray[ind_black] = tmpInd

        var i = 0
        while (i < 9) {
            if (idsArray[i] != i)
                return
            i++
        }
        endGame("win")
    }

    private fun endGame(result: String) {
        if (result == "win")
            timerGame.cancel()
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
                        val intent = Intent(this@SlidingPuzzleActivity, ScoreActivity::class.java)

                        intent.putExtra("Game_Id", gameId)
                        intent.putExtra("result", result)
                        intent.putExtra("word", "")
                        startActivity(intent)
                    }
                }
            }
        }
        service.sendScore(Score(gameId, result, nickname)).enqueue(wsCallback)
    }

}