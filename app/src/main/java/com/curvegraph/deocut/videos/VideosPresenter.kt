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
package com.curvegraph.deocut.videos

import android.content.Context
import android.content.Intent
import com.curvegraph.deocut.home.ModelFolder
import com.curvegraph.deocut.player.VideoActivity
import com.curvegraph.deocut.utils.CommonObject.intentVideo
import com.curvegraph.deocut.utils.CommonObject.intentVideosList
import io.reactivex.disposables.Disposable

class VideosPresenter(var view: VideosContract.View) : VideosContract.Presenter {

    private var disposable: Disposable? = null
    override fun dispose() {
        disposable?.dispose()
    }

private lateinit var folder : ModelFolder
    override fun itemClick(position: Int, context: Context) {
        val intent = Intent(context, VideoActivity::class.java)
        intent.putExtra(intentVideosList, folder)
        intent.putExtra(intentVideo, folder.videoFiles[position])
        context.startActivity(intent)

    }


    override fun getVideoFoldersAndFiles( c : VideosActivity) {
        folder =  c.intent.getParcelableExtra<ModelFolder>(intentVideosList)
        view.title(folder.folderNameDisplay)
        view.showFoldersList(folder.videoFiles)
        //   println("media hashset External storage: ${Environment.getExternalStorageDirectory().absolutePath}")

    }


}