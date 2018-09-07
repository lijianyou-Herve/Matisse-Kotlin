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
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.entity.IncapableCause
import com.herve.library.matisse.internal.entity.SelectionSpec
import com.herve.library.matisse.internal.utils.PhotoMetadataUtils
import com.zhihu.matisse.internal.model.SelectedItemCollection
import com.zhihu.matisse.internal.ui.adapter.PreviewPagerAdapter
import com.zhihu.matisse.internal.ui.widget.CheckRadioView
import com.zhihu.matisse.internal.ui.widget.CheckView
import com.zhihu.matisse.internal.ui.widget.IncapableDialog

import com.zhihu.matisse.internal.utils.Platform

abstract class BasePreviewActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    protected val mSelectedCollection = SelectedItemCollection(this)
    protected var mSpec: SelectionSpec
    protected var mPager: ViewPager

    protected var mAdapter: PreviewPagerAdapter

    protected var mCheckView: CheckView
    protected var mButtonBack: TextView
    protected var mButtonApply: TextView
    protected var mSize: TextView

    protected var mPreviousPos = -1

    private var mOriginalLayout: LinearLayout? = null
    private var mOriginal: CheckRadioView? = null
    protected var mOriginalEnable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(SelectionSpec.getInstance().themeId)
        super.onCreate(savedInstanceState)
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        setContentView(R.layout.activity_media_preview)
        if (Platform.hasKitKat()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        mSpec = SelectionSpec.getInstance()
        if (mSpec.needOrientationRestriction()) {
            requestedOrientation = mSpec.orientation
        }

        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(intent.getBundleExtra(EXTRA_DEFAULT_BUNDLE))
            mOriginalEnable = intent.getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false)
        } else {
            mSelectedCollection.onCreate(savedInstanceState)
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE)
        }
        mButtonBack = findViewById<View>(R.id.button_back) as TextView
        mButtonApply = findViewById<View>(R.id.button_apply) as TextView
        mSize = findViewById<View>(R.id.size) as TextView
        mButtonBack.setOnClickListener(this)
        mButtonApply.setOnClickListener(this)

        mPager = findViewById<View>(R.id.pager) as ViewPager
        mPager.addOnPageChangeListener(this)
        mAdapter = PreviewPagerAdapter(supportFragmentManager, null)
        mPager.adapter = mAdapter
        mCheckView = findViewById<View>(R.id.check_view) as CheckView
        mCheckView.setCountable(mSpec.countable)

        mCheckView.setOnClickListener {
            val item = mAdapter.getMediaItem(mPager.currentItem)
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item)
                if (mSpec.countable) {
                    mCheckView.setCheckedNum(CheckView.UNCHECKED)
                } else {
                    mCheckView.setChecked(false)
                }
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item)
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item))
                    } else {
                        mCheckView.setChecked(true)
                    }
                }
            }
            updateApplyButton()

            if (mSpec.onSelectedListener != null) {
                mSpec.onSelectedListener.onSelected(
                        mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString())
            }
        }


        mOriginalLayout = findViewById(R.id.originalLayout)
        mOriginal = findViewById(R.id.original)
        mOriginalLayout!!.setOnClickListener(View.OnClickListener {
            val count = countOverMaxSize()
            if (count > 0) {
                val incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_count, count, mSpec.originalMaxSize))
                incapableDialog.show(supportFragmentManager,
                        IncapableDialog::class.java!!.getName())
                return@OnClickListener
            }

            mOriginalEnable = !mOriginalEnable
            mOriginal!!.setChecked(mOriginalEnable)
            if (!mOriginalEnable) {
                mOriginal!!.setColor(Color.WHITE)
            }


            if (mSpec.onCheckedListener != null) {
                mSpec.onCheckedListener.onCheck(mOriginalEnable)
            }
        })

        updateApplyButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mSelectedCollection.onSaveInstanceState(outState)
        outState.putBoolean("checkState", mOriginalEnable)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        sendBackResult(false)
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_back) {
            onBackPressed()
        } else if (v.id == R.id.button_apply) {
            sendBackResult(true)
            finish()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        val adapter = mPager.adapter as PreviewPagerAdapter?
        if (mPreviousPos != -1 && mPreviousPos != position) {
            (adapter!!.instantiateItem(mPager, mPreviousPos) as PreviewItemFragment).resetView()

            val item = adapter.getMediaItem(position)
            if (mSpec.countable) {
                val checkedNum = mSelectedCollection.checkedNumOf(item)
                mCheckView.setCheckedNum(checkedNum)
                if (checkedNum > 0) {
                    mCheckView.isEnabled = true
                } else {
                    mCheckView.isEnabled = !mSelectedCollection.maxSelectableReached()
                }
            } else {
                val checked = mSelectedCollection.isSelected(item)
                mCheckView.setChecked(checked)
                if (checked) {
                    mCheckView.isEnabled = true
                } else {
                    mCheckView.isEnabled = !mSelectedCollection.maxSelectableReached()
                }
            }
            updateSize(item)
        }
        mPreviousPos = position
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun updateApplyButton() {
        val selectedCount = mSelectedCollection.count()
        if (selectedCount == 0) {
            mButtonApply.setText(R.string.button_sure_default)
            mButtonApply.isEnabled = false
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonApply.setText(R.string.button_sure_default)
            mButtonApply.isEnabled = true
        } else {
            mButtonApply.isEnabled = true
            mButtonApply.text = getString(R.string.button_sure, selectedCount)
        }

        if (mSpec.originalable) {
            mOriginalLayout!!.visibility = View.VISIBLE
            updateOriginalState()
        } else {
            mOriginalLayout!!.visibility = View.GONE
        }
    }


    private fun updateOriginalState() {
        mOriginal!!.setChecked(mOriginalEnable)
        if (!mOriginalEnable) {
            mOriginal!!.setColor(Color.WHITE)
        }

        if (countOverMaxSize() > 0) {

            if (mOriginalEnable) {
                val incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_size, mSpec.originalMaxSize))
                incapableDialog.show(supportFragmentManager,
                        IncapableDialog::class.java!!.getName())

                mOriginal!!.setChecked(false)
                mOriginal!!.setColor(Color.WHITE)
                mOriginalEnable = false
            }
        }
    }


    private fun countOverMaxSize(): Int {
        var count = 0
        val selectedCount = mSelectedCollection.count()
        for (i in 0 until selectedCount) {
            val item = mSelectedCollection.asList()[i]
            if (item.isImage) {
                val size = PhotoMetadataUtils.getSizeInMB(item.size)
                if (size > mSpec.originalMaxSize) {
                    count++
                }
            }
        }
        return count
    }

    protected fun updateSize(item: Item) {
        if (item.isGif) {
            mSize.visibility = View.VISIBLE
            mSize.text = PhotoMetadataUtils.getSizeInMB(item.size).toString() + "M"
        } else {
            mSize.visibility = View.GONE
        }

        if (item.isVideo) {
            mOriginalLayout!!.visibility = View.GONE
        } else if (mSpec.originalable) {
            mOriginalLayout!!.visibility = View.VISIBLE
        }
    }

    protected fun sendBackResult(apply: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.dataWithBundle)
        intent.putExtra(EXTRA_RESULT_APPLY, apply)
        intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun assertAddSelection(item: Item): Boolean {
        val cause = mSelectedCollection.isAcceptable(item)
        IncapableCause.handleCause(this, cause)
        return cause == null
    }

    companion object {

        val EXTRA_DEFAULT_BUNDLE = "extra_default_bundle"
        val EXTRA_RESULT_BUNDLE = "extra_result_bundle"
        val EXTRA_RESULT_APPLY = "extra_result_apply"
        val EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable"
        val CHECK_STATE = "checkState"
    }
}
