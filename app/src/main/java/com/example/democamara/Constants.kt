package com.example.democamara

import android.Manifest

object Constants {
    const val TAG="Demo camara"
    const val FILE_NAME_FORMAT="yy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS=123
    val REQUIERED_PERMISSIONS= arrayOf(Manifest.permission.CAMERA)
}