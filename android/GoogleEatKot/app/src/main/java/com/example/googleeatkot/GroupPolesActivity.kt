package com.example.googleeatkot

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class GroupPolesActivity : AppCompatActivity() {
    lateinit var MyGroupPolesDBRef : DatabaseReference
    lateinit var myPlacesDBRef : DatabaseReference
    var currUser : FirebaseUser? = FirebaseAuth.getInstance()!!.currentUser
    lateinit var currGroupKey : String
    var OurResult : Int = 0
    lateinit var groupPolesList : MutableList<GroupPole>
    lateinit var placePoleVoters : MutableList<UserData>
    lateinit var activePoleView : View
    lateinit var activePole : GroupPole
    lateinit var oldPolesListView : ListView
    lateinit var vel : ValueEventListener
    lateinit var newPoleButton : Button
    lateinit var endPoleButton : Button
    lateinit var addPlaces2Pole : Button
    lateinit var userDataBundle : Bundle
    lateinit var currAppUser : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_poles)
        userDataBundle= intent.extras
        currAppUser = UserData(userDataBundle!!.getString("UserName"),userDataBundle!!.getString("UserEmail"),userDataBundle!!.getString("UserID"))
        if(currUser == null ){
            Log.w("PERMISSION_ERROR", "Unregistered user in Registered Only Activity")
            Toast.makeText(this, "Unregistered User not Allowed Here", Toast.LENGTH_LONG).show()
            this.finish()
        }
        groupPolesList = mutableListOf()
        placePoleVoters = mutableListOf()
        activePoleView = findViewById(R.id.pole1_ActivePole)
        oldPolesListView = findViewById(R.id.ListView_PastPoles)
        newPoleButton = findViewById(R.id.button_newPole)
        endPoleButton = findViewById(R.id.button_Deactivate_pole)
        addPlaces2Pole = findViewById(R.id.button_addPlace2Pole)
        currGroupKey = intent.getStringExtra("GroupKey") //retreiving the group from father activity
        MyGroupPolesDBRef = FirebaseDatabase.getInstance()!!.getReference("Groups").child(currGroupKey).child("Poles")
        myPlacesDBRef = FirebaseDatabase.getInstance()!!.getReference("Users").child(currAppUser.UserID).child("MyPlaces")
//        MyGroupPolesDBRef.addValueEventListener(object : ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onDataChange(groupPoles: DataSnapshot) {
//                //check if there is an active pole (or any poles at all)
//                if(!(groupPoles.exists()))//no poles to show
//                {
//                    //TODO - inflate layout of adding new pole
//                    Toast.makeText(this@GroupPolesActivity, "No Poles Made for This Group", Toast.LENGTH_LONG).show()
//                }
//                else{
//                    groupPolesList.clear()
//                    for(pole in groupPoles.children){
//                        var currentPole = pole.getValue(PoleData::class.java)
//                        if(!(currentPole!!.active)) {
//                            groupPolesList.add(GroupPole(currentPole!!, mutableListOf()))
//                            for (place in groupPoles.child(currentPole!!.poleKey).children) {
//                                //initialization of every poleplace (listing the voters, and adding to the list
//                                val foodPlace4pole = place.getValue(FoodPlace::class.java)
//                                placePoleVoters.clear()
//                                for (member in groupPoles.child(currentPole!!.poleKey).child("Voters").children) {
//                                    val voter = member.getValue(UserData::class.java)
//                                    placePoleVoters.add(voter!!)
//                                }
//                                groupPolesList.last().placesList.add(
//                                    PolePlace(
//                                        foodPlace4pole,
//                                        placePoleVoters,
//                                        MyGroupPolesDBRef.child(currentPole.poleKey!!)
//                                    )
//                                )
//                            }
//                        }
//                        else{
//                            //activePole - only one can be active
//                            activePole = GroupPole(currentPole!!, mutableListOf())
//                            for (place in groupPoles.child(currentPole!!.poleKey).children) {
//                                //initialization of every poleplace (listing the voters, and adding to the list
//                                val foodPlace4pole = place.getValue(FoodPlace::class.java)
//                                placePoleVoters.clear()
//                                for (member in groupPoles.child(currentPole!!.poleKey).child("Voters").children) {
//                                    val voter = member.getValue(UserData::class.java)
//                                    placePoleVoters.add(voter!!)
//                                }
//                                activePole.placesList.add(
//                                    PolePlace(
//                                        foodPlace4pole,
//                                        placePoleVoters,
//                                        MyGroupPolesDBRef.child(currentPole.poleKey!!)
//                                    )
//                                )
//                            }
//                        }
//                    }
//                    val oldPolesAdapter = ManyPolesAdapter(this@GroupPolesActivity,R.layout.poles_1pole,groupPolesList,currAppUser)
//                    oldPolesListView.adapter = oldPolesAdapter
//                    val activePoleAdapter = PolePlacesAdapter(this@GroupPolesActivity,R.id.one_place_of_pole,activePole.placesList,currAppUser,true)
//                    activePoleView.findViewById<ListView>(R.id.places_of_pole).adapter = activePoleAdapter
//                }
//
//            }
//
//        })


    }

    override fun onResume() {
        super.onResume()
        vel = MyGroupPolesDBRef.child("PolesList").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(groupPoles: DataSnapshot) {
                //check if there is an active pole (or any poles at all)
                if(!(groupPoles.exists()))//no poles to show
                {
                    endPoleButton.visibility = View.INVISIBLE
                    addPlaces2Pole.visibility = View.INVISIBLE
                    Toast.makeText(this@GroupPolesActivity, "No Poles Exist for This Group", Toast.LENGTH_LONG).show()
                }
                else{
                    groupPolesList.clear()
                    for(pole in groupPoles.children){
                        var currentPole = pole.child("PoleData").getValue(PoleData::class.java)
                        if(!(currentPole!!.active)) {
                            endPoleButton.visibility = View.INVISIBLE
                            addPlaces2Pole.visibility = View.INVISIBLE
                            groupPolesList.add(GroupPole(currentPole!!, mutableListOf()))
                            for (place in groupPoles.child(currentPole!!.poleKey).child("PolePlaces").children) {
                                //initialization of every poleplace (listing the voters, and adding to the list
                                val foodPlace4pole = place.getValue(FoodPlace::class.java)
                                placePoleVoters.clear()
                                for (member in groupPoles.child(currentPole!!.poleKey).child("PolePlaces").child(place.key!!).child("Voters").children) {
                                    val voter = member.getValue(UserData::class.java)
                                    placePoleVoters.add(voter!!)
                                }
                                groupPolesList.last().placesList.add(
                                    PolePlace(
                                        foodPlace4pole,
                                        placePoleVoters,
                                        MyGroupPolesDBRef.child("PolesList").child(currentPole.poleKey!!)
                                    )
                                )
                            }
                        }
                        else{
                            //activePole - only one can be active
                            activePole = GroupPole(currentPole!!, mutableListOf())
                            for (place in groupPoles.child(currentPole!!.poleKey).child("PolePlaces").children) {
                                //initialization of every poleplace (listing the voters, and adding to the list
                                val foodPlace4pole = place.getValue(FoodPlace::class.java)
                                placePoleVoters.clear()
                                for (member in groupPoles.child(currentPole!!.poleKey).child("PolePlaces").child(place.key!!).child("Voters").children) {
                                    val voter = member.getValue(UserData::class.java)
                                    placePoleVoters.add(voter!!)
                                }
                                activePole.placesList.add(
                                    PolePlace(
                                        foodPlace4pole,
                                        placePoleVoters,
                                        MyGroupPolesDBRef.child("PolesList").child(currentPole.poleKey!!).child("PolePlaces").child(foodPlace4pole!!.key!!)
                                    )
                                )
                            }
                        }
                    }
                    val oldPolesAdapter = ManyPolesAdapter(this@GroupPolesActivity,R.layout.poles_1pole,groupPolesList,currAppUser)
                    oldPolesListView.adapter = oldPolesAdapter
                    val activePoleAdapter = PolePlacesAdapter(this@GroupPolesActivity,R.layout.poles_1pole_1place,activePole.placesList,currAppUser,true)
                    activePoleView.findViewById<TextView>(R.id.textView_poleName).text = activePole.poleData!!.poleName
                    activePoleView.findViewById<ListView>(R.id.places_of_pole).adapter = activePoleAdapter
                }

            }

        })
        newPoleButton.setOnClickListener(){
            val activePolesDBRefrence = FirebaseDatabase.getInstance()!!.getReference("Groups").child(currGroupKey).child("Poles").child("ActivePole")
            activePolesDBRefrence.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(activePole: DataSnapshot) {
                    if(activePole.exists()){//there is an active pole
                        Toast.makeText(this@GroupPolesActivity, "There is an Active pole Running", Toast.LENGTH_LONG).show()
                    }
                    else{
                        showNewPoleDialog()
                    }
                }
            })
        }
        endPoleButton.setOnClickListener(){
            showEndPoleDialog()
        }
        addPlaces2Pole.setOnClickListener(){
            showAddPlacesDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        MyGroupPolesDBRef.removeEventListener(vel)
    }

    fun showNewPoleDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Create New Pole")
        val inflater = LayoutInflater.from(this)
        val NewPoleDialogView = inflater.inflate(R.layout.new_pole_dialog,null)
        val poleNameText = NewPoleDialogView.findViewById<EditText>(R.id.EditText_newPoleName)
        builder.setView(NewPoleDialogView)

        builder.setPositiveButton("Create Pole"){ p0, p1 ->
            val newPoleName = poleNameText.text.toString().trim()
            val polesDBRefrence = FirebaseDatabase.getInstance()!!.getReference("Groups").child(currGroupKey).child("Poles")
            if(newPoleName.isEmpty()){
                poleNameText.error = "Please Enter a Name"
                poleNameText.requestFocus()
                return@setPositiveButton
            }
            val newPoleKey = polesDBRefrence.push().key
            val NewPole = PoleData(newPoleName,newPoleKey!!,true)
            polesDBRefrence.child("PolesList").child(newPoleKey).child("PoleData").setValue(NewPole)
            polesDBRefrence.child("ActivePole").setValue(newPoleKey)
            Toast.makeText(this@GroupPolesActivity, "Pole Added :)", Toast.LENGTH_LONG).show()
            endPoleButton.visibility = View.VISIBLE
            addPlaces2Pole.visibility = View.VISIBLE
        }

        builder.setNegativeButton("Cancel"){ p0, p1 ->
            Toast.makeText(this@GroupPolesActivity, "New Pole Canceled :(", Toast.LENGTH_LONG).show()
        }
        val alert = builder.create()
        alert.show()

    }
    fun showEndPoleDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("End Current Pole")
        builder.setMessage("Are you sure you want to end the pole?")
        builder.setPositiveButton("Yes"){p0, p1 ->
            val polesDBRefrence = FirebaseDatabase.getInstance()!!.getReference("Groups").child(currGroupKey).child("Poles")
            val activeVel = polesDBRefrence.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(polesList: DataSnapshot) {
                    for(pole in polesList.children){
                        val currPole = pole.getValue(PoleData::class.java)!!
                        if(currPole.active){
                            val updatePole = PoleData(currPole.poleName,currPole.poleKey,false)
                            polesDBRefrence.child(currPole.poleKey).setValue(updatePole)
                            polesDBRefrence.parent!!.child("ActivePole").removeValue()
                            endPoleButton.visibility = View.INVISIBLE
                            addPlaces2Pole.visibility = View.INVISIBLE
                        }
                    }
                }
            })
        }
        builder.setNegativeButton("No"){p0, p1 ->
        }
        val alert = builder.create()
        alert.show()
    }
    fun showAddPlacesDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Places to Pole")
        val inflater = LayoutInflater.from(this)
        val addPlacesDialogView = inflater.inflate(R.layout.add_places_dialog,null)
        val placesListView = addPlacesDialogView.findViewById<ListView>(R.id.addPlacesListView)
        var placesListAdapter : MyPlaceAdapaterClass
        //read all places of user, and save them into a list of <foodPlaces>
        var userFoodPlacesList = mutableListOf<FoodPlace>()
        val currUserMyPlacesDBRef = FirebaseDatabase.getInstance()!!.getReference("Users").child(currAppUser.UserID).child("MyPlaces")
        currUserMyPlacesDBRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(currUserPlaces: DataSnapshot) {
                userFoodPlacesList.clear()
                if(!(currUserPlaces.exists())){ //User has no places to show
                    Toast.makeText(this@GroupPolesActivity, "You have no places...", Toast.LENGTH_LONG).show()
                }
                var currPlace : FoodPlace?
                for(place in currUserPlaces.children){
                    currPlace = place.getValue(FoodPlace::class.java)
                    userFoodPlacesList.add(currPlace!!)
                }
                val placesAdapter = PolePlaceAdapter(this@GroupPolesActivity,R.layout.add_places_1place, userFoodPlacesList)
                placesListView.adapter = placesAdapter
                builder.setView(addPlacesDialogView)
                builder.setPositiveButton("Add Selected Places") { p0, p1 ->
                    var places2Add = mutableListOf<FoodPlace>()
                    val currGroupPolesDBRef =
                        FirebaseDatabase.getInstance()!!.getReference("Groups").child(currGroupKey).child("Poles")
                    for (i in 0 until userFoodPlacesList.size) {
                        if (placesListView.get(i).findViewById<CheckBox>(R.id.checkBox).isChecked) {
                            places2Add.add(userFoodPlacesList[i])
                        }
                    }
                    currGroupPolesDBRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(currGroupPolesDB: DataSnapshot) {
                            val currPoleKey = currGroupPolesDB.child("ActivePole").value.toString()
                            for (place in places2Add) {
                                //check if the place doesn't already belong to the pole places - by googleID
                                //add place to .child("Poles").child(Polekey).child("PolePlaces").child(placeKey).child("FoodPlaceData")
                                var deletedFlag = false
                                for(polePlace in currGroupPolesDB.child("PolesList").child(currPoleKey).child("PolePlaces").children){
                                    if(place.placeID == polePlace.getValue(FoodPlace::class.java)!!.placeID){
                                        places2Add.remove(place)
                                        deletedFlag = true
                                        break
                                    }
                                }
                                if(!deletedFlag){
                                    currGroupPolesDBRef.child("PolesList").child(currPoleKey).child("PolePlaces").child(place.key!!).setValue(place)
                                }
                            }

                        }
                    })
                }
                builder.setNegativeButton("Cancel"){ p0, p1 ->
                    Toast.makeText(this@GroupPolesActivity, "Adding Places Canceled :(", Toast.LENGTH_LONG).show()
                }
                val alert = builder.create()
                alert.show()
            }
        })
    }
}