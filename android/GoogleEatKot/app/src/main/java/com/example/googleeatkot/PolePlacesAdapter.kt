package com.example.googleeatkot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PolePlacesAdapter (val mCtx : Context, val LayoutResId : Int, val PolesPlacesList : List<PolePlace>,
                         val currAppUser : UserData, val poleActive : Boolean = false)
    : ArrayAdapter<PolePlace>(mCtx, LayoutResId, PolesPlacesList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val polePlaceView: View = layoutInflater.inflate(LayoutResId, null)
        val textViewPoleName = polePlaceView.findViewById<TextView>(R.id.textView_polePlaceName)
        val numOfVotes = polePlaceView.findViewById<TextView>(R.id.textView_num_of_votes)
        val vote = polePlaceView.findViewById<Button>(R.id.button_vote)
        val currPlace = PolesPlacesList[position]
        textViewPoleName.text = currPlace.foodPlace!!.name
        numOfVotes.text = currPlace.votersList!!.size.toString()
        if(poleActive) {
            for  (voter in currPlace.votersList){
                if (currAppUser.UserID in voter.UserID) {//this user has already voted for this place
                    vote.text = "Voted"
                    vote.isEnabled = false
                }
            }
            vote.setOnClickListener {
                textViewPoleName.text = currPlace.foodPlace!!.name
                //save new voter as current user
                currPlace.polePlaceDBRef!!.child("Voters").child(currAppUser.UserID)
                    .setValue(currAppUser)
                vote.text = "Voted"
                vote.isEnabled = false
                numOfVotes.text = currPlace.votersList!!.size.toString()
            }
        }
        else {
            vote.isEnabled = false
            if (currAppUser in currPlace.votersList){
                vote.text = "Yes"
                vote.setBackgroundColor(Color.GREEN)
            }
            else{
                vote.text = "No"
                vote.setBackgroundColor(Color.RED)
            }
        }
        return polePlaceView
    }
}