package com.example.cresllo.activity.email

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.activity.tasks.MainActivity
import com.example.cresllo.modals.Users
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignIn : BaseActivity() {

    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setUpActionBar()
        btn_signin.setOnClickListener {
            signInRegisteredUser()
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_signin_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolbar_signin_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun  signInSuccess(user : Users){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun signInRegisteredUser(){
        val email: String = ets_email.text.toString().trim{ it <= ' '}
        val password: String = ets_password.text.toString().trim{ it <= ' '}

        if (validateForm(email ,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }


    private fun validateForm( email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar(resources.getString(R.string.email_enter))
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar(resources.getString(R.string.password_enter))
                false
            }
            else -> {
                true
            }
        }
    }

}