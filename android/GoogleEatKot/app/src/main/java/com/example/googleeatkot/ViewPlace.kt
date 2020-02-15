package com.example.googleeatkot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.googleeatkot.Common.Common
import com.example.googleeatkot.Model.PlaceDetail
import com.example.googleeatkot.remote.IGoogleAPIservice
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view_place.*
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_view_place.view.*
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

//class ViewPlace : Fragment() {
class ViewPlace(context: Context) : GoogleMap.InfoWindowAdapter {
    private var mInfoWindow : View
    private var mContext: Context
    var mPlace: PlaceDetail? = null
    var mURL: String? = null
    lateinit var shitFace: String
    init {
        mContext = context
        mInfoWindow = LayoutInflater.from(mContext).inflate(R.layout.fragment_view_place,null)
        mPlace?.result = Common.currentResult
        mURL="https://maps.googleapis.com/maps/api/place/photo"+"?key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4&maxwidth=400"
    }
    private fun assignWindowText(){
        //Set empty for all tet view
        mInfoWindow.setBackgroundColor(Color.WHITE)
        mInfoWindow.place_name.text = Common.currentResult!!.name
        mInfoWindow.place_address.text = Common.currentResult!!.vicinity
        //Load open hours
        if (Common.currentResult!!.opening_hours != null)
            if (Common.currentResult!!.opening_hours!!.open_now)
                mInfoWindow.open_hours.text = "Open"
            else
                mInfoWindow.open_hours.text = "Closed"
//            mInfoWindow.open_hours.text = "@string/open_now" + Common.currentResult!!.opening_hours!!.open_now
        else
            mInfoWindow.open_hours.visibility = View.GONE
    }
    private fun assignPhoto(){
        //Load photo of place
        var bmp: Bitmap? = null
            if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty())
//            AssignPhoto().execute(Common.currentResult!!.photos!![0].photo_reference!!)
//            shitFace = mURL + "&photoreference=" + Common.currentResult!!.photos!![0].photo_reference!!
//            try {
//                val lets_see = Uri.parse(shitFace)
//            } catch (e: Exception) {
//                Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
//            }
            try {
//                val url = URL(shitFace)
                try {
                    val check_exe = AssignPhoto(this@ViewPlace.mContext).execute(Common.currentResult!!.photos!![0].photo_reference!!)
                    bmp = check_exe.get()
//                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e : Exception) {
                    Toast.makeText(this@ViewPlace.mContext, e.message, Toast.LENGTH_SHORT).show()
                }
                mInfoWindow.photo.setImageBitmap(bmp)

//                Picasso.with(mContext)
//                    //                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!, 1000))
//                    .load(shitFace)
//                    //                .fit().centerCrop()
//                    //            .placeholder(R.drawable.places_ic_search)
//                    //            .error(R.drawable.ic_restaurant_png)
//                    .into(mInfoWindow.photo)
            } catch (e: Exception) {
                Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
            }

    }
    private fun assignRating(){
        //load rating
        if (Common.currentResult!!.rating != null)
            mInfoWindow.rating_bar.rating = Common.currentResult!!.rating.toFloat()
        else
            mInfoWindow.rating_bar.visibility = View.GONE
    }


    override fun getInfoContents(p0: Marker?): View {
        assignWindowText()
        assignPhoto()
        assignRating()
        return mInfoWindow
    }

    override fun getInfoWindow(p0: Marker?): View {
        assignWindowText()
        assignPhoto()
        assignRating()
        return mInfoWindow
    }
//
//    // TODO: Rename and change types of parameters
//    private var listener: OnFragmentInteractionListener? = null
//    internal lateinit var mService: IGoogleAPIservice


//    companion object {
//        @SuppressLint("StaticFieldLeak")
//        var rootView: View? = null
//      //  fun newInstance() = ViewPlace()
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        // Inflate the layout for this fragment
//
//        rootView = inflater.inflate(R.layout.fragment_view_place, container, false)
//
//        // Init Service
//        mService = Common.googleApiService
//        mPlace?.result = Common.currentResult
//
//        btn_show_map.setOnClickListener {
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
//            startActivity(mapIntent)
//        }
//
//
//
//
//
//
//        //use service to fetch address and name
//        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
//            .enqueue(object : retrofit2.Callback<PlaceDetail> {
//                override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
//                    Toast.makeText(this@ViewPlace.requireContext(), ""+t.message, Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
//                    mPlace = response!!.body()
//                    place_address.text = mPlace!!.result!!.formatted_address
//                    place_name.text = mPlace!!.result!!.name
//                }
//
//            })
//
//        return rootView
//    }
//
//    private fun getPlaceDetailUrl(placeId: String): String {
//
//        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?placeid=$placeId&key=AIzaSyBeupMTFuQuV2j_h1audd7aD8MmoZH9QZA")
//        return url.toString()
//    }
//
//    private fun getPhotoOfPlace(photoReference: String, maxWidth: Int): String {
//        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth")
//        url.append("&photoreference=$photoReference")
//        url.append("&key=AIzaSyBeupMTFuQuV2j_h1audd7aD8MmoZH9QZA")
//        return url.toString()
//
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     *
//     *
//     * See the Android Training lesson [Communicating with Other Fragments]
//     * (http://developer.android.com/training/basics/fragments/communicating.html)
//     * for more information.
//     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }


}

class AssignPhoto(context: Context) : AsyncTask<String, Void, Bitmap>() {
    val mURL="https://maps.googleapis.com/maps/api/place/photo"+"?key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4&maxwidth=400"
    var exception: java.lang.Exception? = null
    val mContext: Context = context

    override fun doInBackground(vararg params: String?): Bitmap? {
        try {
            val shitFace = mURL + "&photoreference=" + params[0]
            val url = URL(shitFace)
            var bmp: Bitmap? = null
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e : Exception) {
//                Toast.makeText(this@, e.message, Toast.LENGTH_SHORT).show()
//                Toast.makeText(this.mContext, "the error is: " + e.message, Toast.LENGTH_SHORT).show()
                return null
            }
//            val mInfoWindow: ImageView = params[0] as ImageView
//            mInfoWindow.photo.setImageBitmap(bmp)

            return bmp
        } catch (e: Exception) {
//            Toast.makeText(this.mContext, "the error is: " + e.message, Toast.LENGTH_SHORT).show()

            return null
        } finally {

        }
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if(this.exception != null) {
//            print("the error is: " + this.exception!!.message)
//            Toast.makeText(this.mContext, "the error is: " + this.exception!!.message, Toast.LENGTH_SHORT).show()
            throw this.exception!!
        }
    }


//    if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty())
//        shitFace = mURL + "&photoreference=" + Common.currentResult!!.photos!![0].photo_reference!!
//        try {
//            val lets_see = Uri.parse(shitFace)
//        } catch (e: Exception) {
//            Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
//        }
//        try {
//            val url = URL(shitFace)
//            var bmp: Bitmap? = null
//            try {
//                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//            } catch (e : Exception) {
//                Toast.makeText(this@ViewPlace.mContext, e.message, Toast.LENGTH_SHORT).show()
//            }
//            mInfoWindow.photo.setImageBitmap(bmp)
//        } catch (e: Exception) {
//            Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
//        }

}
