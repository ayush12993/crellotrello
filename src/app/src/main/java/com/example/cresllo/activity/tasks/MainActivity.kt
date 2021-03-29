package com.example.cresllo.activity.tasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.activity.email.IntroActivity
import com.example.cresllo.activity.email.Profile_Activity
import com.example.cresllo.activity.tasks.BoardActivity
import com.example.cresllo.activity.tasks.TaskListActivity
import com.example.cresllo.adapters.BoardItemsAdapter
import com.example.cresllo.modals.Boards
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_bar_main.*
import kotlinx.android.synthetic.main.activity_content_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {

   private lateinit var mUsername: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()

        nav_view.setNavigationItemSelectedListener (this)

        Firebasestore().loadUserData(this,true)

        fab_create_board.setOnClickListener {
            val intent =  Intent(this, BoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            startActivityForResult(intent,Constants.CREATE_BOARD_REQUEST_CODE)
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Boards>){
       hideProgressDialog()

        if (boardsList.size>0){
            rv_boards_list.visibility = View.VISIBLE
            no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this,boardsList)
            rv_boards_list.adapter = adapter


            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Boards) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })


        }else{
            rv_boards_list.visibility = View.GONE
            no_boards_available.visibility = View.VISIBLE
        }
    }

    fun updateNavigationUserDetails(user :Users, readBoardsLists: Boolean){

        mUsername = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .into(nav_image_view);

        tv_username.text = user.name

        if (readBoardsLists){
            showProgressDialog(resources.getString(R.string.please_wait))
            Firebasestore().getBoardsList(this)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.MY_PROFILE_REQUEST_CODE){
            Firebasestore().loadUserData(this)
        }else if(resultCode == Activity.RESULT_OK
                && requestCode == Constants.CREATE_BOARD_REQUEST_CODE){
            Firebasestore().getBoardsList(this)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_activity_main)
        toolbar_activity_main.setNavigationIcon(R.drawable.ic_menu)
        toolbar_activity_main.setNavigationOnClickListener {
               toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_myprofile ->{
              startActivityForResult(Intent(this, Profile_Activity::class.java), Constants.MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_signout ->{
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }
}