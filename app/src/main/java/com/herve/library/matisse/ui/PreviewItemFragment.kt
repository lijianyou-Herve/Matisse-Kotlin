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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.herve.library.matisse.internal.utils.PhotoMetadataUtils

import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase

class PreviewItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preview_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = arguments!!.getParcelable<Item>(ARGS_ITEM) ?: return

        val videoPlayButton = view.findViewById<View>(R.id.video_play_button)
        if (item.isVideo()) {
            videoPlayButton.visibility = View.VISIBLE
            videoPlayButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(item.getContentUri(), "video/*")
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.error_no_video_activity, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            videoPlayButton.visibility = View.GONE
        }

        val image = view.findViewById<View>(R.id.image_view) as ImageViewTouch
        image.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN

        val size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), activity!!)
        if (item.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifImage(context!!, size.x, size.y, image,
                    item.getContentUri())
        } else {
            SelectionSpec.getInstance().imageEngine.loadImage(context!!, size.x, size.y, image,
                    item.getContentUri())
        }
    }

    fun resetView() {
        if (view != null) {
            (view!!.findViewById<View>(R.id.image_view) as ImageViewTouch).resetMatrix()
        }
    }

    companion object {

        private val ARGS_ITEM = "args_item"

        fun newInstance(item: Item): PreviewItemFragment {
            val fragment = PreviewItemFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARGS_ITEM, item)
            fragment.arguments = bundle
            return fragment
        }
    }
}
