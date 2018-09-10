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
package com.herve.library.matisse

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.*
import android.os.Build
import android.support.annotation.IntDef
import android.support.annotation.RequiresApi
import android.support.annotation.StyleRes
import com.herve.library.matisse.engine.ImageEngine
import com.herve.library.matisse.internal.MimeType
import com.herve.library.matisse.internal.entity.CaptureStrategy
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.herve.library.matisse.internal.filter.Filter
import com.herve.library.matisse.listener.OnCheckedListener
import com.herve.library.matisse.listener.OnSelectedListener
import com.zhihu.matisse.ui.MatisseActivity
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

/**
 * Fluent API for building media select specification.
 */
class SelectionCreator
/**
 * Constructs a new specification builder on the context.
 *
 * @param matisse   a requester context wrapper.
 * @param mimeTypes MIME type set to select.
 */
internal constructor(private val mMatisse: Matisse, mimeTypes: Set<MimeType>, mediaTypeExclusive: Boolean) {
    private val mSelectionSpec: SelectionSpec

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntDef(SCREEN_ORIENTATION_UNSPECIFIED, SCREEN_ORIENTATION_LANDSCAPE, SCREEN_ORIENTATION_PORTRAIT, SCREEN_ORIENTATION_USER, SCREEN_ORIENTATION_BEHIND, SCREEN_ORIENTATION_SENSOR, SCREEN_ORIENTATION_NOSENSOR, SCREEN_ORIENTATION_SENSOR_LANDSCAPE, SCREEN_ORIENTATION_SENSOR_PORTRAIT, SCREEN_ORIENTATION_REVERSE_LANDSCAPE, SCREEN_ORIENTATION_REVERSE_PORTRAIT, SCREEN_ORIENTATION_FULL_SENSOR, SCREEN_ORIENTATION_USER_LANDSCAPE, SCREEN_ORIENTATION_USER_PORTRAIT, SCREEN_ORIENTATION_FULL_USER, SCREEN_ORIENTATION_LOCKED)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class ScreenOrientation

    init {
        mSelectionSpec = SelectionSpec.getCleanInstance()
        mSelectionSpec.mimeTypeSet = mimeTypes
        mSelectionSpec.mediaTypeExclusive = mediaTypeExclusive
        mSelectionSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED
    }

    /**
     * Whether to show only one media type if choosing medias are only images or videos.
     *
     * @param showSingleMediaType whether to show only one media type, either images or videos.
     * @return [SelectionCreator] for fluent API.
     * @see SelectionSpec.onlyShowImages
     * @see SelectionSpec.onlyShowVideos
     */
    fun showSingleMediaType(showSingleMediaType: Boolean): SelectionCreator {
        mSelectionSpec.showSingleMediaType = showSingleMediaType
        return this
    }

    /**
     * Theme for media selecting Activity.
     *
     *
     * There are two built-in themes:
     * 1. com.zhihu.matisse.R.style.Matisse_Zhihu;
     * 2. com.zhihu.matisse.R.style.Matisse_Dracula
     * you can define a custom theme derived from the above ones or other themes.
     *
     * @param themeId theme resource id. Default value is com.zhihu.matisse.R.style.Matisse_Zhihu.
     * @return [SelectionCreator] for fluent API.
     */
    fun theme(@StyleRes themeId: Int): SelectionCreator {
        mSelectionSpec.themeId = themeId
        return this
    }

    /**
     * Show a auto-increased number or a check mark when user select media.
     *
     * @param countable true for a auto-increased number from 1, false for a check mark. Default
     * value is false.
     * @return [SelectionCreator] for fluent API.
     */
    fun countable(countable: Boolean): SelectionCreator {
        mSelectionSpec.countable = countable
        return this
    }

    /**
     * Maximum selectable count.
     *
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return [SelectionCreator] for fluent API.
     */
    fun maxSelectable(maxSelectable: Int): SelectionCreator {
        if (maxSelectable < 1)
            throw IllegalArgumentException("maxSelectable must be greater than or equal to one")
        if (mSelectionSpec.maxImageSelectable > 0 || mSelectionSpec.maxVideoSelectable > 0)
            throw IllegalStateException("already set maxImageSelectable and maxVideoSelectable")
        mSelectionSpec.maxSelectable = maxSelectable
        return this
    }

    /**
     * Only useful when [SelectionSpec.mediaTypeExclusive] set true and you want to set different maximum
     * selectable files for image and video media types.
     *
     * @param maxImageSelectable Maximum selectable count for image.
     * @param maxVideoSelectable Maximum selectable count for video.
     * @return
     */
    fun maxSelectablePerMediaType(maxImageSelectable: Int, maxVideoSelectable: Int): SelectionCreator {
        if (maxImageSelectable < 1 || maxVideoSelectable < 1)
            throw IllegalArgumentException("max selectable must be greater than or equal to one")
        mSelectionSpec.maxSelectable = -1
        mSelectionSpec.maxImageSelectable = maxImageSelectable
        mSelectionSpec.maxVideoSelectable = maxVideoSelectable
        return this
    }

    /**
     * Add filter to filter each selecting item.
     *
     * @param filter [Filter]
     * @return [SelectionCreator] for fluent API.
     */
    fun addFilter(filter: Filter): SelectionCreator {
        if (mSelectionSpec.filters == null) {
            mSelectionSpec.filters = ArrayList()
        }
        if (filter == null) throw IllegalArgumentException("filter cannot be null")
        (mSelectionSpec.filters!! as ArrayList).add(filter)
        return this
    }

    /**
     * Determines whether the photo capturing is enabled or not on the media grid view.
     *
     *
     * If this value is set true, photo capturing entry will appear only on All Media's page.
     *
     * @param enable Whether to enable capturing or not. Default value is false;
     * @return [SelectionCreator] for fluent API.
     */
    fun capture(enable: Boolean): SelectionCreator {
        mSelectionSpec.capture = enable
        return this
    }

    /**
     * Show a original photo check options.Let users decide whether use original photo after select
     *
     * @param enable Whether to enable original photo or not
     * @return [SelectionCreator] for fluent API.
     */
    fun originalEnable(enable: Boolean): SelectionCreator {
        mSelectionSpec.originalable = enable
        return this
    }

    /**
     * Maximum original size,the unit is MB. Only useful when {link@originalEnable} set true
     *
     * @param size Maximum original size. Default value is Integer.MAX_VALUE
     * @return [SelectionCreator] for fluent API.
     */
    fun maxOriginalSize(size: Int): SelectionCreator {
        mSelectionSpec.originalMaxSize = size
        return this
    }

    /**
     * Capture strategy provided for the location to save photos including internal and external
     * storage and also a authority for [android.support.v4.content.FileProvider].
     *
     * @param captureStrategy [CaptureStrategy], needed only when capturing is enabled.
     * @return [SelectionCreator] for fluent API.
     */
    fun captureStrategy(captureStrategy: CaptureStrategy): SelectionCreator {
        mSelectionSpec.captureStrategy = captureStrategy
        return this
    }

    /**
     * Set the desired orientation of this activity.
     *
     * @param orientation An orientation constant as used in [ScreenOrientation].
     * Default value is [android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT].
     * @return [SelectionCreator] for fluent API.
     * @see Activity.setRequestedOrientation
     */
    fun restrictOrientation(@ScreenOrientation orientation: Int): SelectionCreator {
        mSelectionSpec.orientation = orientation
        return this
    }

    /**
     * Set a fixed span count for the media grid. Same for different screen orientations.
     *
     *
     * This will be ignored when [.gridExpectedSize] is set.
     *
     * @param spanCount Requested span count.
     * @return [SelectionCreator] for fluent API.
     */
    fun spanCount(spanCount: Int): SelectionCreator {
        if (spanCount < 1) throw IllegalArgumentException("spanCount cannot be less than 1")
        mSelectionSpec.spanCount = spanCount
        return this
    }

    /**
     * Set expected size for media grid to adapt to different screen sizes. This won't necessarily
     * be applied cause the media grid should fill the view container. The measured media grid's
     * size will be as close to this value as possible.
     *
     * @param size Expected media grid size in pixel.
     * @return [SelectionCreator] for fluent API.
     */
    fun gridExpectedSize(size: Int): SelectionCreator {
        mSelectionSpec.gridExpectedSize = size
        return this
    }

    /**
     * Photo thumbnail's scale compared to the View's size. It should be a float value in (0.0,
     * 1.0].
     *
     * @param scale Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return [SelectionCreator] for fluent API.
     */
    fun thumbnailScale(scale: Float): SelectionCreator {
        if (scale <= 0f || scale > 1f)
            throw IllegalArgumentException("Thumbnail scale must be between (0.0, 1.0]")
        mSelectionSpec.thumbnailScale = scale
        return this
    }

    /**
     * Provide an image engine.
     *
     *
     * There are two built-in image engines:
     * 1. [com.zhihu.matisse.engine.impl.GlideEngine]
     * 2. [com.zhihu.matisse.engine.impl.PicassoEngine]
     * And you can implement your own image engine.
     *
     * @param imageEngine [ImageEngine]
     * @return [SelectionCreator] for fluent API.
     */
    fun imageEngine(imageEngine: ImageEngine): SelectionCreator {
        mSelectionSpec.imageEngine = imageEngine
        return this
    }

    /**
     * Set listener for callback immediately when user select or unselect something.
     *
     *
     * It's a redundant API with [Matisse.obtainResult],
     * we only suggest you to use this API when you need to do something immediately.
     *
     * @param listener [OnSelectedListener]
     * @return [SelectionCreator] for fluent API.
     */
    fun setOnSelectedListener(listener: OnSelectedListener?): SelectionCreator {
        mSelectionSpec.onSelectedListener = listener
        return this
    }

    /**
     * Set listener for callback immediately when user check or uncheck original.
     *
     * @param listener [OnSelectedListener]
     * @return [SelectionCreator] for fluent API.
     */
    fun setOnCheckedListener(listener: OnCheckedListener?): SelectionCreator {
        mSelectionSpec.onCheckedListener = listener
        return this
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    fun forResult(requestCode: Int) {
        val activity = mMatisse.activity ?: return

        val intent = Intent(activity, MatisseActivity::class.java)

        val fragment = mMatisse.fragment
        if (fragment != null) {
            fragment!!.startActivityForResult(intent, requestCode)
        } else {
            activity!!.startActivityForResult(intent, requestCode)
        }
    }

}
