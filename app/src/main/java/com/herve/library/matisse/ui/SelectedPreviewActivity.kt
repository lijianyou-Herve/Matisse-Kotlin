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
import android.os.Bundle
import com.herve.library.matisse.internal.entity.Item
import com.herve.library.matisse.internal.entity.SelectionSpec

import com.zhihu.matisse.internal.model.SelectedItemCollection
import kotlinx.android.synthetic.main.activity_media_preview.*

class SelectedPreviewActivity : BasePreviewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val bundle = intent.getBundleExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE)
        val selected = bundle.getParcelableArrayList<Item>(SelectedItemCollection.STATE_SELECTION)
        mAdapter?.addAll(selected)
        mAdapter?.notifyDataSetChanged()
        if (mSpec.countable) {
            check_view.setCheckedNum(1)
        } else {
            check_view.setChecked(true)
        }
        mPreviousPos = 0
        updateSize(selected!![0])
    }

}
