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
package com.zhihu.matisse.internal.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec
import kotlinx.android.synthetic.main.media_grid_content.view.*

class MediaGrid : SquareFrameLayout, View.OnClickListener {

    private lateinit var mThumbnail: ImageView
    private lateinit var mGifTag: ImageView
    private lateinit var mVideoDuration: TextView
    private lateinit var media: Item
    private lateinit var mPreBindInfo: PreBindInfo
    private var mListener: OnMediaGridClickListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.media_grid_content, this, true)

        mThumbnail = findViewById<View>(R.id.media_thumbnail) as ImageView
        mGifTag = findViewById<View>(R.id.gif) as ImageView
        mVideoDuration = findViewById<View>(R.id.video_duration) as TextView

        mThumbnail.setOnClickListener(this)
        check_view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (mListener != null) {
            if (v === mThumbnail) {
                mListener?.onThumbnailClicked(mThumbnail, media, mPreBindInfo.mViewHolder)
            } else if (v === check_view) {
                mListener?.onCheckViewClicked(check_view, media, mPreBindInfo.mViewHolder)
            }
        }
    }

    fun preBindMedia(info: PreBindInfo) {
        mPreBindInfo = info
    }

    fun bindMedia(item: Item) {
        media = item
        setGifTag()
        initCheckView()
        setImage()
        setVideoDuration()
    }

    private fun setGifTag() {
        mGifTag.visibility = if (media.isGif()) View.VISIBLE else View.GONE
    }

    private fun initCheckView() {
        check_view.setCountable(mPreBindInfo.check_viewCountable)
    }

    fun setCheckEnabled(enabled: Boolean) {
        check_view.isEnabled = enabled
    }

    fun setCheckedNum(checkedNum: Int) {
        check_view.setCheckedNum(checkedNum)
    }

    fun setChecked(checked: Boolean) {
        check_view.setChecked(checked)
    }

    private fun setImage() {
        if (media.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifThumbnail(context, mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, media.getContentUri())
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail(context, mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, media.getContentUri())
        }
    }

    private fun setVideoDuration() {
        if (media.isVideo()) {
            mVideoDuration.visibility = View.VISIBLE
            mVideoDuration.text = DateUtils.formatElapsedTime(media.duration / 1000)
        } else {
            mVideoDuration.visibility = View.GONE
        }
    }

    fun setOnMediaGridClickListener(listener: OnMediaGridClickListener) {
        mListener = listener
    }

    fun removeOnMediaGridClickListener() {
        mListener = null
    }

    interface OnMediaGridClickListener {

        fun onThumbnailClicked(thumbnail: ImageView, item: Item, holder: RecyclerView.ViewHolder)

        fun onCheckViewClicked(checkView: CheckView, item: Item, holder: RecyclerView.ViewHolder)
    }

    class PreBindInfo(internal var mResize: Int, internal var mPlaceholder: Drawable, internal var check_viewCountable: Boolean,
                      internal var mViewHolder: RecyclerView.ViewHolder)

}
