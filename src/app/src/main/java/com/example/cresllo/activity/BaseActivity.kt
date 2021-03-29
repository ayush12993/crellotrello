package com.example.cresllo.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.cresllo.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

    }
    fun showProgressDialog(text:String){
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,
        resources.getString(R.string.please_click_again_to_exit),
            Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false},2000)
    }

    fun showErrorSnackBar(message: String){
        val Snackbar = Snackbar.make(findViewById(android.R.id.content),
            message,Snackbar.LENGTH_LONG)
        val snackBarView = Snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.Snackbar_error_color))
        Snackbar.show()
    }
}