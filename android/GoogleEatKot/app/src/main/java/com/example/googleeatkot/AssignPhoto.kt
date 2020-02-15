//package com.example.googleeatkot
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.AsyncTask
//import android.widget.Toast
//import java.net.URL
//
//class AssignPhoto(context: Context) : AsyncTask<String, Void, Bitmap>() {
//    val mURL="https://maps.googleapis.com/maps/api/place/photo"+"?key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4&maxwidth=400"
//    var exception: java.lang.Exception? = null
//    val mContext: Context = context
//
//    override fun doInBackground(vararg params: String?): Bitmap? {
//        try {
//            val shitFace = mURL + "&photoreference=" + params[0]
//            val url = URL(shitFace)
//            var bmp: Bitmap? = null
//            try {
//                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//            } catch (e : Exception) {
////                Toast.makeText(this@, e.message, Toast.LENGTH_SHORT).show()
//                Toast.makeText(this.mContext, "the error is: " + e.message, Toast.LENGTH_SHORT).show()
//            }
////            val mInfoWindow: ImageView = params[0] as ImageView
////            mInfoWindow.photo.setImageBitmap(bmp)
//
//            return bmp
//        } catch (e: Exception) {
//            Toast.makeText(this.mContext, "the error is: " + e.message, Toast.LENGTH_SHORT).show()
//
//            return null
//        } finally {
//
//        }
//    }
//
//    override fun onPostExecute(result: Bitmap?) {
//        super.onPostExecute(result)
//        if(this.exception != null) {
////            print("the error is: " + this.exception!!.message)
//            Toast.makeText(this.mContext, "the error is: " + this.exception!!.message, Toast.LENGTH_SHORT).show()
//            throw this.exception!!
//        }
//    }
//
//
////    if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.isNotEmpty())
////        shitFace = mURL + "&photoreference=" + Common.currentResult!!.photos!![0].photo_reference!!
////        try {
////            val lets_see = Uri.parse(shitFace)
////        } catch (e: Exception) {
////            Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
////        }
////        try {
////            val url = URL(shitFace)
////            var bmp: Bitmap? = null
////            try {
////                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
////            } catch (e : Exception) {
////                Toast.makeText(this@ViewPlace.mContext, e.message, Toast.LENGTH_SHORT).show()
////            }
////            mInfoWindow.photo.setImageBitmap(bmp)
////        } catch (e: Exception) {
////            Toast.makeText(this@ViewPlace.mContext, "exception in photo url", Toast.LENGTH_SHORT).show()
////        }
//
//}
