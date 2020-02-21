package com.example.googleeatkot

import com.google.firebase.auth.FirebaseUser

data class User(
    var FbUser: FirebaseUser? = null,
    var MyPlacesList: List<FoodPlace>? = null
)