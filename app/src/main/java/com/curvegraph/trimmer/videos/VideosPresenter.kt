package com.curvegraph.trimmer.videos

import android.content.Context
import android.content.Intent
import com.curvegraph.trimmer.home.ModelFolder
import com.curvegraph.trimmer.player.VideoActivity
import com.curvegraph.trimmer.utils.CommonObject.intentVideo
import com.curvegraph.trimmer.utils.CommonObject.intentVideosList
import io.reactivex.disposables.Disposable
import kotlin.collections.ArrayList

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
        view.showFoldersList(folder.videoFiles)
        //   println("media hashset External storage: ${Environment.getExternalStorageDirectory().absolutePath}")

    }


}