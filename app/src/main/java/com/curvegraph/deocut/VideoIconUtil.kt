package com.curvegraph.deocut

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.curvegraph.frameselector.BackgroundExecutor
import com.curvegraph.frameselector.ExecutorCallBack
import wseemann.media.FFmpegMediaMetadataRetriever

class VideoIconUtil {


    fun backgroundShootVideoThumb(context: Context, items: List<String>, thumbWidth: Int, callback: ExecutorCallBack<Bitmap, Int, Int>) {
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {

                for (i in 0 until items.size) {

                    try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(context, Uri.parse(items[i]))
                        var bitmap = retriever.getFrameAtTime(1000)
                        bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbWidth, false)
                        callback.onExecutorCallback(bitmap, i, 1000)
                        retriever.release()
                        // mImageViewTreeMap.put((int) frameTime,bitmap);
                    } catch (ignore: Exception) {
                        backgroundShootVideoThumbFFMPEG(context, Uri.parse(items[i]), thumbWidth, i, callback)
                    }

                }
            }
        })
    }


    fun backgroundShootVideoThumb(context: Context, uri: Uri, position: Int, thumbWidth: Int, callback: ExecutorCallBack<Bitmap, Int, Int>) {
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, uri)
                    var bitmap = retriever.getFrameAtTime(1000)
                    bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbWidth, false)
                    callback.onExecutorCallback(bitmap, position, 1000)
                    retriever.release()
                    // mImageViewTreeMap.put((int) frameTime,bitmap);
                } catch (ignore: Exception) {
                    backgroundShootVideoThumbFFMPEG(context, uri, thumbWidth, position, callback)
                }

            }
        })
    }


    private fun backgroundShootVideoThumbFFMPEG(context: Context, videoUri: Uri, thumbWidth: Int, position: Int, callback: ExecutorCallBack<Bitmap, Int, Int>) {
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {

                try {
                    val retriever = FFmpegMediaMetadataRetriever()
                    retriever.setDataSource(context, videoUri)
                    var bitmap = retriever.getFrameAtTime(1000)
                    bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbWidth, false)
                    callback.onExecutorCallback(bitmap, position, 1000)
                    retriever.release()
                    // mImageViewTreeMap.put((int) frameTime,bitmap);
                } catch (ignore: Exception) {
                }

            }
        })
    }

}