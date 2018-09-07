package com.herve.library.matisse.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herve.library.matisse.OnAdapterItemClickListener
import com.herve.library.matisse.R
import com.herve.library.matisse.internal.entity.midea.BaseMedia
import kotlinx.android.synthetic.main.item_media.view.*

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */

class MediaAdapter(mContext: Context, var mWrapperList: MutableList<BaseMedia>) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val baseMedia = mWrapperList[position]
        with(holder.itemView) {
            tv_media_item.text = baseMedia.fileName
        }
    }

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private lateinit var mOnAdapterItemClickListener: OnAdapterItemClickListener

    override fun getItemCount(): Int {
        return mWrapperList.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }

    fun setAdapterItemClickListener(onAdapterItemClickListener: OnAdapterItemClickListener) {
        this.mOnAdapterItemClickListener = onAdapterItemClickListener;
    }



}