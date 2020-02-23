package com.example.googleeatkot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
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
    lateinit var newGroupButton : Button
    lateinit var enteredNewGroupButton : Button
    lateinit var groupNameText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_groups_list)
        if(currUser == null ){
            Log.w("PERMISSION_ERROR", "Unregistered user in Registered Only Activity")
            Toast.makeText(this, "Unregistered User not Allowed Here", Toast.LENGTH_LONG).show()
            this.finish()
        }

        newGroupButton = findViewById(R.id.new_group_button)
        enteredNewGroupButton = findViewById(R.id.group_name_button)
        groupNameText = findViewById(R.id.group_name_text)
        newGroupButton.setOnClickListener {
            enteredNewGroupButton.visibility = View.VISIBLE
            groupNameText.visibility = View.VISIBLE
            enteredNewGroupButton.setOnClickListener {
                if (groupNameText.text == "") {
                    Toast.makeText(this, "Group name is empty, please enter group name...", Toast.LENGTH_SHORT).show()
                } else {
                    val databaseRef = FirebaseDatabase.getInstance().getReference("Groups")
                    val DBUserGroupRef = FirebaseDatabase.getInstance().getReference("Users").child(currUser!!.uid).child("MyGroups")
                    val key = databaseRef.push().key
                    val gData = GroupData(key, groupNameText.text.toString(),databaseRef.child(key!!))
                    databaseRef.child(key!!).setValue(Group(null, null, gData))
                    DBUserGroupRef.child(key).setValue(gData).addOnCompleteListener {
                        enteredNewGroupButton.visibility = View.INVISIBLE
                        groupNameText.visibility = View.INVISIBLE
                    }
                }

            }


        }
        GroupsListView = findViewById(R.id.groups_list)
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