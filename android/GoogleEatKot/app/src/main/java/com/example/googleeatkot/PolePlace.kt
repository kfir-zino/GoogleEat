package com.example.googleeatkot

import com.google.firebase.database.DatabaseReference

class PolePlace(var foodPlace : FoodPlace? = null,
                var votersList : MutableList<UserData> = mutableListOf(),
                var polePlaceDBRef : DatabaseReference? = null) {
}