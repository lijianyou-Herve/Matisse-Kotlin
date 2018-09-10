/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.entity.Album
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.zhihu.matisse.internal.model.AlbumMediaCollection
import com.zhihu.matisse.internal.model.SelectedItemCollection
import com.zhihu.matisse.internal.ui.adapter.AlbumMediaAdapter
import com.zhihu.matisse.internal.ui.widget.MediaGridInset
import com.zhihu.matisse.internal.utils.UIUtils
import kotlinx.android.synthetic.main.fragment_media_selection.*
import java.lang.IllegalStateException

class MediaSelectionFragment : Fragment(), AlbumMediaCollection.AlbumMediaCallbacks, AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener {

    private val mAlbumMediaCollection = AlbumMediaCollection()
    private lateinit var mAdapter: AlbumMediaAdapter
    private lateinit var mSelectionProvider: SelectionProvider
    private var mCheckStateListener: AlbumMediaAdapter.CheckStateListener? = null
    private var mOnMediaClickListener: AlbumMediaAdapter.OnMediaClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SelectionProvider) {
            mSelectionProvider = context
        } else {
            throw IllegalStateException("Context must implement SelectionProvider.")
        }
        if (context is AlbumMediaAdapter.CheckStateListener) {
            mCheckStateListener = context
        }
        if (context is AlbumMediaAdapter.OnMediaClickListener) {
            mOnMediaClickListener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val album = arguments!!.getParcelable<Album>(EXTRA_ALBUM)

        context?.let {
            mAdapter = AlbumMediaAdapter(it,
                    mSelectionProvider.provideSelectedItemCollection(), mRecyclerView)
            mAdapter.registerCheckStateListener(this)
            mAdapter.registerOnMediaClickListener(this)
            mRecyclerView.setHasFixedSize(true)

            val spanCount: Int
            val selectionSpec = SelectionSpec.getInstance()
            if (selectionSpec.gridExpectedSize > 0) {
                spanCount = UIUtils.spanCount(context!!, selectionSpec.gridExpectedSize)
            } else {
                spanCount = selectionSpec.spanCount
            }
            mRecyclerView.layoutManager = GridLayoutManager(context, spanCount)

            val spacing = resources.getDimensionPixelSize(R.dimen.media_grid_spacing)
            mRecyclerView.addItemDecoration(MediaGridInset(spanCount, spacing, false))
            mRecyclerView.adapter = mAdapter
            mAlbumMediaCollection.onCreate(activity!!, this)
            mAlbumMediaCollection.load(album, selectionSpec.capture)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAlbumMediaCollection.onDestroy()
    }

    fun refreshMediaGrid() {
        mAdapter!!.notifyDataSetChanged()
    }

    fun refreshSelection() {
        mAdapter!!.refreshSelection()
    }

    override fun onAlbumMediaLoad(cursor: Cursor) {
        mAdapter!!.swapCursor(cursor)
    }

    override fun onAlbumMediaReset() {
        mAdapter!!.swapCursor(null)
    }

    override fun onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener!!.onUpdate()
        }
    }

    override fun onMediaClick(album: Album, item: Item, adapterPosition: Int) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener!!.onMediaClick(arguments!!.getParcelable<Parcelable>(EXTRA_ALBUM) as Album,
                    item, adapterPosition)
        }
    }

    interface SelectionProvider {
        fun provideSelectedItemCollection(): SelectedItemCollection
    }

    companion object {

        val EXTRA_ALBUM = "extra_album"

        fun newInstance(album: Album): MediaSelectionFragment {
            val fragment = MediaSelectionFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_ALBUM, album)
            fragment.arguments = args
            return fragment
        }
    }
}
