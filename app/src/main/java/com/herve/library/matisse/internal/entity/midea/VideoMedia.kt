package com.herve.library.matisse.internal.entity.midea

/**
 * Created by Lijianyou on 2018-09-05.
 * @author  Lijianyou
 *
 */
class VideoMedia(
        fileName: String,
        filePath: String,
        val fileSize: Long,
        val mediaLength: Long)
    : BaseMedia(fileName, filePath) {


}