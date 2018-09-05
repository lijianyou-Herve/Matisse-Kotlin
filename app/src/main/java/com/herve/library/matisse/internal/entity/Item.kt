package com.herve.library.matisse.internal.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
@Parcelize
class Item(var id: Long, var type: String, var size: Long = 0, var duration: Long = 0) : Parcelable {

}