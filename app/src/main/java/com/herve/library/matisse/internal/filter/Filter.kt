package com.herve.library.matisse.internal.filter

import android.content.Context
import com.herve.library.matisse.internal.MimeType
import com.herve.library.matisse.internal.entity.IncapableCause
import com.herve.library.matisse.internal.entity.Item

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */
abstract class Filter {
    /**
     * Convenient constant for a minimum value.
     */
    val MIN = 0
    /**
     * Convenient constant for a maximum value.
     */
    val MAX = Integer.MAX_VALUE
    /**
     * Convenient constant for 1024.
     */
    val K = 1024

    /**
     * Against what mime types this filter applies.
     */
    protected abstract fun constraintTypes(): Set<MimeType>

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, [IncapableCause] if not selectable.
     */
    public abstract fun filter(context: Context, item: Item): IncapableCause

    /**
     * Whether an [Item] need filtering.
     */
    protected fun needFiltering(context: Context, item: Item): Boolean {
        for (type in constraintTypes()) {
            if (type.checkType(context.contentResolver, item.getContentUri())) {
                return true
            }
        }
        return false
    }
}