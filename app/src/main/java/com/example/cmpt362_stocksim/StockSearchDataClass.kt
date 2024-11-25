package com.example.cmpt362_stocksim

import android.os.Parcel
import android.os.Parcelable


data class StockSearchDataClass(var dataTitle: String, var dataDesc: String, var ticker: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dataTitle)
        parcel.writeString(dataDesc)
        parcel.writeString(ticker)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StockSearchDataClass> {
        override fun createFromParcel(parcel: Parcel): StockSearchDataClass {
            return StockSearchDataClass(parcel)
        }

        override fun newArray(size: Int): Array<StockSearchDataClass?> {
            return arrayOfNulls(size)
        }
    }
}