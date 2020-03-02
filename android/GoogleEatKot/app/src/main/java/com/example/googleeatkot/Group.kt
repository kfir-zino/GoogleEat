package com.example.googleeatkot

import android.widget.Toast
import com.google.firebase.database.*
import kotlin.coroutines.coroutineContext


class GroupData(var key : String? = "",var name : String = "")

class Group (var MemebersList : MutableList<UserData>? = mutableListOf(), var FoodPlaces : List<FoodPlace>? = null,
              var groupData : GroupData? = null) {
    fun AddMember2Group (newMemberList : List<UserData>, groupDBRef : DatabaseReference, userGroupDBRef : DatabaseReference? = null) : Int {
        var memberDataList : MutableList<UserData> = mutableListOf()
        //var emptyList = mutableListOf<UserData>()
//        var mailWrapper = userEmailWraper()
//        mailWrapper.userEmail2UserData(newMemberList)
//        memberDataList  = mailWrapper.userDataList
        if(newMemberList.size==0){ //No email matching
            return 1
        }
        for (member in newMemberList!!) {
            groupDBRef!!.child("MemebersList").child(member.UserID).setValue(member).addOnCompleteListener {
                //if adding the user to the group worked, add the group to the users "my groups"
                if (userGroupDBRef != null)
                    userGroupDBRef.child(groupData!!.key!!).setValue(groupData)
                else {
                    FirebaseDatabase.getInstance().getReference("Users").child(member.UserID).child("MyGroups").child(groupData!!.key!!).setValue(groupData)
                }
            }
        }
        return 0
    }
}