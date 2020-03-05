package com.example.googleeatkot

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class PastPolesRecyclerAdapter(val mCtx : Context, val MyPolesList : List<GroupPole>,
                               val currAppUser : UserData)
    : RecyclerView.Adapter<PoleViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoleViewHolder {
        return PoleViewHolder(LayoutInflater.from(mCtx),parent,mCtx,currAppUser)
    }

    override fun getItemCount(): Int {
        return MyPolesList.size
    }

    override fun onBindViewHolder(holder: PoleViewHolder, position: Int) {
        val mGroupPole = MyPolesList[position]
        holder.bind(mGroupPole)
    }
}

class PoleViewHolder(inflater: LayoutInflater, parent: ViewGroup,val mCtx : Context,val currAppUser : UserData)
    :RecyclerView.ViewHolder(inflater.inflate(R.layout.poles_1pole,parent,false)){
    private var textViewPoleName : TextView? = null
    private var OnePole : androidx.recyclerview.widget.RecyclerView? = null

    init{
        textViewPoleName = itemView.findViewById(R.id.textView_poleName)
        OnePole = itemView.findViewById(R.id.places_of_pole)
    }

    fun bind(mGroupPole : GroupPole?){
        textViewPoleName?.text = mGroupPole?.poleData!!.poleName
        OnePole?.apply {
            layoutManager = LinearLayoutManager(mCtx)
            adapter = PolePlacesRecyclerAdapter(mCtx,mGroupPole.placesList,currAppUser,mGroupPole!!.poleData!!.active)
        }

    }


}