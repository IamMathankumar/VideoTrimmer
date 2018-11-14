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
package com.curvegraph.trimmer.videos

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.curvegraph.trimmer.R
import com.curvegraph.trimmer.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_main.*

class VideosActivity : AppCompatActivity(), VideosContract.View, VideosAdapter.ItemClickListener {
    override fun title(title: String) {
        toolbar.title = title
    }

    private lateinit var adapter: VideosAdapter
    override fun onItemClickListener(position: Int) {
        presenter.itemClick(position, this)
    }

    private lateinit var presenter: VideosPresenter
    override fun showFoldersList(items: List<String>) {
        adapter = VideosAdapter(items, this, this)
        listView.adapter = adapter

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        presenter = VideosPresenter(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionsHelper.hasPermissions(this)) {
                callMedia()
            } else {
                PermissionsHelper.requestPermissions(this)
            }
        } else {
            callMedia()
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Note that order matters - see the note in onPause(), the reverse applies here.
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callMedia()
            // permission was granted, yay! do the
            // calendar task you need to do.

        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
        return

    }

    private fun callMedia() {
        presenter.getVideoFoldersAndFiles(this)
    }

}
