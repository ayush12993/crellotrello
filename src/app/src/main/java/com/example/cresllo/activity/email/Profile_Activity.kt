package com.example.cresllo.activity.email

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile_.*
import java.io.IOException

class Profile_Activity : BaseActivity() {


    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails:Users
    private var mProfileImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_)

        setUpActionBar()

        Firebasestore().loadUserData(this)

        iv_profile_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                 Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        btn_update.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,
                "OOPS, you just denied the permission for storage. You cannot allow the image",Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileUri = data.data

           try {
               Glide
                   .with(this)
                   .load(mSelectedImageFileUri)
                   .centerCrop()
                   .placeholder(R.drawable.ic_profile)
                   .into(iv_profile_image);
           }
           catch (e : IOException){
               e.printStackTrace()
           }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun setUserDataInUI(user: Users){

        mUserDetails = user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_profile)
            .into(iv_profile_image);

        tvp_name.setText(user.name)
        tvp_email.setText(user.email)
        if (user.mobile != 0L){
            tvp_mobile.setText(user.mobile.toString())
        }

    }
  private  fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (tvp_name.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = tvp_name.text.toString()
        }

        if (tvp_mobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = tvp_mobile.text.toString().toLong()
        }
       Firebasestore().updateUserProfileData(this,userHashMap)


    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null){

            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    Constants.USERS_IMAGE+
                            System.currentTimeMillis()+"."
                            +Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image Url",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this@Profile_Activity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }

    }

    fun profileUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
}