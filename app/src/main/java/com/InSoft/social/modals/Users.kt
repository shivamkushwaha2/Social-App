package com.InSoft.social.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ansh on 25/08/18.
 */
@Parcelize
data class Users(
        val uid: String,
        val name: String,
        val profileImageUrl: String?
) : Parcelable {
    constructor() : this("", "", "")
}