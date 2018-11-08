package com.curvegraph.trimmer.home

import android.content.Context

interface HomeContract {

    /** Represents the View in MVP.  */
    interface View {

        fun showFoldersList(items: List<ModelFolder>)

    }


    interface Presenter {

        fun getVideoFoldersAndFiles(c : Context)

        fun dispose()

        fun itemClick(position: Int, context: Context)
    }

}