package com.example.cresllo.activity.tasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.adapters.TaskListItemsAdapter
import com.example.cresllo.modals.Boards
import com.example.cresllo.modals.Card
import com.example.cresllo.modals.Task
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails : Boards
    private lateinit var mBoardDocumentId: String
     lateinit var mAssignedMemberDetailList: ArrayList<Users>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        if (intent.hasExtra(Constants.DOCUMENT_ID)){
           mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        Firebasestore().getBoardDetails(this,mBoardDocumentId)

    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)

            actionBar.title = mBoardDetails.name
        }
        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode ==
            Constants.MEMBER_REQUEST_CODE || requestCode == Constants.CARD_DETAIL_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))

            Firebasestore().getBoardDetails(this@TaskListActivity,mBoardDocumentId)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }

    fun cardDetail(taskListPosition: Int, cardPosition: Int){
        val intent = Intent(this@TaskListActivity, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        startActivityForResult(intent,Constants.CARD_DETAIL_REQUEST_CODE)
    }

    fun boardDetails(boards: Boards){
        mBoardDetails = boards
        hideProgressDialog()
        setUpActionBar()



        showProgressDialog(resources.getString(R.string.please_wait))

        Firebasestore().getAssignedMembersListDetails(
            this,mBoardDetails.assignedTo
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent,Constants.MEMBER_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

     fun addUpdateTaskListSuccess(){
         hideProgressDialog()

         showProgressDialog(resources.getString(R.string.please_wait))
         Firebasestore().getBoardDetails(this,mBoardDetails.documentId)
     }
    fun createTaskList(taskListName: String){
        val task = Task(taskListName, Firebasestore().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))

        Firebasestore().addUpdateTaskList(this,mBoardDetails)
    }
    fun updateTaskList(position: Int,listName: String,model: Task){
        val task = Task(listName,model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))

        Firebasestore().addUpdateTaskList(this,mBoardDetails)
    }
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))

        Firebasestore().addUpdateTaskList(this,mBoardDetails)
    }
    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(Firebasestore().getCurrentUserId())

        val card = Card(cardName,Firebasestore().getCurrentUserId(),cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards

        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        Firebasestore().addUpdateTaskList(this,mBoardDetails)
    }
   fun boardMembersDetailsList(list: ArrayList<Users>){
       mAssignedMemberDetailList = list

       hideProgressDialog()

       val addTaskList = Task(resources.getString(R.string.add_list))
       mBoardDetails.taskList.add(addTaskList)

       rv_task_list.layoutManager = LinearLayoutManager(this,
               LinearLayoutManager.HORIZONTAL,false)

       rv_task_list.setHasFixedSize(true)

       val adapter = TaskListItemsAdapter(this,mBoardDetails.taskList)
       rv_task_list.adapter = adapter
   }

    fun updateCardInTaskList(taskListPosition: Int, cards: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        mBoardDetails.taskList[taskListPosition].cards = cards
        showProgressDialog(resources.getString(R.string.please_wait))
        Firebasestore().addUpdateTaskList(this,mBoardDetails)
    }

}