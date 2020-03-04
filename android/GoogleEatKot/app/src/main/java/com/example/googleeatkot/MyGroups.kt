package com.example.googleeatkot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.my_groups_1group.view.*
import kotlinx.android.synthetic.main.my_groups_list.*

class MyGroups : AppCompatActivity()  {
    lateinit var MyGroupsDBRef : DatabaseReference
    var currUser : FirebaseUser? = FirebaseAuth.getInstance()!!.currentUser
    var OurResult : Int = 0
    lateinit var MyGroupsList : MutableList<GroupData>
    lateinit var GroupsListView : ListView
    lateinit var newGroupButton : Button
    lateinit var enteredNewGroupButton : Button
    lateinit var groupNameText : TextView
    lateinit var UBundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_groups_list)
        if(currUser == null ){
            Log.w("PERMISSION_ERROR", "Unregistered user in Registered Only Activity")
            Toast.makeText(this, "Unregistered User not Allowed Here", Toast.LENGTH_LONG).show()
            OurResult = 1
            this.finish()
        }

        newGroupButton = findViewById(R.id.new_group_button)
        enteredNewGroupButton = findViewById(R.id.group_name_button)
        groupNameText = findViewById(R.id.group_name_text)
        newGroupButton.setOnClickListener {
            enteredNewGroupButton.visibility = View.VISIBLE
            groupNameText.visibility = View.VISIBLE
            enteredNewGroupButton.setOnClickListener {
                if (groupNameText.text.isEmpty()) {
                    enteredNewGroupButton.visibility = View.INVISIBLE
                    groupNameText.visibility = View.INVISIBLE
                    Toast.makeText(this, "Group name is empty, please enter group name...", Toast.LENGTH_SHORT).show()
                } else {
                    val databaseRef = FirebaseDatabase.getInstance().getReference("Groups")
                    val DBUserGroupRef = FirebaseDatabase.getInstance().getReference("Users").child(currUser!!.uid).child("MyGroups")
                    val key = databaseRef.push().key
                    val gData = GroupData(key, groupNameText.text.toString())
                    val newGroup = Group(null, null, gData)
                    //Adding new groups to "Groups" in DB
                    databaseRef.child(key!!).setValue(newGroup).addOnCompleteListener {
                       //successfully added a group to the general group list - removing add buttons
                        enteredNewGroupButton.visibility = View.INVISIBLE
                        groupNameText.visibility = View.INVISIBLE
                        //adding the user to the group he created by his email
                        val emailList : MutableList<String> = mutableListOf()
                        emailList.add(currUser!!.email!!) //according to Firebase user (not user in DB)
                        var userDataList : MutableList<UserData> = mutableListOf()
                        val DBUserRef = FirebaseDatabase.getInstance().getReference("Users")
                        DBUserRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                            override fun onDataChange(UsersDBList: DataSnapshot) {
                                var currentUser: UserData
                                for (user in UsersDBList.children) {
                                    currentUser = user.getValue(User::class.java)!!.userData!!
                                    if (currentUser.UserEmail in emailList) {
                                        userDataList.add(currentUser)
                                    }
                                }
                                if(newGroup.AddMember2Group(userDataList, databaseRef.child(key!!),DBUserGroupRef)==1){
                                    Toast.makeText(this@MyGroups, "The Creating User Not Found...", Toast.LENGTH_SHORT).show()
                                    Log.w("ACCESS_ERROR", "Cannot find registered user by email")
                                    OurResult = 1
//                                    this.finish()
                                }
                                Toast.makeText(this@MyGroups, "New Group ${groupNameText.text} was Created", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

            }


        }
        GroupsListView = findViewById(R.id.groups_list)
        MyGroupsList = mutableListOf()
        MyGroupsDBRef = FirebaseDatabase.getInstance().getReference("Users").child(currUser!!.uid).child("MyGroups")
        MyGroupsDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(GroupsListSnapshot: DataSnapshot) {
                if(GroupsListSnapshot!!.exists()){ //making sure we have places to show
                    MyGroupsList.clear()
                    for(group in GroupsListSnapshot.children){ //going through all places in MyPlaces
                        val ListedGroup = group.getValue(GroupData::class.java)
                        MyGroupsList.add(ListedGroup!!)
                    }

                    val addMember = findViewById<Button>(R.id.add_member_to_group)
                    addMember.visibility = View.INVISIBLE
                    val show_members = findViewById<Button>(R.id.show_group_members)
                    show_members.visibility = View.INVISIBLE
                    val poles = findViewById<Button>(R.id.group_poles)
                    poles.visibility = View.INVISIBLE
                    UBundle = intent.extras
                    val adapter = MyGroupsAdapterClass(this@MyGroups,R.layout.my_groups_1group, MyGroupsList, addMember, show_members, poles, UBundle)
                    GroupsListView.adapter = adapter
                }
                else Toast.makeText(this@MyGroups, "No Groups to Show", Toast.LENGTH_LONG).show()

            }



        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                UBundle = data.extras!!
            }
        }
    }

    override fun finish() {
        if(OurResult== 0) setResult(Activity.RESULT_OK)
        else setResult(Activity.RESULT_CANCELED)
        super.finish()
    }

    override fun onResume() {
        super.onResume()
        print("hi")
    }
}