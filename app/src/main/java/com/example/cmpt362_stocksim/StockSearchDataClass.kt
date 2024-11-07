package com.example.cmpt362_stocksim

import android.os.Parcel
import android.os.Parcelable



data class StockSearchDataClass(var dataImage: Int, var dataTitle: String, var dataDesc: String, var dataDetailImage: Int, var ticker: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dataImage)
        parcel.writeString(dataTitle)
        parcel.writeString(dataDesc)
        parcel.writeInt(dataDetailImage)
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
