package com.example.clothingstoreapp.models
import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    var productId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var selectedSize: String = "",
    var quantity: Int = 0,
    var isSelected: Boolean = false
) : Parcelable {

    constructor() : this("", "", 0.0, "", "", 0, false)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(imageUrl)
        parcel.writeString(selectedSize)
        parcel.writeInt(quantity)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem = CartItem(parcel)
        override fun newArray(size: Int): Array<CartItem?> = arrayOfNulls(size)
    }
}