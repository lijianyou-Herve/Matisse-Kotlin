package com.herve.library.matisse.internal.entity

import android.content.pm.ActivityInfo
import android.support.annotation.StyleRes
import com.herve.library.matisse.R
import com.herve.library.matisse.engine.ImageEngine
import com.herve.library.matisse.engine.impl.GlideEngine
import com.herve.library.matisse.internal.MimeType
import com.herve.library.matisse.internal.filter.Filter
import com.herve.library.matisse.internal.ofImage
import com.herve.library.matisse.internal.ofVideo
import com.herve.library.matisse.listener.OnCheckedListener
import com.herve.library.matisse.listener.OnSelectedListener

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */
class SelectionSpec {
    var mimeTypeSet: Set<MimeType>? = null
    var mediaTypeExclusive: Boolean = false
    var showSingleMediaType: Boolean = false
    @StyleRes
    var themeId: Int = 0
    var orientation: Int = 0
    var countable: Boolean = false
    var maxSelectable: Int = 0
    var maxImageSelectable: Int = 0
    var maxVideoSelectable: Int = 0
    var filters: List<Filter>? = null
    var capture: Boolean = false
    var captureStrategy: CaptureStrategy? = null
    var spanCount: Int = 0
    var gridExpectedSize: Int = 0
    var thumbnailScale: Float = 0.toFloat()
    lateinit var imageEngine: ImageEngine
    var hasInited: Boolean = false
    var onSelectedListener: OnSelectedListener? = null
    var originalable: Boolean = false
    var originalMaxSize: Int = 0
    var onCheckedListener: OnCheckedListener? = null

    companion object {

        fun getInstance(): SelectionSpec {
            return InstanceHolder.INSTANCE
        }

        fun getCleanInstance(): SelectionSpec {
            val selectionSpec = getInstance()
            selectionSpec.reset()
            return selectionSpec
        }
    }

    private constructor()



    private fun reset() {
        mimeTypeSet = null
        mediaTypeExclusive = true
        showSingleMediaType = false
        themeId = R.style.AppTheme
        orientation = 0
        countable = false
        maxSelectable = 1
        maxImageSelectable = 0
        maxVideoSelectable = 0
        filters = null
        capture = false
        captureStrategy = null
        spanCount = 3
        gridExpectedSize = 0
        thumbnailScale = 0.5f
        imageEngine = GlideEngine()
        hasInited = true
        originalable = false
        originalMaxSize = Integer.MAX_VALUE
    }

    fun singleSelectionModeEnabled(): Boolean {
        return !countable && (maxSelectable == 1 || maxImageSelectable == 1 && maxVideoSelectable == 1)
    }

    fun needOrientationRestriction(): Boolean {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    fun onlyShowImages(): Boolean {
        return showSingleMediaType && ofImage().containsAll(mimeTypeSet!!)
    }

    fun onlyShowVideos(): Boolean {
        return showSingleMediaType && ofVideo().containsAll(mimeTypeSet!!)
    }

    private object InstanceHolder {
        val INSTANCE = SelectionSpec()
    }
}