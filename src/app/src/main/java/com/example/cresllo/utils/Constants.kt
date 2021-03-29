package com.example.cresllo.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val USERS_IMAGE: String = "users_image"
    const val BOARDS_IMAGE: String = "boards_image"
    const val STORED_VERIFICATION_ID :String= "storedVerificationId"

    const val BOARDS: String ="boards"
    const val ASSIGNED_TO: String ="assignedTo"
    const val DOCUMENT_ID: String ="documentId"

    const val IMAGE :String = "image"
    const val NAME :String = "name"
    const val MOBILE :String = "mobile"
    const val TASK_LIST:String = "taskList"
    const val BOARD_DETAIL: String = "board_detail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val BOARD_MEMBERS_LIST:String = "board_members_list"
    const val SELECT: String = "Select"
    const val UNSELECT: String = "UnSelect"


    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"



    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val MY_PROFILE_REQUEST_CODE:Int = 11
    const val CREATE_BOARD_REQUEST_CODE : Int = 12
    const val MEMBER_REQUEST_CODE : Int = 13
    const val CARD_DETAIL_REQUEST_CODE :Int = 14

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

  fun getFileExtension(activity: Activity,uri: Uri?) :String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}