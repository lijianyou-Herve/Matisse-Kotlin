package com.herve.library.matisse.internal

import android.content.ContentResolver
import android.net.Uri
import android.support.v4.util.ArraySet
import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.herve.library.matisse.internal.utils.PhotoMetadataUtils
import java.util.*

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */
enum class MimeType {


    // ============== images ==============

    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),

    PNG("image/png", arraySetOf(
            "png"
    )),

    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("video/mpeg", arraySetOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", arraySetOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("video/quicktime", arraySetOf(
            "mov"
    )),
    THREEGPP("video/3gpp", arraySetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", arraySetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", arraySetOf(
            "mkv"
    )),
    WEBM("video/webm", arraySetOf(
            "webm"
    )),
    TS("video/mp2ts", arraySetOf(
            "ts"
    )),
    AVI("video/avi", arraySetOf(
            "avi"
    ));

    private var mimeTypeName: String
    private var extensions: Set<String>

    constructor(mimeTypeName: String, extensions: Set<String>) {
        this.mimeTypeName = mimeTypeName
        this.extensions = extensions
    }

    override fun toString(): String {
        return mimeTypeName
    }

    fun checkType(resolver: ContentResolver, uri: Uri?): Boolean {
        val map = MimeTypeMap.getSingleton()
        if (uri == null) {
            return false
        }
        val type = map.getExtensionFromMimeType(resolver.getType(uri))
        var path: String? = null
        // lazy load the path and prevent resolve for multiple times
        var pathParsed = false
        for (extension in extensions) {
            if (extension == type) {
                return true
            }
            if (!pathParsed) {
                // we only resolve the path for one time
                path = PhotoMetadataUtils.getPath(resolver, uri)
                if (!TextUtils.isEmpty(path)) {
                    path = path!!.toLowerCase(Locale.US)
                }
                pathParsed = true
            }
            if (path != null && path.endsWith(extension)) {
                return true
            }
        }
        return false
    }
}

fun ofAll(): Set<MimeType> {
    return EnumSet.allOf(MimeType::class.java)
}

fun of(type: MimeType, vararg rest: MimeType): Set<MimeType> {
    return EnumSet.of(type, *rest)
}

fun ofImage(): Set<MimeType> {
    return EnumSet.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF, MimeType.BMP, MimeType.WEBP)
}

fun ofVideo(): Set<MimeType> {
    return EnumSet.of(MimeType.MPEG, MimeType.MP4, MimeType.QUICKTIME, MimeType.THREEGPP, MimeType.THREEGPP2, MimeType.MKV, MimeType.WEBM, MimeType.TS, MimeType.AVI)
}

fun arraySetOf(vararg suffixes: String): Set<String> {
    return ArraySet(Arrays.asList(*suffixes))
}

