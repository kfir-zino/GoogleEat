package com.example.googleeatkot


class UserData(var UserName : String, var UserEmail : String, var UserID : String)

class Group (var MemebersList : List<UserData>? = null, var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
}