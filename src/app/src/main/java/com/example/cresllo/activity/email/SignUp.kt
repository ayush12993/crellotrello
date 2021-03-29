package com.example.cresllo.activity.email

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.modals.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_signup_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolbar_signup_activity.setNavigationOnClickListener { onBackPressed() }

        btn_signup.setOnClickListener {
            registerUser()
        }
    }

     fun userRegisteredSuccess(){
        Toast.makeText(this," you have successfully registered ",
            Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name:String = et_name.text.toString().trim{ it <= ' '}
        val email:String = et_email.text.toString().trim{ it <= ' '}
        val password:String = et_password.text.toString().trim{ it <= ' '}

        if (validateForm(name, email, password)){
           showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener  (
                {
                    task ->

                    if (task.isSuccessful){
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = Users(firebaseUser.uid, name ,email)

                        Firebasestore().registerUser(this,user)
                    }else{
                        Toast.makeText(
                            this,
                            task.exception!!.message,Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    private fun validateForm(name: String, email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar(resources.getString(R.string.name_enter))
                false
            }
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