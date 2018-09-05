package com.herve.library.matisse.internal.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
@Parcelize
class Album(var mId: String, var mCoverPath: String, var mDisplayName: String, var mCount: Long = 0) : Parcelable {

}