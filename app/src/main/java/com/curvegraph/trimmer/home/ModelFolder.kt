package com.curvegraph.trimmer.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelFolder(
        val folderNameDisplay: String,
        val folderName: String,
        val folderPath: String,
        val videosCount: Int, // 0
        val videoFiles: List<String> = ArrayList()
) : Parcelable