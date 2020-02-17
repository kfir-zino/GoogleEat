package com.example.googleeatkot

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.googleeatkot.Common.Common
import com.example.googleeatkot.ui.login.LoginActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.MapView
import android.graphics.BitmapFactory
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var signInButton: Button
    lateinit var signOutButton: Button
    lateinit var map: MapFragment
    lateinit var currentUser: FirebaseUser
    lateinit var gso: GoogleSignInOptions
    lateinit var loginIntent: Intent
    lateinit var miniMap: MapView
    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // signOutButton.visibility = View.INVISIBLE
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setContentView(R.layout.activity_main)

        signInButton = findViewById<Button>(R.id.signInButton) as Button
        signOutButton = findViewById<Button>(R.id.signOutButton) as Button

//        map = findViewById<MapView>(R.id.mapView) as MapView

        signInButton.visibility = View.VISIBLE
        // this.onCreateView(R.layout.activity_main, MapsActivity::class.java.newInstance(), "map")


        signOutButton.visibility = View.INVISIBLE

        if (savedInstanceState == null) {
           supportFragmentManager.beginTransaction().add(R.id.map_fragment, MapFragment.newInstance(), "mainMap").commit()
           miniMap = findViewById(R.id.map_fragment)
        }
        // map.visibility = View.VISIBLE

        scrollPlaces()


    }

    fun scrollPlaces() {
        val placesHorizontalScrollView = HorizontalScrollView(this)
        //setting height and width
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        placesHorizontalScrollView.layoutParams = layoutParams


        val linearLayout = LinearLayout(this)
        //setting height and width
        val linearParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.layoutParams = linearParams

        //adding horizontal scroll view to the layout
        placesHorizontalScrollView.addView(linearLayout)

        val currPlaces = Common.placesResults
        if (currPlaces != null) {
            var listSize = Common.placesResults!!.size
            if (listSize > 1)
                listSize = 0
            for (i in 0 until listSize) {
                val placeImage = ImageView(this)
                //setting height and width
                val params1 = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                placeImage.layoutParams = params1
                // accessing images that we downloaded and copied to
                // drawable folder and setting it to imageview
//                var mURL =
//                    "https://maps.googleapis.com/maps/api/place/photo" + "?key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4&maxwidth=400"
//                var shitFace =
//                    mURL + "&photoreference=" + currPlaces[i].photos!![0].photo_reference!!
//                val url = URL(shitFace)
//                var bmp: Bitmap? = null
//                try {
//                    val check_exe = AssignPhoto(this@MainActivity.baseContext).execute(currPlaces[i].photos!![0].photo_reference!!)
//                    bmp = check_exe.get()
//                } catch (e : Exception) {
//                    Toast.makeText(this@MainActivity.baseContext, e.message, Toast.LENGTH_SHORT).show()
//                }
//                placeImage.setImageBitmap(bmp)


//                placeImage.setImageResource(
//                    Picasso.with(this)
//                    .load(shitFace)
//                    .into(mInfoWindow.photo))
//                Toast.makeText(this@MainActivity.baseContext, currPlaces[i].name, Toast.LENGTH_SHORT).show()
                linearLayout.addView(placeImage)
            }
        }

        val placeScrollLinearLayout = findViewById<RelativeLayout>(R.id.scroll_places)
        placeScrollLinearLayout?.addView(placesHorizontalScrollView)
    }

    override fun dispatchTouchEvent(ev : MotionEvent) : Boolean {
        /**
         * Request all parents to relinquish the touch events
         */
        miniMap!!.parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume(){
        signInButton = findViewById<Button>(R.id.signInButton) as Button
        signOutButton = findViewById<Button>(R.id.signOutButton) as Button
        miniMap = findViewById(R.id.map_fragment)!!
        scrollPlaces()

        // map = findViewById<MapView>(R.id.mapView) as MapView
        // this.onCreateView()
        super.onResume()
    }
    override fun onStart() {
        super.onStart()
        signInButton.setOnClickListener{
            this.signIn()
        }
        signOutButton.setOnClickListener{
            this.signOut()
        }
        scrollPlaces()

    }


    fun signIn() {
        loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, RC_SIGN_IN)
//        signInButton.visibility = View.INVISIBLE
//        signOutButton.visibility = View.VISIBLE
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        signInButton.visibility = View.VISIBLE
        signOutButton.visibility = View.INVISIBLE
        GoogleSignIn.getClient(this, gso).signOut()
//        GoogleSignIn.getSignedInAccountFromIntent(loginIntent).getResult()
//        GoogleSignIn.getLastSignedInAccount(this)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            this.signInButton.visibility = View.INVISIBLE
            this.signOutButton.visibility = View.VISIBLE
            if (data != null) {
                if (data.hasExtra("FBuser")) {
                    currentUser = data.extras.getParcelable("FBuser")
                    gso =  data.extras.getParcelable("gso")

                }
            }
        }
    }

    private fun updateUI(account: FirebaseUser?) {
//        val dispTxt = findViewById<View>(R.id.dispTxt) as TextView
//        dispTxt.text = account!!.displayName
    }
}