package com.example.googleeatkot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MyGroups : AppCompatActivity()  {
    lateinit var MyGroupsDBRef : DatabaseReference
    var currUser : FirebaseUser? = FirebaseAuth.getInstance()!!.currentUser
    var OurResult : Int = 0
    lateinit var MyGroupsList : MutableList<Group>
    lateinit var GroupsListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_groups_list)
        if(currUser == null ){
            Log.w("PERMISSION_ERROR", "Unregistered user in Registered Only Activity")
            Toast.makeText(this, "Unregistered User not Allowed Here", Toast.LENGTH_LONG).show()
            this.finish()
        }
        GroupsListView = findViewById(R.id.PlacesListView)
        MyGroupsList = mutableListOf()
        MyGroupsDBRef = FirebaseDatabase.getInstance().getReference("Groups")
        MyGroupsDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(PlacesListSnapshot: DataSnapshot) {
                if(PlacesListSnapshot!!.exists()){ //making sure we have places to show
                    MyGroupsList.clear()
                    for(group in PlacesListSnapshot.children){ //going through all places in MyPlaces
                        val ListedGroup = group.getValue(Group::class.java)
                        MyGroupsList.add(ListedGroup!!)
                    }

                    val adapter = MyGroupsAdapterClass(this@MyGroups,R.layout.my_groups_1group, MyGroupsList)
                    GroupsListView.adapter = adapter
                }
                else Toast.makeText(this@MyGroups, "No Groups to Show", Toast.LENGTH_LONG).show()
            }

        })
    }
    override fun finish() {
        if(OurResult== 0) setResult(Activity.RESULT_OK)
        else setResult(Activity.RESULT_CANCELED)
        super.finish()
    }
}