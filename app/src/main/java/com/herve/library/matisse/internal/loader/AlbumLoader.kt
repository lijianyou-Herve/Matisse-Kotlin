package com.herve.library.matisse.internal.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import com.herve.library.matisse.internal.entity.Album
import com.herve.library.matisse.internal.entity.SelectionSpec

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */

class AlbumLoader
(context: Context,
 selection: String,
 selectionArgs: Array<String>) : CursorLoader(context,
        QUERY_URI, PROJECTION, selection, selectionArgs,
        BUCKET_ORDER_BY) {

    companion object {

        const val COLUMN_COUNT = "count"
        private val QUERY_URI = MediaStore.Files.getContentUri("external")
        private val COLUMNS = arrayOf(MediaStore.Files.FileColumns._ID, "bucket_id", "bucket_display_name", MediaStore.MediaColumns.DATA, COLUMN_COUNT)
        private val PROJECTION = arrayOf(MediaStore.Files.FileColumns._ID, "bucket_id", "bucket_display_name", MediaStore.MediaColumns.DATA, "COUNT(*) AS $COLUMN_COUNT")
        private const val BUCKET_ORDER_BY = "datetaken DESC"

        // === params for showSingleMediaType: false ===
        private const val SELECTION = (
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                        + ") GROUP BY (bucket_id")
        private val SELECTION_ARGS = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
        // =============================================

        // === params for showSingleMediaType: true ===
        private const val SELECTION_FOR_SINGLE_MEDIA_TYPE = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                        + ") GROUP BY (bucket_id")


        private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
            return arrayOf(mediaType.toString())
        }

        fun newInstance(context: Context): CursorLoader {
            val selection: String
            val selectionArgs: Array<String>
            if (SelectionSpec.getInstance().onlyShowImages()) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            } else {
                selection = SELECTION
                selectionArgs = SELECTION_ARGS
            }
            return AlbumLoader(context, selection, selectionArgs)
        }

    }

    // =============================================

    fun newInstance(context: Context): CursorLoader {
        val selection: String
        val selectionArgs: Array<String>
        if (SelectionSpec.getInstance().onlyShowImages()) {
            selection = SELECTION_FOR_SINGLE_MEDIA_TYPE
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        } else if (SelectionSpec.getInstance().onlyShowVideos()) {
            selection = SELECTION_FOR_SINGLE_MEDIA_TYPE
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        } else {
            selection = SELECTION
            selectionArgs = SELECTION_ARGS
        }
        return AlbumLoader(context, selection, selectionArgs)
    }

    override fun loadInBackground(): Cursor? {

        val albums = super.loadInBackground()
        val allAlbum = MatrixCursor(COLUMNS)
        var totalCount = 0
        var allAlbumCoverPath = ""
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT))
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA))
            }
        }
        allAlbum.addRow(arrayOf(Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath, totalCount.toString()))

        return MergeCursor(arrayOf<Cursor>(allAlbum, albums!!))
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}