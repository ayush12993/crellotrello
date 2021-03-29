package com.example.cresllo.Firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.cresllo.activity.*
import com.example.cresllo.activity.email.Profile_Activity
import com.example.cresllo.activity.email.SignIn
import com.example.cresllo.activity.email.SignUp
import com.example.cresllo.activity.tasks.*
import com.example.cresllo.modals.Boards
import com.example.cresllo.modals.Users
import com.example.cresllo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Firebasestore {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getBoardsList(activity: MainActivity){
                mFirestore.collection(Constants.BOARDS)
                        .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
                        .get()
                        .addOnSuccessListener {
                            document ->
                            Log.i(activity.javaClass.simpleName,document.documents.toString())
                            val boardList: ArrayList<Boards> = ArrayList()
                            for (i in document.documents){
                                val boards = i.toObject(Boards::class.java)!!
                                boards.documentId = i.id
                                boardList.add(boards)

                            }
                            activity.populateBoardsListToUI(boardList)
                        }.addOnFailureListener { e ->

                            activity.hideProgressDialog()
                            Log.e(activity.javaClass.simpleName,"Error while creating boards",e)
                        }

    }
    fun addUpdateTaskList(activity: Activity,boards: Boards){
           val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = boards.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(boards.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList updated successfully")
                if (activity is TaskListActivity)
                activity.addUpdateTaskListSuccess()
                else if(activity is CardDetailsActivity)
                    activity.addUpdateTaskListSuccess()
            }.addOnFailureListener {
                exception ->
                if (activity is TaskListActivity)
                activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating the tasklist")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board = document.toObject(Boards::class.java)!!
                board.documentId = document.id


              activity.boardDetails(board)
            }.addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating boards",e)
            }

    }

    fun registerUser(activity: SignUp, userinfo: Users){
mFirestore.collection(Constants.USERS)
    .document(getCurrentUserId())
    .set(userinfo, SetOptions.merge())
    .addOnSuccessListener {
        activity.userRegisteredSuccess()
    }
    .addOnFailureListener {
        e ->
        Log.e(activity.javaClass.simpleName,"Error writing documents")
    }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
var loggedInUser = document.toObject(Users::class.java)
                if (loggedInUser != null)
                    when(activity) {
                        is SignIn ->{
                        activity.signInSuccess(loggedInUser)
                    }
                        is MainActivity ->{
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is Profile_Activity ->{
                         activity.setUserDataInUI(loggedInUser)
                        }
                    }
            }
            .addOnFailureListener {
                    e ->
                when(activity) {
                    is SignIn ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SigninUser","Error writing documents")
            }

    }

    fun getCurrentUserId(): String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
    fun createBoard(activity: BoardActivity, boards: Boards){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(boards, SetOptions.merge())
            .addOnSuccessListener {
               Log.e(activity.javaClass.simpleName,"Board created successfully.")

                Toast.makeText(activity,
                "Board created successfully",Toast.LENGTH_SHORT).show()
                 activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "erre while creating a board.",
                    exception
                )
            }
    }
    fun updateUserProfileData(activity: Profile_Activity,
                              userHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile data Updated")
                Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
                .addOnFailureListener {
                    e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating a board.",
                            e
                    )
                    Toast.makeText(activity,"Error when updating the profile!",Toast.LENGTH_LONG).show()
                }

    }

    fun getAssignedMembersListDetails(
        activity: Activity , assignedTo : ArrayList<String>){
          mFirestore.collection(Constants.USERS)
              .whereIn(Constants.ID , assignedTo)
              .get()
              .addOnSuccessListener {
                  document ->
                  Log.e(activity.javaClass.simpleName,document.documents.toString())

                  val usersList : ArrayList<Users> = ArrayList()

                  for (i in document.documents){
                      val user = i.toObject(Users::class.java)!!
                      usersList.add(user)
                  }
                  if (activity is MembersActivity)
                  activity.setUpMembersList(usersList)
                  else if (activity is TaskListActivity)
                      activity.boardMembersDetailsList(usersList)
              }.addOnFailureListener { e ->
                  if (activity is MembersActivity)
                      activity.hideProgressDialog()
                  else if (activity is TaskListActivity)
                  activity.hideProgressDialog()
                  Log.e(
                      activity.javaClass.simpleName,
                      "Error while creating a board.",
                      e
                  )
              }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
           mFirestore.collection(Constants.USERS)
               .whereEqualTo(Constants.EMAIL,email)
               .get()
               .addOnSuccessListener {
                   document ->
                   if (document.documents.size>0){
                       val user = document.documents[0].toObject(Users::class.java)!!
                       activity.memberDetails(user)
                   }
                   else{
                       activity.hideProgressDialog()
                       activity.showErrorSnackBar("No such member")
                   }
               }
               .addOnFailureListener { e ->
                   activity.hideProgressDialog()
                   Log.e(
                       activity.javaClass.simpleName,
                       "Error while getting user details",
                       e
                   )

               }
    }
    fun assignMemberToBoard(
            activity: MembersActivity, boards: Boards, users: Users){
         val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = boards.assignedTo

        mFirestore.collection(Constants.BOARDS)
            .document(boards.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(users)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)

            }
    }

}