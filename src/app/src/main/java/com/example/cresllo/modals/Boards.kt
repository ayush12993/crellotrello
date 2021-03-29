package com.example.cresllo.modals

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeStringList

data class Boards (
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId : String = "",
    var taskList: ArrayList<Task> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
            parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Boards> {
        override fun createFromParcel(parcel: Parcel): Boards {
            return Boards(parcel)
        }

        override fun newArray(size: Int): Array<Boards?> {
            return arrayOfNulls(size)
        }
    }
}