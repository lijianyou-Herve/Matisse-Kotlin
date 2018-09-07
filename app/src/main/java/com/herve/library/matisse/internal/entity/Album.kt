package com.herve.library.matisse.internal.entity

import android.content.Context
import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.loader.AlbumLoader
import kotlinx.android.parcel.Parcelize

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
@Parcelize
class Album(var mId: String, var mCoverPath: String, var mDisplayName: String, var mCount: Long = 0) : Parcelable {

    companion object {
        const val ALBUM_NAME_ALL: String = "All"
        const val ALBUM_ID_ALL: String = (-1).toString()

        /**
         * Constructs a new [Album] entity from the [Cursor].
         * This method is not responsible for managing cursor resource, such as close, iterate, and so on.
         */
        fun valueOf(cursor: Cursor): Album {
            return Album(
                    cursor.getString(cursor.getColumnIndex("bucket_id")),
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                    cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                    cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)))
        }
    }

    fun getId(): String {
        return mId
    }

    fun getCoverPath(): String {
        return mCoverPath
    }

    fun getCount(): Long {
        return mCount
    }

    fun addCaptureCount() {
        mCount++
    }

    fun getDisplayName(context: Context): String {
        return if (isAll()) {
            context.getString(R.string.album_name_all)
        } else mDisplayName
    }

    fun isAll(): Boolean {
        return ALBUM_ID_ALL == mId
    }

    fun isEmpty(): Boolean {
        return mCount == 0L
    }
}