package com.example.googleeatkot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyGroupsAdapterClass (val mCtx : Context, val LayoutResId : Int, val MyGroupsList : List<Group>)
: ArrayAdapter<Group>(mCtx, LayoutResId, MyGroupsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val MyGroupView: View = layoutInflater.inflate(LayoutResId, null)
        val textViewGroupName = MyGroupView.findViewById<TextView>(R.id.textViewGroupName)
        val currGroup = MyGroupsList[position]
        textViewGroupName.text = currGroup.groupData!!.name
        return MyGroupView
    }
}