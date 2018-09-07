/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.herve.library.matisse.internal.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import com.herve.library.matisse.internal.entity.Album
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.herve.library.matisse.internal.utils.MediaStoreCompat


/**
 * Load images and videos into a single cursor.
 */
class AlbumMediaLoader private constructor(context: Context, selection: String, selectionArgs: Array<String>, private val mEnableCapture: Boolean) : CursorLoader(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY) {

    override fun loadInBackground(): Cursor? {
        val result = super.loadInBackground()
        if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(context)) {
            return result
        }
        val dummy = MatrixCursor(PROJECTION)
        dummy.addRow(arrayOf(Item.ITEM_ID_CAPTURE, Item.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0))
        return MergeCursor(arrayOf<Cursor>(dummy, result!!))
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }

    companion object {
        private val QUERY_URI = MediaStore.Files.getContentUri("external")
        private val PROJECTION = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE, "duration")

        // === params for album ALL && showSingleMediaType: false ===
        private val SELECTION_ALL = (
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")
        private val SELECTION_ALL_ARGS = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
        // ===========================================================

        // === params for album ALL && showSingleMediaType: true ===
        private val SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
            return arrayOf(mediaType.toString())
        }
        // =========================================================

        // === params for ordinary album && showSingleMediaType: false ===
        private val SELECTION_ALBUM = (
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                        + " AND "
                        + " bucket_id=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private fun getSelectionAlbumArgs(albumId: String): Array<String> {
            return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), albumId)
        }
        // ===============================================================

        // === params for ordinary album && showSingleMediaType: true ===
        private val SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                        + " AND "
                        + " bucket_id=?"
                        + " AND " + MediaStore.MediaColumns.SIZE + ">0")

        private fun getSelectionAlbumArgsForSingleMediaType(mediaType: Int, albumId: String): Array<String> {
            return arrayOf(mediaType.toString(), albumId)
        }
        // ===============================================================

        private val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"

        fun newInstance(context: Context, album: Album, capture: Boolean): CursorLoader {
            val selection: String
            val selectionArgs: Array<String>
            val enableCapture: Boolean

            if (album.isAll()) {
                if (SelectionSpec.getInstance().onlyShowImages()) {
                    selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                    selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                    selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
                    selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                } else {
                    selection = SELECTION_ALL
                    selectionArgs = SELECTION_ALL_ARGS
                }
                enableCapture = capture
            } else {
                if (SelectionSpec.getInstance().onlyShowImages()) {
                    selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
                    selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                            album.mId)
                } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                    selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
                    selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                            album.mId)
                } else {
                    selection = SELECTION_ALBUM
                    selectionArgs = getSelectionAlbumArgs(album.mId)
                }
                enableCapture = false
            }
            return AlbumMediaLoader(context, selection, selectionArgs, enableCapture)
        }
    }
}
