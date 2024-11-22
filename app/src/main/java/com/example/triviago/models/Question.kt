package com.example.triviago.models

import android.os.Parcel
import android.os.Parcelable

data class Question(
    val questionText: String,
    val isBooleanType: Boolean,
    val answer: String,
    val options: List<String>,
    val difficulty: String,
    val category: String? = null

) : Parcelable {
    private constructor(parcel: Parcel) : this(
        questionText = parcel.readString() ?: "",
        options = parcel.createStringArrayList() ?: emptyList(),
        answer = parcel.readString() ?: "",
        isBooleanType = parcel.readByte() != 0.toByte(),
        difficulty = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionText)
        parcel.writeStringList(options)
        parcel.writeString(answer)
        parcel.writeByte(if (isBooleanType) 1 else 0)
        parcel.writeString(difficulty)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
