package com.example.googleeatkot

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_groups_1group.view.*
import kotlinx.android.synthetic.main.my_groups_list.view.*

class MyGroupsAdapterClass (val mCtx : Context, val LayoutResId : Int, val MyGroupsList : List<GroupData>, val add_member: Button, val show_members: Button, val poles: Button, val UBundle : Bundle)
: ArrayAdapter<GroupData>(mCtx, LayoutResId, MyGroupsList) {
//    lateinit var poleIntent: Intent

    @SuppressLint("ViewHolder", "ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        var OurResult = 1
        val MyGroupView: View = layoutInflater.inflate(LayoutResId, null)
        val currGroup = MyGroupsList[position]
        val textViewGroupName = MyGroupView.findViewById<TextView>(R.id.textViewGroupName)
        textViewGroupName.text = currGroup.name
        var currUser : FirebaseUser? = FirebaseAuth.getInstance()!!.currentUser
        textViewGroupName.setOnClickListener {
            add_member.visibility = Button.VISIBLE
            show_members.visibility = View.VISIBLE
            poles.visibility = View.VISIBLE
            add_member.setOnClickListener {
                add_member.visibility = Button.INVISIBLE
                show_members.visibility = View.INVISIBLE
                poles.visibility = View.INVISIBLE
                val emailList: MutableList<String> = mutableListOf()
                val addMemberLayoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
                val addMemberView: View = layoutInflater.inflate(R.layout.add_member_window, null)
                val popupWindow = PopupWindow(
                    addMemberView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // Set an elevation for the popup window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.elevation = 10.0F
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Create a new slide animation for popup window enter transition
                    val slideIn = Slide()
                    slideIn.slideEdge = Gravity.TOP
                    popupWindow.enterTransition = slideIn

                    // Slide animation for popup window exit transition
                    val slideOut = Slide()
                    slideOut.slideEdge = Gravity.RIGHT
                    popupWindow.exitTransition = slideOut

                }
                val mail = addMemberView.findViewById<TextView>(R.id.member_mail)
                val add_member_button = addMemberView.findViewById<TextView>(R.id.add_member)
                val add_member_done = addMemberView.findViewById<TextView>(R.id.Done)
                popupWindow.isFocusable = true
                popupWindow.update()
                add_member_button.setOnClickListener {
                    //                    add_member.visibility = Button.INVISIBLE
//                    show_members.visibility = View.INVISIBLE
//                    poles.visibility = View.INVISIBLE
                    if (mail.text.isNotEmpty()) {
                        emailList.add(mail.text.toString().trim().toLowerCase())
                    }
                }
                add_member_done.setOnClickListener {
                    //                    add_member.visibility = Button.INVISIBLE
//                    show_members.visibility = View.INVISIBLE
//                    poles.visibility = View.INVISIBLE
                    popupWindow.dismiss()

                    val key = currGroup.key
                    val databaseRef =
                        FirebaseDatabase.getInstance().getReference("Groups") // .child(key!!)
                    var newGroup = Group(null, null, currGroup)
                    //adding the user to the group he created by his email TODO : change to proper commit
                    //emailList.add(currUser!!.email!!) //according to Firebase user (not user in DB)
                    var userDataList: MutableList<UserData> = mutableListOf()
                    val DBUserRef = FirebaseDatabase.getInstance().getReference("Users")
                    DBUserRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        @SuppressLint("DefaultLocale")
                        override fun onDataChange(UsersDBList: DataSnapshot) {
                            var currentUser: UserData
                            userDataList.clear()
                            for (user in UsersDBList.children) {
                                currentUser = user.getValue(User::class.java)!!.userData!!
                                if (emailList.contains(currentUser.UserEmail)) {
                                    userDataList.add(currentUser)
                                }
                            }
                            if (newGroup!!.AddMember2Group(
                                    userDataList,
                                    databaseRef.child(key!!),
                                    null
                                ) == 1
                            ) {
                                Toast.makeText(
                                    this@MyGroupsAdapterClass.context,
                                    "The Creating User Not Found...",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.w("ACCESS_ERROR", "Cannot find registered user by email")
                                OurResult = 1
//                                    this.finish()
                            }
                            Toast.makeText(
                                this@MyGroupsAdapterClass.context,
                                "New Group " + textViewGroupName.text + " was Created",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
                TransitionManager.beginDelayedTransition(MyGroupView as ViewGroup?)
                popupWindow.showAtLocation(
                    MyGroupView, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
                )



            }

            show_members.setOnClickListener {
                add_member.visibility = Button.INVISIBLE
                show_members.visibility = View.INVISIBLE
                poles.visibility = View.INVISIBLE
                val MemberListView: View = layoutInflater.inflate(R.layout.group_members_list, null)
                val membersList = MemberListView.findViewById<ListView>(R.id.members_list)
                membersList.setBackgroundColor(R.color.quantum_googgreen)
                val userDataList: MutableList<UserData> = mutableListOf()
                val DBGroupMembersRef = FirebaseDatabase.getInstance().getReference("Groups").child(currGroup!!.key.toString()).child("MemebersList")
                DBGroupMembersRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(membersUserData: DataSnapshot) {
                        userDataList.clear()
                        for (userData in membersUserData.children) {
                            userDataList.add(userData.getValue(UserData::class.java)!!)
                        }
                        val adapter = MyGroupMembersAdapter(mCtx, R.layout.group_members_1member, userDataList)
                        membersList.adapter = adapter
                        val popupWindow = PopupWindow(
                            MemberListView,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        // Set an elevation for the popup window
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            popupWindow.elevation = 10.0F
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Create a new slide animation for popup window enter transition
                            val slideIn = Slide()
                            slideIn.slideEdge = Gravity.TOP
                            popupWindow.enterTransition = slideIn

                            // Slide animation for popup window exit transition
                            val slideOut = Slide()
                            slideOut.slideEdge = Gravity.RIGHT
                            popupWindow.exitTransition = slideOut

                        }
                        popupWindow.isFocusable = true
                        popupWindow.update()
                        TransitionManager.beginDelayedTransition(MemberListView as ViewGroup?)
                        popupWindow.showAtLocation(
                            MemberListView, // Location to display popup window
                            Gravity.CENTER, // Exact position of layout to display popup
                            0, // X offset
                            0 // Y offset
                        )
                    }
                })




            }
            poles.setOnClickListener {
                add_member.visibility = Button.INVISIBLE
                show_members.visibility = View.INVISIBLE
                poles.visibility = View.INVISIBLE
                val poleIntent = Intent(mCtx, GroupPolesActivity::class.java)
                val currentBundle = UBundle
                currentBundle.putString("GroupKey",currGroup.key)
                poleIntent.putExtras(currentBundle)
//                mCtx.startActivity(poleIntent)
                val a= mCtx as MyGroups
                a.startActivity(poleIntent)
                a.finish()

            }
            Toast.makeText(mCtx, "you clicked on: " + textViewGroupName.text, Toast.LENGTH_LONG).show()
//            add_member.visibility = Button.INVISIBLE
//            show_members.visibility = View.INVISIBLE
//            poles.visibility = View.INVISIBLE
        }

        return MyGroupView
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == AppCompatActivity.RESULT_OK) {
//            if (data != null) {
//                UBundle = data.extras!!
//            }
//        }
        print("hi")
    }
}

