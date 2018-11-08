package com.curvegraph.trimmer.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.M)
object PermissionsHelper {


    private const val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE


    private const val GRANT_REQUEST_CODE = 1


    fun hasPermissions(activity: Activity): Boolean {
        return activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    }

    /**
     * Check to see we have the necessary permissions for this app, and ask for them if we don't.
     */
    fun requestPermissions(activity: Activity) {
        activity.requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE_PERMISSION, READ_EXTERNAL_STORAGE),
                GRANT_REQUEST_CODE)
    }

}
