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
import com.google.android.gms.maps.MapView
import android.graphics.BitmapFactory
import java.net.URL
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var mAuth: FirebaseAuth? = null
    lateinit var signInButton: Button
    lateinit var signOutButton: Button
    lateinit var imagesCheckBox: CheckBox
    var currentUser: FirebaseUser? = null
    var gso: GoogleSignInOptions = GoogleSignInOptions.DEFAULT_SIGN_IN
    lateinit var loginIntent: Intent
    lateinit var miniMap: MapView
    val RC_SIGN_IN: Int = 1
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var placesIntent : Intent
    lateinit var groupsIntent : Intent
    var UBundle: Bundle = Bundle()
    lateinit var currUserDataDBRef : DatabaseReference
    var showImages : Boolean = false
    lateinit var velUsers : ValueEventListener
    lateinit var currUserData : UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        // signOutButton.visibility = View.INVISIBLE
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)

        signInButton = findViewById<Button>(R.id.signInButton) as Button
        signOutButton = findViewById<Button>(R.id.signOutButton) as Button
        imagesCheckBox = findViewById<CheckBox>(R.id.show_image_box) as CheckBox
        if (imagesCheckBox.isChecked) {
            showImages = true
        }

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

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_places -> {
                placesIntent = Intent(this, MyPlacesActivity::class.java)
                placesIntent.putExtras(UBundle)
                startActivityForResult(placesIntent, RC_SIGN_IN)
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_groups -> {
                groupsIntent = Intent(this, MyGroups::class.java)
                groupsIntent.putExtras(UBundle)
                startActivityForResult(groupsIntent, RC_SIGN_IN)
                Toast.makeText(this, "Groups clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_friends -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                if (currentUser == null){
                    Toast.makeText(this, "You are not signed in.", Toast.LENGTH_SHORT).show()
                } else {
//                    this.signIn()
                    this.signOut()
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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

        if (currPlaces.isNotEmpty()) {
            var listSize = Common.placesResults!!.size
            if (listSize > 1)
                listSize = 20
            val keys = currPlaces.keys.take(listSize)
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
                var mURL =
                    "https://maps.googleapis.com/maps/api/place/photo" + "?key=AIzaSyASmgAWrMWkLhB26W9iZYjX-Vvtq0xJ0X4&maxwidth=400"
                var shitFace =
                    mURL + "&photoreference=" + currPlaces[keys[i]]!!.photos!![0].photo_reference!!
                val url = URL(shitFace)
                var bmp: Bitmap? = null
                if (showImages) {
                    try {
                        val check_exe = AssignPhoto(this@MainActivity.baseContext).execute(currPlaces[keys[i]]!!.photos!![0].photo_reference!!)
                        bmp = check_exe.get()
                    } catch (e : Exception) {
                        Toast.makeText(this@MainActivity.baseContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                    placeImage.setImageBitmap(bmp)
                } else {
                    placeImage.setImageResource(R.drawable.ic_restaurant_png)
                }




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
        imagesCheckBox = findViewById<CheckBox>(R.id.show_image_box) as CheckBox
        imagesCheckBox.setOnClickListener{
            showImages = imagesCheckBox.isChecked
        }
        currentUser = mAuth!!.currentUser
        updateUI(currentUser)
        scrollPlaces()

        // map = findViewById<MapView>(R.id.mapView) as MapView
        // this.onCreateView()

        // Check if user is signed in (non-null) and update UI accordingly.
        signInButton.setOnClickListener{
            this.signIn()
        }
        signOutButton.setOnClickListener{
//            this.signIn()
            this.signOut()
        }
        imagesCheckBox = findViewById<CheckBox>(R.id.show_image_box) as CheckBox
        imagesCheckBox.setOnClickListener{
            showImages = imagesCheckBox.isChecked
        }
        super.onResume()
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        signInButton.setOnClickListener{
            this.signIn()
        }
        signOutButton.setOnClickListener{
//            this.signIn()
            this.signOut()
        }
        imagesCheckBox = findViewById<CheckBox>(R.id.show_image_box) as CheckBox
        imagesCheckBox.setOnClickListener{
            showImages = imagesCheckBox.isChecked
        }
       // scrollPlaces()

    }


    fun signIn() {
        loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, RC_SIGN_IN)
//        signInButton.visibility = View.INVISIBLE
//        signOutButton.visibility = View.VISIBLE
    }

    fun signOut() {
        val acct: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.baseContext)!!
        if (acct != null) {

            FirebaseAuth.getInstance().signOut()
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.INVISIBLE
            if (gso == null) {
                var opt = acct.account!!
            } else {
                GoogleSignIn.getClient(this, gso!!).signOut()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            this.signInButton.visibility = View.INVISIBLE
            this.signOutButton.visibility = View.VISIBLE
            if (data != null) {
                if (data.hasExtra("FBuser")) {
                    currentUser = data.extras.getParcelable("FBuser")
                    gso =  data.extras.getParcelable("gso")!!
                }
            }
        }
    }

    private fun updateUI(account: FirebaseUser?) {
//        val dispTxt = findViewById<View>(R.id.dispTxt) as TextView
//        dispTxt.text = account!!.displayName
        if(account == null) {
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.INVISIBLE
        }
        else {
            signInButton.visibility = View.INVISIBLE
            signOutButton.visibility = View.VISIBLE
            currUserDataDBRef = FirebaseDatabase.getInstance().getReference("Users").child(account.uid)
            velUsers = currUserDataDBRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(currUserDBData: DataSnapshot) {

                    currUserData = currUserDBData.getValue(User::class.java)!!.userData!!
                    UBundle.putString("UserName",currUserData.UserName)
                    UBundle.putString("UserEmail", currUserData.UserEmail)
                    UBundle.putString("UserID", currUserData.UserID)
                }
            })

        }
    }

}
