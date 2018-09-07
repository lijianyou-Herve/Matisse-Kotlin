package com.herve.library.matisse.engine.impl

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.herve.library.matisse.engine.ImageEngine
import com.squareup.picasso.Picasso

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */
class PicassoEngine : ImageEngine {

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        Picasso.with(context).load(uri).placeholder(placeholder)
                .resize(resize, resize)
                .centerCrop()
                .into(imageView)
    }

    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView,
                                  uri: Uri) {
        loadThumbnail(context, resize, placeholder, imageView, uri)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Picasso.with(context).load(uri).resize(resizeX, resizeY).priority(Picasso.Priority.HIGH)
                .centerInside().into(imageView)
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        loadImage(context, resizeX, resizeY, imageView, uri)
    }

    override fun supportAnimatedGif(): Boolean {
        return false
    }
}