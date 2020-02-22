package com.example.googleeatkot


class GroupData(var key : String? = "",var name : String = "")

class Group (var MemebersList : List<UserData>? = null, var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
}