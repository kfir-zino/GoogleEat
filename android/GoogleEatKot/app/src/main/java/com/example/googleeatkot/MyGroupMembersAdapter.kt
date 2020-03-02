package com.example.googleeatkot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyGroupMembersAdapter(val mCtx : Context, val LayoutResId : Int, val MyMembersList : List<UserData>)
    :ArrayAdapter<UserData>(mCtx, LayoutResId, MyMembersList){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val MyMemberView : View = layoutInflater.inflate(LayoutResId,null)
        val textViewMemberName = MyMemberView.findViewById<TextView>(R.id.one_member_text)
        val Member = MyMembersList[position]
        textViewMemberName.text = Member.UserName
        return MyMemberView
    }
}