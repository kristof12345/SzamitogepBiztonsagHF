package com.itsecurityteam.caffstore.view

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.itsecurityteam.caffstore.R


class MainActivity : AppCompatActivity() {
    companion object {
        const val STORAGE_PERMISSION_REQUEST_CODE = 1009
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            || !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.permission_title).setMessage(R.string.permission_reason)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    requestPermissionForStorage()
                }

            builder.create().show()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val result: Int =
            applicationContext.checkSelfPermission(permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForStorage() {
        try {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    finish()
                }
            }
        }
    }
}