package com.example.chatapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentAuthBinding
import com.example.chatapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.auth.GoogleAuthProvider



class AuthFragment : Fragment() {
    private val binding by lazy { FragmentAuthBinding.inflate(layoutInflater) }
    lateinit var googleSignInClient:GoogleSignInClient
    private  val TAG = "AuthFragment"
    lateinit var database:FirebaseDatabase
    lateinit var reference:DatabaseReference
    var RC_SIGN_IN = 1
    lateinit var auth:FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        if(auth.currentUser!=null){
            findNavController().navigate(
                R.id.viewPager,
                null,
                NavOptions.Builder()
                    .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
            )
        }
         googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")
        binding.btnSign.setOnClickListener {
            signIn()
        }
        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "onActivityResult: ${account.displayName}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d(TAG, "onActivityResult: failure ${e.message}")
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in succes,update UI with the signed-in user's information
                    Log.d(TAG, "firebaseAuthWithGoogle: succes")
                    val user = User(auth.uid,auth.currentUser?.displayName,auth.currentUser?.photoUrl.toString())
                    reference.child(auth.uid!!).setValue(user)
                    //updateUI (user)
                    Toast.makeText(context, "${user?.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.usersFragment)
                } else {
                    // if sign in fails,display a message to the user
                    Log.d(TAG, "firebaseAuthWithGoogle: failure", task.exception)
                    //updateUI (null)
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}