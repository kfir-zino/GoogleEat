package com.example.googleeatkot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class PolePlaceAdapter(val mCtx : Context, val LayoutResId : Int, val MyPlacesList : List<FoodPlace>)
    :ArrayAdapter<FoodPlace>(mCtx, LayoutResId, MyPlacesList){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val MyPlaceView : View = layoutInflater.inflate(LayoutResId,null)
        val textViewPlaceName = MyPlaceView.findViewById<CheckBox>(R.id.checkBox)
        val FoodPlace = MyPlacesList[position]
        textViewPlaceName.text = FoodPlace.name
        return MyPlaceView
    }
}