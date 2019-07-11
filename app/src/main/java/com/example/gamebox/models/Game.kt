package com.example.gamebox.models

data class Game(var id: Int, var name: String, var picture: String) {
    var playable = false
}