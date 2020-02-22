package com.example.googleeatkot

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MyPlacesActivity : AppCompatActivity() {

    lateinit var MyPlacesDBRef : DatabaseReference
    var currUser : FirebaseUser? = FirebaseAuth.getInstance()!!.currentUser
    var OurResult : Int = 0
    lateinit var MyPlacesList : MutableList<FoodPlace>
    lateinit var PlacesListView : ListView


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.my_places_list)
        if(currUser == null ){
            Log.w("PERMISSION_ERROR", "Unregistered user in Registered Only Activity")
            Toast.makeText(this, "Unregistered User not Allowed Here", Toast.LENGTH_LONG).show()
            this.finish()
        }
        PlacesListView = findViewById(R.id.PlacesListView)
        MyPlacesList = mutableListOf()
        MyPlacesDBRef = FirebaseDatabase.getInstance().getReference(currUser!!.uid).child("MyPlaces")
        MyPlacesDBRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(PlacesListSnapshot: DataSnapshot) {
                if(PlacesListSnapshot!!.exists()){ //making sure we have places to show
                    MyPlacesList.clear()
                    for(place in PlacesListSnapshot.children){ //going through all places in MyPlaces
                        val ListedPlace = place.getValue(FoodPlace::class.java)
                        MyPlacesList.add(ListedPlace!!)
                    }

                    val adapter = MyPlaceAdapaterClass(this@MyPlacesActivity,R.layout.my_places_1place,MyPlacesList)
                    PlacesListView.adapter = adapter
                }
                else Toast.makeText(this@MyPlacesActivity, "No Places to Show", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun finish() {
        if(OurResult== 0) setResult(Activity.RESULT_OK)
        else setResult(Activity.RESULT_CANCELED)
        super.finish()
    }
}