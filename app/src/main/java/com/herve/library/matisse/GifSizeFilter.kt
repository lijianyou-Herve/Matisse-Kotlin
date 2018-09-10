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
package com.zhihu.matisse.sample

import android.content.Context
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.MimeType
import com.herve.library.matisse.internal.entity.IncapableCause
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.filter.Filter
import com.herve.library.matisse.internal.utils.PhotoMetadataUtils
import java.util.*

internal class GifSizeFilter(private val mMinWidth: Int, private val mMinHeight: Int, private val mMaxSize: Int) : Filter() {

    public override fun constraintTypes(): Set<MimeType> {
        return object : HashSet<MimeType>() {
            init {
                add(MimeType.GIF)
            }
        }
    }

    override fun filter(context: Context, item: Item): IncapableCause {
        if (!needFiltering(context, item))
            return null!!
        val size = PhotoMetadataUtils.getBitmapBound(context.contentResolver, item.getContentUri())
        return if (size.x < mMinWidth || size.y < mMinHeight || item.size > mMaxSize) {
            IncapableCause(IncapableCause.DIALOG, context.getString(R.string.error_gif, mMinWidth,
                    PhotoMetadataUtils.getSizeInMB(mMaxSize.toLong()).toString()))
        } else null!!
    }

}
