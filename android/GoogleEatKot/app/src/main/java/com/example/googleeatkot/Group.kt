package com.example.googleeatkot

import com.google.firebase.database.DatabaseReference


class GroupData(var key : String? = "",var name : String = "", var groupDBRef : DatabaseReference? = null)

class Group (var MemebersList : List<UserData>? = null, var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
    fun AddMember2Group (memberList : List<UserData>) {

    }
}