package com.example.googleeatkot

import android.widget.Toast
import com.google.firebase.database.*
import kotlin.coroutines.coroutineContext


class GroupData(var key : String? = "",var name : String = "", var groupDBRef : DatabaseReference? = null)

class Group (var MemebersList : MutableList<UserData> = mutableListOf(), var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
    fun AddMember2Group (newMemberList : List<String>) : Int {
        var memberDataList : MutableList<UserData> = mutableListOf()
        memberDataList  = userEmail2UserData(newMemberList)
        if(memberDataList.size==0){ //No email matching
            return 1
        }
        for (member in memberDataList!!) {
            groupData!!.groupDBRef!!.child("MemebersList").child(member.UserID).setValue(member).addOnCompleteListener {
                //if adding the user to the group worked, add the group to the users "my groups"
                member.userDBRef!!.child("MyGroups").child(groupData!!.key!!).setValue(groupData)
            }
        }
        return 0
    }
}