package com.herve.library.matisse.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.herve.library.matisse.OnAdapterItemClickListener
import com.herve.library.matisse.R
import com.herve.library.matisse.adapter.MediaAdapter

class MatisseActivity : AppCompatActivity() {

    private lateinit var mediaAdapter: MediaAdapter

    /**
     * 启动本界面
     * */
    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MatisseActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matisse)

        mediaAdapter = MediaAdapter()
        mediaAdapter.setAdapterItemClickListener(object : OnAdapterItemClickListener {
            override fun onAdapterItemClick() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
