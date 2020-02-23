package com.example.googleeatkot

import android.widget.Toast
import com.google.firebase.database.*
import kotlin.coroutines.coroutineContext


class GroupData(var key : String? = "",var name : String = "")

class Group (var MemebersList : MutableList<UserData>? = mutableListOf(), var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
}

fun AddMember2Group (newMemberList : List<String>, groupDBRef : DatabaseReference, userGroupDBRef : DatabaseReference,groupData : GroupData ) : Int {
    var memberDataList : MutableList<UserData> = mutableListOf()
    //var emptyList = mutableListOf<UserData>()
    var mailWrapper = userEmailWraper()
    mailWrapper.userEmail2UserData(newMemberList)
    memberDataList  = mailWrapper.userDataList
    if(memberDataList.size==0){ //No email matching
        return 1
    }
    for (member in memberDataList!!) {
        groupDBRef!!.child("MemebersList").child(member.UserID).setValue(member).addOnCompleteListener {
            //if adding the user to the group worked, add the group to the users "my groups"
            userGroupDBRef.child(groupData!!.key!!).setValue(groupData)
        }
    }
    return 0
}