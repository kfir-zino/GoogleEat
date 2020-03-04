package com.example.googleeatkot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class ManyPolesAdapter (val mCtx : Context, val LayoutResId : Int, val MyPolesList : List<GroupPole>,
                        val currAppUser : UserData)
: ArrayAdapter<GroupPole>(mCtx, LayoutResId, MyPolesList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this.mCtx)
        val PoleView : View = layoutInflater.inflate(LayoutResId,null)
        val adapter = PolePlacesAdapter(mCtx,R.layout.poles_1pole_1place,MyPolesList[position].placesList,currAppUser,MyPolesList[position].poleData!!.active )
        PoleView.findViewById<ListView>(R.id.places_of_pole).adapter = adapter
        val currPole = MyPolesList[position]
    return PoleView
    }
}
