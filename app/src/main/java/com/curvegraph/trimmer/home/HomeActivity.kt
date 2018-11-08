package com.curvegraph.trimmer.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.curvegraph.trimmer.R
import com.curvegraph.trimmer.utils.PermissionsHelper
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity(), HomeContract.View, HomeAdapter.ItemClickListener {
    private lateinit var adapter: HomeAdapter
    override fun onItemClickListener(position: Int) {
        presenter.itemClick(position, this)
    }

    private lateinit var presenter: HomePresenter
    override fun showFoldersList(items: List<ModelFolder>) {
        adapter = HomeAdapter(items, this, this)
        listView.adapter = adapter

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = HomePresenter(this)
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
