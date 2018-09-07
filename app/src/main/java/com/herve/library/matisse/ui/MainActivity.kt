package com.herve.library.matisse.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.herve.library.matisse.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_media_store.setOnClickListener {
            MatisseActivity.launch(this)

        }
    }
}
