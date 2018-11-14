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

package com.curvegraph.trimmer.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.curvegraph.trimmer.R

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

object Dialogs {



    fun dialogInfoWrong(_A: Context) {
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
        dialog.show()

        val handler = Handler()
        handler.postDelayed({
            //Do something after 100ms
            dialog.dismiss()
        }, 2000)
    }


    interface DialogListener {
        fun nextLevel()

        fun reload()

        fun startTimer()
        fun back()
    }

}
