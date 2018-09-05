package com.herve.library.matisse.internal.entity

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.SOURCE

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
class IncapableCause {

    companion object {
        const val TOAST = 0x00
        const val DIALOG = 0x01
        const val NONE = 0x02
    }

    @Retention(SOURCE)
    @IntDef(TOAST, DIALOG, NONE)
    annotation class Form

    private var mForm = TOAST
    private var mTitle: String? = null
    private var mMessage: String? = null

    constructor(message: String) {
        mMessage = message
    }

    constructor(title: String, message: String) {
        mTitle = title
        mMessage = message
    }

    constructor(@Form form: Int, message: String) {
        mForm = form
        mMessage = message
    }

    constructor(@Form form: Int, title: String, message: String) {
        mForm = form
        mTitle = title
        mMessage = message
    }
}