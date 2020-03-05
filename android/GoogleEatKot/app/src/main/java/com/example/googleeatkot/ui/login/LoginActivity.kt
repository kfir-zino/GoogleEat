package com.example.googleeatkot.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.*

import com.example.googleeatkot.R
import com.example.googleeatkot.User
import com.example.googleeatkot.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
// import android.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var databaseRef: DatabaseReference
    private lateinit var DBUserRef: DatabaseReference
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    var ourResult: Int = 0
    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()



        user = auth.currentUser
        if (user != null) {
            ourResult = 0
            this.finish()
        } else {
            this.signIn()
        }
    }



    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GoogleSignIn.getLastSignedInAccount(this) //TODO check if needed
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("EMDT_ERROR", "Google sign in failed", e)
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.w("EMDT_ERROR", "Google sign in failed", e)
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("EMDT_ERROR", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("EMDT_ERROR", "signInWithCredential:success")
                    try {
                        user = auth.currentUser!!
                        ourResult = 0
                        Toast.makeText(
                            applicationContext,
                            "welcome ${user!!.displayName}",
                            Toast.LENGTH_LONG
                        ).show()
                        databaseRef = FirebaseDatabase.getInstance().getReference("messages")
                        databaseRef.setValue("Hello, \${user!!.displayName!}")
                        DBUserRef = FirebaseDatabase.getInstance().getReference("Users")
                        DBUserRef.child(user!!.uid).addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(databsaeError: DatabaseError) {
                                Toast.makeText(
                                    applicationContext,
                                    "Firebase failed to get data for ${user!!.displayName}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    //writing new user to DB
//                                    DBUserRef.child(user!!.uid).setValue(user!!)
                                    val newUserData = UserData(user!!.displayName!!,user!!.email!!, user!!.uid)
                                    val newUser  = User(newUserData,null,null)
                                    DBUserRef.child(user!!.uid).setValue(newUser)
                                    Toast.makeText(
                                        applicationContext,
                                        "Welcome to GoogleEat ${user!!.displayName}!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        })
                    } catch (e: ApiException){
                        ourResult = 1
                        Toast.makeText(
                            applicationContext,
                            "connection failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    this.finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("EMDT_ERROR", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        applicationContext,
                        "connection failed",
                        Toast.LENGTH_LONG
                    ).show()
                    this.finish()
                }
            }
    }

    override fun finish() {
        val data = Intent()
        user = auth.currentUser!!
        if (ourResult == 0) {
            data.putExtra("FBuser", user)
            data.putExtra("gso", gso)
//            mGoogleSignInClient.signOut()
            setResult(RESULT_OK, data)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        super.finish()
    }
}


