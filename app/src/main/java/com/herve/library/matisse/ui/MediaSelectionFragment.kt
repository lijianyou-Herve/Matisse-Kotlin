package com.herve.library.matisse.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herve.library.matisse.OnAdapterItemClickListener
import com.herve.library.matisse.R
import com.herve.library.matisse.R.id.rv_media
import com.herve.library.matisse.internal.entity.midea.BaseMedia
import com.herve.library.matisse.internal.entity.midea.PhotoMedia
import com.herve.library.matisse.ui.adapter.MediaAdapter

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
class MediaSelectionFragment : Fragment() {

    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var mutableList: MutableList<BaseMedia>

    companion object {
        fun newInstance(): MediaSelectionFragment {
            return MediaSelectionFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        mutableList = mutableListOf()

        mutableList.add(PhotoMedia("图片1", "路径1", 100L, 1000L))
        mutableList.add(PhotoMedia("图片2", "路径2", 100L, 1000L))
        mutableList.add(PhotoMedia("图片3", "路径3", 100L, 1000L))

        mediaAdapter = MediaAdapter(activity!!, mutableList)

        rv_media.adapter = mediaAdapter
        rv_media.layoutManager = GridLayoutManager(activity!!, 4, RecyclerView.VERTICAL, false)
        mediaAdapter.setAdapterItemClickListener(object : OnAdapterItemClickListener {
            override fun onAdapterItemClick(holder: RecyclerView.ViewHolder, viewGroup: ViewGroup, position: Int) {

            }
        })
    }

    fun test(adapter: MediaAdapter) {

    }

    private fun initView() {
        rv_media

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}