package com.example.cresllo.modals

import android.os.Parcel
import android.os.Parcelable

data class Card (
    val name: String="",
    val createdBy: String="",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor : String = "",
    val dueDate :Long = 0,
    val startDate: Long = 0
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.createStringArrayList()!!,
        source.readString()!!,
            source.readLong()!!,
            source.readLong()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(labelColor)
        writeLong(dueDate)
        writeLong(startDate)
    }

    override fun describeContents(): Int =0

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(source: Parcel): Card {
            return Card(source) }
        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}