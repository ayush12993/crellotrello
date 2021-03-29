package com.example.cresllo.activity.tasks

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.BaseActivity
import com.example.cresllo.adapters.CardMemberListItemsAdapter
import com.example.cresllo.dialog.LabelColorListDialog
import com.example.cresllo.dialog.MembersListDialog
import com.example.cresllo.modals.*
import com.example.cresllo.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Boards
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<Users>
    private var mSelectedStartDateMilliSeconds: Long = 0
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()

        setUpActionBar()

        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            }
            else{
                Toast.makeText(this@CardDetailsActivity,
                "Enter a card name.",Toast.LENGTH_SHORT).show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()

        mSelectedDueDateMilliSeconds = mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardPosition].dueDate


        if (mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH)
            val selectedDueDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            tv_select_due_date.text = selectedDueDate
        }

        tv_select_due_date.setOnClickListener {
            showDueDataPicker()
        }

        mSelectedStartDateMilliSeconds = mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardPosition].startDate

        if (mSelectedStartDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH)
            val selectedStartDate = simpleDateFormat.format(Date(mSelectedStartDateMilliSeconds))
            tv_select_start_date.text = selectedStartDate
        }

        tv_select_start_date.setOnClickListener {
            showStartDataPicker()
        }

    }

    fun addUpdateTaskListSuccess(){
         hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()

    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }
        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.action_delete_card -> {
               alertDialogForDeleteCard( mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
           }
       }

        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun membersListDialog(){
        var cardAssignedMembersList =  mBoardDetails.taskList[mTaskListPosition].
        cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0){
            for (i in mMembersDetailList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
                this,
                mMembersDetailList,
                resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: Users, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]
                                    .assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]
                                .assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]
                            .assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices){
                        if (mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected =false
                        }
                    }
                }
                setupSelectedMembersList()
            }

        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        val card = Card(
        et_name_card_details.text.toString(),
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,mSelectedDueDateMilliSeconds,mSelectedStartDateMilliSeconds
        )

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        Firebasestore().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        Firebasestore().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){
            dialog, which ->
            dialog.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){
            dialog, which ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun colorsList(): ArrayList<String>{
      val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    private fun setColor(){
        tv_select_label_color.text=""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog(){
       val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    private fun setupSelectedMembersList(){
        val cardAssignedMembersList =  mBoardDetails.
        taskList[mTaskListPosition].cards[mCardPosition].assignedTo

      val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices){
            for (j in cardAssignedMembersList){
                if (mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                           mMembersDetailList[i].id,
                            mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
    }
        if (selectedMembersList.size > 0){
        selectedMembersList.add(SelectedMembers("",""))
            tv_select_members.visibility = View.GONE
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(
                    this,6
            )
            val adapter = CardMemberListItemsAdapter(this,selectedMembersList,true)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(
                    object : CardMemberListItemsAdapter.OnClickListener{
                        override fun onClick() {
                            membersListDialog()
                        }

                    }
            )
        }
        else{
            tv_select_members.visibility = View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }

    private fun showDueDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
                c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    /*
                      The listener used to indicate the user has finished selecting a date.
                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                    // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                    val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                    val sMonthOfYear =
                            if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                    val selectedDueDate = "$sDayOfMonth/$sMonthOfYear/$year"
                    // Selected date it set to the TextView to make it visible to user.
                    tv_select_due_date.text = selectedDueDate

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                    // The formatter will parse the selected date in to Date object
                    // so we can simply get date in to milliseconds.
                    val theDate = sdf.parse(selectedDueDate)

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */
                    mSelectedDueDateMilliSeconds = theDate!!.time

                },
                year,
                month,
                day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }
    private fun showStartDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
                c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    /*
                      The listener used to indicate the user has finished selecting a date.
                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                    // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                    val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                    val sMonthOfYear =
                            if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                    val selectedStartDate = "$sDayOfMonth/$sMonthOfYear/$year"
                    // Selected date it set to the TextView to make it visible to user.
                    tv_select_start_date.text = selectedStartDate

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                    // The formatter will parse the selected date in to Date object
                    // so we can simply get date in to milliseconds.
                    val theDate = sdf.parse(selectedStartDate)

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    mSelectedStartDateMilliSeconds = theDate!!.time
                },
                year,
                month,
                day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }

}