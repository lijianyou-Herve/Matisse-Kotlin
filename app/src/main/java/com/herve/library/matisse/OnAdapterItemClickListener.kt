package com.herve.library.matisse

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface OnAdapterItemClickListener {
    fun onAdapterItemClick(holder: RecyclerView.ViewHolder, viewGroup: ViewGroup, position: Int)
    }