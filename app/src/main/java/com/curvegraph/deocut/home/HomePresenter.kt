/*
 *
 *  Copyright 2018 Mathankumar K. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.curvegraph.deocut.home

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import com.curvegraph.deocut.player.VideoActivity
import com.curvegraph.deocut.utils.CommonObject
import com.curvegraph.deocut.utils.CommonObject.intentVideosList
import com.curvegraph.deocut.videos.VideosActivity
import io.reactivex.disposables.Disposable
import java.io.File
import java.util.HashSet
import kotlin.collections.ArrayList
import java.nio.file.Files.isDirectory



class HomePresenter(var view: HomeContract.View) : HomeContract.Presenter {
    override fun searchItemClick(video: String, context: Context) {
        val intent = Intent(context, VideoActivity::class.java)
        intent.putExtra(CommonObject.intentVideo, video)
        context.startActivity(intent)
    }

    override fun searchText(searchText: String) {
        if (searchText.length > 1) {
// to get the result as list
            val filteredList = mediaItems.filter { s -> s.contains(searchText, true) }
            if (filteredList.isNotEmpty())
                view.showSearchList(filteredList)
            else {
                view.hideSearchListView()
            }
        } else {
            view.hideSearchListView()
        }
    }

    private var disposable: Disposable? = null
    override fun dispose() {
        disposable?.dispose()
    }

    private var items: List<ModelFolder> = ArrayList()
    private var mediaItems: ArrayList<String> = ArrayList()


    override fun itemClick(position: Int, context: Context) {
        val intent = Intent(context, VideosActivity::class.java)
        intent.putExtra(intentVideosList, items[position])
        context.startActivity(intent)
        //Do something after 100ms
    }


    private fun allMediaFromDevice(c: Context): ArrayList<String> {
        val videoItemHashSet = HashSet<String>()
        val projection = arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
        val cursor = c.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        try {
            cursor!!.moveToFirst()
            do {
                videoItemHashSet.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
            } while (cursor.moveToNext())

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return java.util.ArrayList(videoItemHashSet)
    }


    override fun getVideoFoldersAndFiles(c: Context) {
        val items: ArrayList<ModelFolder> = ArrayList()
        mediaItems = allMediaFromDevice(c)
        val mediaFiles: ArrayList<String> = ArrayList()
        mediaFiles.addAll(mediaItems.toMutableList())
        val parentPath = Environment.getExternalStorageDirectory().absolutePath
        val videoItemHashSet = HashSet<String>()
        for (day in mediaFiles) {
            val index = day.lastIndexOf("/")
            val folder = day.substring(0, index)
            videoItemHashSet.add(folder)
        }

        //This call is to order the list based on size nested folders in a file path, we used contains option to get the folder list and
        // to get the folder files if we didn't order file path based on most nested folders files, contains will not give expected result
        val orderList: java.util.ArrayList<ModelFolderNested> = java.util.ArrayList()
        for (day in videoItemHashSet) {
            orderList.add(ModelFolderNested(day, (day.split("/")).size))
        }
        val sortedList = orderList.sortedWith(compareByDescending { it.nestedCount })
        for (d in sortedList) {
            val day = d.folderName
            //  println("media hashset: $day")
            val folderSplit = day.replace(parentPath, "")
            var displayName: String
            var folderName = ""
            if (folderSplit.isBlank()) {
                displayName = "Base folder"
            } else {
                displayName = day.substring(day.lastIndexOf("/") + 1)
                folderName = day.substring(day.lastIndexOf("/") + 1)
            }
            val files: List<String> = mediaFiles.filter { it.contains(day.plus("/")) }
            items.add(ModelFolder(displayName, folderName, day, files.size, files))
            mediaFiles.removeAll(files)
            this.items = items.sortedWith(compareBy { it.folderName.toLowerCase() })
        }

        view.showFoldersList(this.items)
        //   println("media hashset External storage: ${Environment.getExternalStorageDirectory().absolutePath}")

    }


}