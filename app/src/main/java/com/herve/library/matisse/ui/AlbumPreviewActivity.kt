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
package com.herve.library.matisse.ui

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import com.herve.library.matisse.internal.entity.Album
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.zhihu.matisse.internal.model.AlbumMediaCollection
import com.zhihu.matisse.internal.ui.adapter.PreviewPagerAdapter
import kotlinx.android.synthetic.main.activity_media_preview.*
import java.util.*

class AlbumPreviewActivity : BasePreviewActivity(), AlbumMediaCollection.AlbumMediaCallbacks {

    private val mCollection = AlbumMediaCollection()

    private var mIsAlreadySetPosition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        mCollection.onCreate(this, this)
        val album = intent.getParcelableExtra<Album>(EXTRA_ALBUM)
        mCollection.load(album)

        val item = intent.getParcelableExtra<Item>(EXTRA_ITEM)
        if (mSpec.countable) {
            check_view.setCheckedNum(mSelectedCollection.checkedNumOf(item))
        } else {
            check_view.setChecked(mSelectedCollection.isSelected(item))
        }
        updateSize(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCollection.onDestroy()
    }

    override fun onAlbumMediaLoad(cursor: Cursor) {
        val items = ArrayList<Item>()
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor))
        }
        //        cursor.close();

        if (items.isEmpty()) {
            return
        }

        val adapter = pager.adapter as PreviewPagerAdapter?
        adapter!!.addAll(items)
        adapter.notifyDataSetChanged()
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true
            val selected = intent.getParcelableExtra<Item>(EXTRA_ITEM)
            val selectedIndex = items.indexOf(selected)
            pager.setCurrentItem(selectedIndex, false)
            mPreviousPos = selectedIndex
        }
    }

    override fun onAlbumMediaReset() {

    }

    companion object {

        val EXTRA_ALBUM = "extra_album"
        val EXTRA_ITEM = "extra_item"
    }
}
