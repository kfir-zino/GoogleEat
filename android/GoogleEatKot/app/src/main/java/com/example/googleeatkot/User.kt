package com.example.googleeatkot

import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseUser

class GroupData(var key : String? = "",var name : String = "")

data class User(var userData: UserData? = null, var MyPlacesList: List<FoodPlace>? = null,
                var MyGroupsList: List<GroupData>? = null)
