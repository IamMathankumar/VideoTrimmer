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
package com.curvegraph.deocut.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import com.claudiodegio.msv.OnSearchViewListener
import com.curvegraph.deocut.R
import com.curvegraph.deocut.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity(), HomeContract.View, HomeAdapter.ItemClickListener, OnSearchViewListener, SearchAdapter.ItemClickListener {
    override fun showSearchList(items: List<String>) {
        listViewSearchCard.visibility = View.VISIBLE
        searchdapter.addAllItems(items)

    }

    override fun hideSearchListView() {
        listViewSearchCard.visibility = View.GONE
    }

    override fun onSearchItemClickListener(video: String) {
       presenter.searchItemClick(video,this)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        // handle text submit and then return true
        return false;
    }

    override fun onSearchViewClosed() {
        listViewSearchCard.visibility = View.GONE
    }

    override fun onQueryTextChange(p0: String?) {
        presenter.searchText(p0!!)
    }

    override fun onSearchViewShown() {
    }


    private lateinit var adapter: HomeAdapter
    private lateinit var searchdapter: SearchAdapter
    override fun onItemClickListener(position: Int) {
        presenter.itemClick(position, this)
    }

    private lateinit var presenter: HomePresenter
    override fun showFoldersList(items: List<ModelFolder>) {
        adapter = HomeAdapter(items, this, this)
        listView.adapter = adapter

        searchdapter = SearchAdapter(this,this)
        listViewSearch.adapter = searchdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = HomePresenter(this)
        setSupportActionBar(toolbar)
        searchView.setOnSearchViewListener(this); // this class implements OnSearchViewListener
        getVideosFromDevice()

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


    private fun getVideosFromDevice(){
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


    override fun onResume() {
        super.onResume()
        getVideosFromDevice()
    }

    private fun callMedia() {
        presenter.getVideoFoldersAndFiles(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }
}
