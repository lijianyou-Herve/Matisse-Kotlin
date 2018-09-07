package com.herve.library.matisse.listener

import android.net.Uri

/**
 * Created by Lijianyou on 2018-09-07.
 * @author  Lijianyou
 *
 */
interface OnSelectedListener {
    /**
     * @param uriList the selected item [Uri] list.
     * @param pathList the selected item file path list.
     */
    abstract fun onSelected(uriList: List<Uri>, pathList: List<String>)
}