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
package com.zhihu.matisse.internal.model

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import com.herve.library.matisse.internal.entity.Album
import com.herve.library.matisse.internal.loader.AlbumMediaLoader

import java.lang.ref.WeakReference

class AlbumMediaCollection : LoaderManager.LoaderCallbacks<Cursor> {
    private var mContext: WeakReference<Context>? = null
    private var mLoaderManager: LoaderManager? = null
    private var mCallbacks: AlbumMediaCallbacks? = null

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val context = mContext!!.get() ?: return null!!

        val album = args!!.getParcelable<Album>(ARGS_ALBUM) ?: return null!!

        return AlbumMediaLoader.newInstance(context, album,
                album.isAll() && args.getBoolean(ARGS_ENABLE_CAPTURE, false))
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        val context = mContext!!.get() ?: return

        mCallbacks!!.onAlbumMediaLoad(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        val context = mContext!!.get() ?: return

        mCallbacks!!.onAlbumMediaReset()
    }

    fun onCreate(context: FragmentActivity, callbacks: AlbumMediaCallbacks) {
        mContext = WeakReference(context)
        mLoaderManager = context.supportLoaderManager
        mCallbacks = callbacks
    }

    fun onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager!!.destroyLoader(LOADER_ID)
        }
        mCallbacks = null
    }

    @JvmOverloads
    fun load(target: Album?, enableCapture: Boolean = false) {
        val args = Bundle()
        args.putParcelable(ARGS_ALBUM, target)
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture)
        mLoaderManager!!.initLoader(LOADER_ID, args, this)
    }

    interface AlbumMediaCallbacks {

        fun onAlbumMediaLoad(cursor: Cursor)

        fun onAlbumMediaReset()
    }

    companion object {
        private val LOADER_ID = 2
        private val ARGS_ALBUM = "args_album"
        private val ARGS_ENABLE_CAPTURE = "args_enable_capture"
    }
}
