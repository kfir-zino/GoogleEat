package com.example.googleeatkot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.googleeatkot.Common.Common
import com.example.googleeatkot.Model.PlaceDetail
import com.example.googleeatkot.remote.IGoogleAPIservice
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view_place.*
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ViewPlace : Fragment() {
    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    internal lateinit var mService: IGoogleAPIservice
    var mPlace: PlaceDetail? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_view_place, container, false)

        // Init Service
        mService = Common.googleApiService

        //Set empty for all tet view
        place_name.text = ""
        place_address.text = ""
        open_hours.text = ""
        btn_show_map.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
            startActivity(mapIntent)
        }

        //Load photo of place
        if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty())
            Picasso.with(this.requireContext())
                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!, 1000))
                .into(photo)

        //load rating
        if (Common.currentResult!!.rating != null)
            rating_bar.rating = Common.currentResult!!.rating.toFloat()
        else
            rating_bar.visibility = View.GONE

        //Load open hours
        if (Common.currentResult!!.opening_hours != null)
            open_hours.text = "@string/open_now" + Common.currentResult!!.opening_hours!!.open_now
        else
            open_hours.visibility = View.GONE

        //use service to fetch address and name
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
            .enqueue(object : retrofit2.Callback<PlaceDetail> {
                override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                    Toast.makeText(this@ViewPlace.requireContext(), ""+t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
                    mPlace = response!!.body()
                    place_address.text = mPlace!!.result!!.formatted_address
                    place_name.text = mPlace!!.result!!.name
                }

            })

        return rootView
    }

    private fun getPlaceDetailUrl(placeId: String): String {

        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?placeid=$placeId&key=AIzaSyBeupMTFuQuV2j_h1audd7aD8MmoZH9QZA")
        return url.toString()
    }

    private fun getPhotoOfPlace(photoReference: String, maxWidth: Int): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth")
        url.append("&photoreference=$photoReference")
        url.append("&key=AIzaSyBeupMTFuQuV2j_h1audd7aD8MmoZH9QZA")
        return url.toString()

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var rootView: View? = null
        fun newInstance() = ViewPlace()
    }
}
