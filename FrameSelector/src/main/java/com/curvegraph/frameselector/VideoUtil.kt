package com.curvegraph.frameselector

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import wseemann.media.FFmpegMediaMetadataRetriever

class VideoUtil {


    fun backgroundShootVideoThumb(context: Context, videoUri: Uri, thumbWidth: Int, totalThumbsCount: Int,
                                  singleLengthToGetBitmap: Long, callback: ExecutorCallBack<Bitmap, Int, Int>) {
        //((BaseActivity)context).showProgress(context.getString(R.string.getting_video_frames));
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {
                try {

                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(context, videoUri)
                    // Retrieve media data use microsecond
                    //long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    var frameTime = 0L
                    /*if(mImageViewTreeMap.size()>0){
                        mImageViewTreeMap.clear();
                    }*/
                    for (i in 0 until totalThumbsCount) {
                        //long frameTime = startPosition + interval * i;
                        frameTime += singleLengthToGetBitmap

                        var bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbWidth, false)
                            // mImageViewTreeMap.put((int) frameTime,bitmap);
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }

                        callback.onExecutorCallback(bitmap, i, frameTime.toInt())
                        // if(DEBUG_MODE)Log.e("FrameTime:",frameTime+"");
                    }
                    mediaMetadataRetriever.release()
                    // ((BaseActivity)context).hideProgress();
                } catch (e: Throwable) {
                    backgroundShootVideoThumbFFMPEG(context, videoUri, totalThumbsCount, thumbWidth, 0, singleLengthToGetBitmap, callback)
                    //   Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }

            }
        })
    }


    private fun backgroundShootVideoThumbFFMPEG(context: Context, videoUri: Uri, thumbWidth: Int, totalThumbsCount: Int, startPosition: Long,
                                                singleLengthToGetBitmap: Long, callback: ExecutorCallBack<Bitmap, Int, Int>) {
        //((BaseActivity)context).showProgress(context.getString(R.string.getting_video_frames));
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {
                try {
                    //                     backgroundShootVideoThumbFFMPEG(context, videoUri, totalThumbsCount, startPosition, endPosition, callback);
                    val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(context, videoUri)
                    // Retrieve media data use microsecond
                    //long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    var frameTime = startPosition
                    /*if(mImageViewTreeMap.size()>0){
                        mImageViewTreeMap.clear();
                    }*/
                    for (i in 0 until totalThumbsCount) {
                        //long frameTime = startPosition + interval * i;

                        var bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbWidth, false)
                            // mImageViewTreeMap.put((int) frameTime,bitmap);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        callback.onExecutorCallback(bitmap, i, frameTime.toInt())
                        frameTime += singleLengthToGetBitmap
                        // if(DEBUG_MODE)Log.e("FrameTime:",frameTime+"");
                    }
                    mediaMetadataRetriever.release()
                    // ((BaseActivity)context).hideProgress();
                } catch (e: Throwable) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }

            }
        })
    }

}