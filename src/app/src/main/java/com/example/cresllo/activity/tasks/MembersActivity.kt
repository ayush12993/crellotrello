package com.example.cresllo.activity.tasks

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.adapters.MemberListItemsAdapter
import com.example.cresllo.modals.Boards
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Boards
    private lateinit var mAssignedMembersList: ArrayList<Users>
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Boards>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            Firebasestore().getAssignedMembersListDetails(
                this,mBoardDetails.assignedTo
            )
        }
             setUpActionBar()


    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun setUpMembersList(list : ArrayList<Users>){

        mAssignedMembersList = list
        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this,list)
        rv_members_list.adapter =adapter
    }

    fun memberDetails(users: Users){
     mBoardDetails.assignedTo.add(users.id)
        Firebasestore().assignMemberToBoard(this,mBoardDetails,users)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
             menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {
             dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener {
            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()){
             dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                Firebasestore().getMemberDetails(this,email)
            }else{
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(users: Users){
        hideProgressDialog()
        mAssignedMembersList.add(users)

        anyChangesMade = true

        setUpMembersList(mAssignedMembersList)
    }

}