package com.example.googleeatkot

class PoleData (var poleName : String = "", var poleKey : String = "", var active : Boolean = true)

class GroupPole(var poleData : PoleData? = null,
                var placesList : MutableList<PolePlace> = mutableListOf()) {
}