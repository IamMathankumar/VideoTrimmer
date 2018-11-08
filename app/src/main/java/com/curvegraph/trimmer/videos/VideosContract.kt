package com.curvegraph.trimmer.videos

import android.content.Context

interface VideosContract {

    /** Represents the View in MVP.  */
    interface View {
        fun showFoldersList(items: List<String>)
    }


    interface Presenter {

        fun getVideoFoldersAndFiles(c : VideosActivity)

        fun dispose()

        fun itemClick(position: Int, context: Context)
    }

}