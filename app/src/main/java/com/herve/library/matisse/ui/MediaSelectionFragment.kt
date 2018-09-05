package com.herve.library.matisse.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herve.library.matisse.R
import com.herve.library.matisse.adapter.MediaAdapter
import com.herve.library.matisse.internal.entity.Album
import kotlinx.android.synthetic.main.fragment_media_selection.*

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
class MediaSelectionFragment : Fragment() {

    private val mediaAdapter: MediaAdapter? = null

    fun newInstance(album: Album) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        initView();

        return inflater.inflate(R.layout.fragment_media_selection, container, false);
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