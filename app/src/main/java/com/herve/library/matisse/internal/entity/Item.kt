package com.herve.library.matisse.internal.entity

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import com.herve.library.matisse.internal.MimeType
import kotlinx.android.parcel.Parcelize

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */


@Parcelize
class Item(var id: Long, var mimeType: String, var size: Long = 0, var duration: Long = 0) : Parcelable {
    private var uri: Uri

    companion object {
        const val ITEM_ID_CAPTURE: Long = -1
        const val ITEM_DISPLAY_NAME_CAPTURE = "Capture"

        fun valueOf(cursor: Cursor): Item {
            return Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                    cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                    cursor.getLong(cursor.getColumnIndex("duration")))
        }
    }

    init {
        val contentUri: Uri
        when {
            isImage() -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo() -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> // ?
                contentUri = MediaStore.Files.getContentUri("external")
        }
        uri = ContentUris.withAppendedId(contentUri, id)
    }

    fun getContentUri(): Uri {
        return uri
    }

    fun isCapture(): Boolean {
        return id == ITEM_ID_CAPTURE
    }

    fun isImage(): Boolean {
        return if (mimeType == null) false else mimeType == MimeType.JPEG.toString()
                || mimeType == MimeType.PNG.toString()
                || mimeType == MimeType.GIF.toString()
                || mimeType == MimeType.BMP.toString()
                || mimeType == MimeType.WEBP.toString()
    }

    fun isGif(): Boolean {
        return if (mimeType == null) false else mimeType == MimeType.GIF.toString()
    }

    fun isVideo(): Boolean {
        return if (mimeType == null) false else mimeType == MimeType.MPEG.toString()
                || mimeType == MimeType.MP4.toString()
                || mimeType == MimeType.QUICKTIME.toString()
                || mimeType == MimeType.THREEGPP.toString()
                || mimeType == MimeType.THREEGPP2.toString()
                || mimeType == MimeType.MKV.toString()
                || mimeType == MimeType.WEBM.toString()
                || mimeType == MimeType.TS.toString()
                || mimeType == MimeType.AVI.toString()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Item) {
            return false
        }

        val other = obj as Item?
        return (id == other!!.id
                && (mimeType != null && mimeType == other.mimeType || mimeType == null && other.mimeType == null)
                && (uri != null && uri == other.uri || uri == null && other.uri == null)
                && size == other.size
                && duration == other.duration)
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + java.lang.Long.valueOf(id).hashCode()
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode()
        }
        result = 31 * result + uri.hashCode()
        result = 31 * result + java.lang.Long.valueOf(size).hashCode()
        result = 31 * result + java.lang.Long.valueOf(duration).hashCode()
        return result
    }

}