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

@file:Suppress("unused")

package com.curvegraph.deocut.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.curvegraph.deocut.R
import kotlinx.android.synthetic.main.dialog_completed.*

object Dialogs {


    fun dialogTrimCompleted(_A: Context, bitmap: Bitmap, listener: DialogListener) {
        val dialog = Dialog(_A, R.style.NewDialog)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val lp = WindowManager.LayoutParams()
        if (null != dialog.window)
            lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        dialog.window!!.attributes = lp
        if (null != dialog.window)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_completed)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setCanceledOnTouchOutside(true)
        Glide.with(_A).load(bitmap).thumbnail(0.2f).apply(RequestOptions().centerCrop()).into(dialog.badgeIcon)

        dialog.actionShare.setOnClickListener {
            listener.shareVideo()
            dialog.dismiss()
        }
        dialog.actionPlay.setOnClickListener {
            listener.playVideo()
            dialog.dismiss()
        }
        dialog.actionSkip.setOnClickListener {
            dialog.dismiss()
            val handler = Handler()
            handler.postDelayed({
                //Do something after 100ms
                listener.skipped()
            }, 1000)

        }

        dialog.show()

    }


    interface DialogListener {
        fun skipped()
        fun playVideo()
        fun shareVideo()
    }

}
