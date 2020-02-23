package com.example.googleeatkot

import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class UserData(var UserName : String, var UserEmail : String, var UserID : String, var userDBRef : DatabaseReference? = null)

fun userEmail2UserData(emailList : List<String>) : MutableList<UserData> {
    val DBUserRef = FirebaseDatabase.getInstance().getReference("Users")
    var userDataList : MutableList<UserData> = mutableListOf()
    DBUserRef.addValueEventListener(object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(UsersDBList: DataSnapshot) {
            var currUser : UserData
            for (user in UsersDBList.children){
                currUser = user.getValue(User::class.java)!!.userData!!
                if(currUser.UserEmail in emailList){
                    userDataList.add(currUser)
                }
            }
        }

    })
    return userDataList
}

data class User(var userData: UserData? = null, var MyPlacesList: List<FoodPlace>? = null,
                var MyGroupsList: List<GroupData>? = null){
}
