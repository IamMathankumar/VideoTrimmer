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

package com.curvegraph.trimmer.utils

import android.content.Context
import com.curvegraph.trimmer.interfaces.VideoTrimListener
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.net.Uri
import java.io.File




object VideoTrimmer {

    private val TAG = VideoTrimmer::class.java.simpleName


    fun trim(context: Context, inputFileIs: String, outputFileIs: String, start: String, duration: String, callback: VideoTrimListener) {
        var outputFile = outputFileIs
        var inputFile = inputFileIs
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputName = "Curvegraph_$timeStamp.mp4"
        outputFile = "$outputFile/$outputName"

        val value = getMimeType(context, Uri.parse( File(inputFile).toString()))
        if(value == null){
     //       inputFile=inputFile.plus(".mp4")
        }
        println("getMimeType  : ${getMimeType(context, Uri.parse( File(inputFile).toString()))}")
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
        commandList.add("-c")
        commandList.add("copy")
        commandList.add(outputFile)
        val command = commandList.toTypedArray()
        try {
            val tempOutFile = outputFile
            FFmpeg.getInstance(context).execute(command, object : ExecuteBinaryResponseHandler() {

                override fun onFailure(message: String?) {
                    super.onFailure(message)
                //    pb.visibility = View.INVISIBLE
                }

                override fun onProgress(message: String?) {
                    super.onProgress(message)
                 //       pb.message.text = message
                    println("Progress  : $message")
                }
                override fun onSuccess(s: String?) {
                    callback.onFinishTrim(tempOutFile)
               //     pb.visibility = View.INVISIBLE
               //     pb.message.visibility = View.INVISIBLE
                }

                override fun onStart() {
                    callback.onStartTrim()
             //       pb.visibility = View.VISIBLE
            //        pb.progress = 0
             //       pb.message.visibility = View.VISIBLE
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getMimeType( context: Context, uri: Uri): String? {
        var mimeType: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = context.getContentResolver()
            mimeType = cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }

}
