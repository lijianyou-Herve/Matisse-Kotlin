package com.herve.library.matisse.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.herve.library.matisse.OnAdapterItemClickListener

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */

class CustomAdapter : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private lateinit var mOnAdapterItemClickListener: OnAdapterItemClickListener
    private val mWrapperList: MutableList<ItemWrapper> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }

    fun setAdapterItemClickListener(onAdapterItemClickListener: OnAdapterItemClickListener) {
        this.mOnAdapterItemClickListener = onAdapterItemClickListener;
    }



}