package com.herve.library.matisse.internal.entity

import android.content.Context
import android.support.annotation.IntDef
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.zhihu.matisse.internal.ui.widget.IncapableDialog
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

        fun handleCause(context: Context, cause: IncapableCause?) {
            if (cause == null)
                return

            when (cause.mForm) {
                NONE -> {
                }
                DIALOG -> {
                    val incapableDialog = IncapableDialog.newInstance(cause.mTitle!!, cause.mMessage!!)
                    incapableDialog.show((context as FragmentActivity).supportFragmentManager,
                            IncapableDialog::class.java.name)
                }
                TOAST -> Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show()
            }// do nothing.
        }
    }

    @Retention(SOURCE)
    @IntDef(TOAST, DIALOG, NONE)
    annotation class Form

    var mForm = TOAST
    var mTitle: String? = null
    var mMessage: String? = null

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