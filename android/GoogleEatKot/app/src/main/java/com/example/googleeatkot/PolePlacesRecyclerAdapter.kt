package com.example.googleeatkot

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PolePlacesRecyclerAdapter (val mCtx : Context, val PolesPlacesList : List<PolePlace>,
                                 val currAppUser : UserData, val poleActive : Boolean = false)
    : RecyclerView.Adapter<PolePlaceViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolePlaceViewHolder {
        return PolePlaceViewHolder(LayoutInflater.from(mCtx),parent,currAppUser,poleActive)
    }

    override fun getItemCount(): Int {
        return PolesPlacesList.size
    }

    override fun onBindViewHolder(holder: PolePlaceViewHolder, position: Int) {
        val currPlace : PolePlace = PolesPlacesList[position]
        holder.bind(currPlace)
    }
}



class PolePlaceViewHolder(inflater: LayoutInflater, parent: ViewGroup, val currAppUser : UserData, val poleActive : Boolean = false)
    :RecyclerView.ViewHolder(inflater.inflate(R.layout.poles_1pole_1place,parent,false)){
    private var textViewPlaceName : TextView? = null
    private var numOfVotes : TextView? = null
    private var vote : Button? = null

    init{
        textViewPlaceName = itemView.findViewById(R.id.textView_polePlaceName)
        numOfVotes = itemView.findViewById(R.id.textView_num_of_votes)
        vote = itemView.findViewById(R.id.button_vote)
    }

    fun bind(polePlace: PolePlace){
        textViewPlaceName?.text = polePlace.foodPlace!!.name
        numOfVotes?.text = polePlace.votersList!!.size.toString()
        if(poleActive) {
            for  (voter in polePlace.votersList){
                if (currAppUser.UserID in voter.UserID) {//this user has already voted for this place
                    vote?.text = "Voted"
                    vote?.isEnabled = false
                }
            }
            vote!!.setOnClickListener {
                textViewPlaceName?.text = polePlace.foodPlace!!.name
                //save new voter as current user
                polePlace.polePlaceDBRef!!.child("Voters").child(currAppUser.UserID)
                    .setValue(currAppUser)
                vote?.text = "Voted"
                vote?.isEnabled = false
                numOfVotes?.text = polePlace.votersList!!.size.toString()
            }
        }
        else {
            vote?.isEnabled = false
            vote?.text = "No"
            vote?.setBackgroundColor(Color.RED)
            for (voter in polePlace.votersList) {
                if (currAppUser.UserID == voter.UserID) {
                    vote?.text = "Yes"
                    vote?.setBackgroundColor(Color.GREEN)
                }
            }
        }
    }
}