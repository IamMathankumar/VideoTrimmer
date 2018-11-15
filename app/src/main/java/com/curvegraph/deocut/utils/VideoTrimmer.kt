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

package com.curvegraph.deocut.utils

import android.content.Context
import com.curvegraph.deocut.interfaces.VideoTrimListener
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.provider.MediaStore
import android.content.ContentValues


object VideoTrimmer {

    private val TAG = VideoTrimmer::class.java.simpleName


    fun trim(context: Context, inputFile: String, outputFileIs: String, start: String, duration: String, callback: VideoTrimListener) {
        var outputFile = outputFileIs
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputName = "DeoCut_$timeStamp.mp4"
        outputFile = "$outputFile/$outputName"
        // val cmd = "-y -ss $start -i $inputFile -to $duration -strict -2 -c copy $outputFile"
        // val cmd = "-y -ss $start -i $inputFile -to $duration -async 1 -strict -2 -c copy $outputFile"
        /// ffmpeg -i movie.mp4 -ss 00:00:03 -t 00:00:08 -async 1 -c copy cut.mp4


        // val cmd = " \"$inputFile\" -ss $start -t $duration -c copy $outputFile"

        val commandList = LinkedList<String>()
        commandList.add("-i")
        commandList.add(inputFile)
        commandList.add("-ss")
        commandList.add(start)
        commandList.add("-t")
        commandList.add(duration)
        commandList.add("-async")
        commandList.add("1")
        commandList.add("-strict")
        commandList.add("-2")
        commandList.add("-c")
        commandList.add("copy")
        commandList.add(outputFile)
        val command = commandList.toTypedArray()
        try {
            val tempOutFile = outputFile
            FFmpeg.getInstance(context).execute(command, object : ExecuteBinaryResponseHandler() {

                override fun onFailure(message: String?) {
                    super.onFailure(message)
                    File(outputFile).delete()
                    callback.onFailed()
                }

                override fun onProgress(message: String?) {
                    super.onProgress(message)

                    println("Progress  : $message")
                }

                override fun onSuccess(s: String?) {
                    callback.onFinishTrim(tempOutFile)
                    addVideo(File(tempOutFile), outputName, context)
                }

                override fun onStart() {
                    callback.onStartTrim()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun addVideo(videoFile: File, filename: String, context: Context) {
        val values = ContentValues()
        values.put(MediaStore.Video.Media.TITLE, filename)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Deo Cut")
        values.put(MediaStore.Images.Media.DESCRIPTION, "")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

    }

}
