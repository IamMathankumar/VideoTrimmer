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
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object VideoTrimmer {

    private val TAG = VideoTrimmer::class.java.simpleName


    fun trim(context: Context, inputFile: String, outputFile: String, start: String, duration: String, callback: VideoTrimListener) {
        var outputFile = outputFile
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputName = "Curvegraph_$timeStamp.mp4"
        outputFile = "$outputFile/$outputName"

        val cmd = "-y -ss $start -i $inputFile -to $duration -async 1 -strict -2 -c copy $outputFile"

        val command = cmd.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        try {
            val tempOutFile = outputFile
            FFmpeg.getInstance(context).execute(command, object : ExecuteBinaryResponseHandler() {

                override fun onSuccess(s: String?) {
                    callback.onFinishTrim(tempOutFile)
                }

                override fun onStart() {
                    callback.onStartTrim()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
