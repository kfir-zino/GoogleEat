package com.example.googleeatkot

import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseUser

data class User(
    var UserName : String, var UserEmail : String, var UserID : String,
    var MyPlacesList: List<FoodPlace>? = null, var MyGroupsList: List<String>? = null)
