package com.example.cresllo.activity.tasks

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
import com.example.cresllo.modals.Boards
import com.example.cresllo.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_board.*
import java.io.IOException

class BoardActivity : BaseActivity() {
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUsername : String
    private var mBoardImageURL : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        setUpActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUsername = intent.getStringExtra(Constants.NAME).toString()
        }

iv_board_image.setOnClickListener {
        if (ContextCompat.checkSelfPermission(
                this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )}
}
        btn_create.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)

        finish()
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        toolbar_create_board_activity .setNavigationOnClickListener { onBackPressed() }

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
                    "OOPS, you just denied the permission for storage. You cannot allow the image",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        var board = Boards(
            et_board_name.text.toString(),
            mBoardImageURL,
            mUsername,
            assignedUsersArrayList
        )

        Firebasestore().createBoard(this,board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))



        if (mSelectedImageFileUri != null) {

            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    Constants.BOARDS_IMAGE +
                            System.currentTimeMillis() + "."
                            + Constants.getFileExtension(this, mSelectedImageFileUri)
                )
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i(
                    " Board Image Url",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mBoardImageURL = uri.toString()

                    createBoard()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this@BoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
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
                    .into(iv_board_image);
            }
            catch (e : IOException){
                e.printStackTrace()
            }
        }
    }
}