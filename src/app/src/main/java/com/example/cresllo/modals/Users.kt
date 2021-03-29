package com.example.cresllo.modals

import android.os.Parcel
import android.os.Parcelable

data class Users (
    val id:String = "",
    val name:String = "",
    val email:String = "",
    val image:String = "",
    val mobile:Long = 0,
    val fcmToken:String = "",
    var selected: Boolean = false

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeLong(mobile)
        parcel.writeString(fcmToken)

    }


    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}