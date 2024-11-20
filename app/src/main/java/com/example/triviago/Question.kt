package com.example.triviago

import android.os.Parcel
import android.os.Parcelable

data class Question(
    val questionText: String,
    val isBooleanType: Boolean,
    val answer: String,
    val options: List<String>,
    val difficulty: String

) : Parcelable {
    // Constructor for creating an object from a Parcel
    private constructor(parcel: Parcel) : this(
        questionText = parcel.readString() ?: "",
        options = parcel.createStringArrayList() ?: emptyList(),
        answer = parcel.readString() ?: "",
        isBooleanType = parcel.readByte() != 0.toByte(),
        difficulty = parcel.readString() ?: ""
    )

    // Writing object's data to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionText)
        parcel.writeStringList(options)
        parcel.writeString(answer)
        parcel.writeByte(if (isBooleanType) 1 else 0)
        parcel.writeString(difficulty)
    }

    // Describe the contents of this Parcelable (default 0)
    override fun describeContents(): Int = 0

    // Companion object with the CREATOR field
    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
