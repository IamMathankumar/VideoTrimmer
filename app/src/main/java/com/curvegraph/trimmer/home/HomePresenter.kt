package com.curvegraph.trimmer.home

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import com.curvegraph.trimmer.utils.CommonObject.intentVideosList
import com.curvegraph.trimmer.videos.VideosActivity
import io.reactivex.disposables.Disposable
import java.util.HashSet
import kotlin.collections.ArrayList

class HomePresenter(var view: HomeContract.View) : HomeContract.Presenter {

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


     private fun allMediaFromDevice(c : Context): ArrayList<String>
      {
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

    override fun getVideoFoldersAndFiles( c : Context) {
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
        val orderList : java.util.ArrayList<ModelFolderNested> = java.util.ArrayList()
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