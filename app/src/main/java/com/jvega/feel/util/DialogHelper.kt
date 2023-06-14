package com.jvega.feel.util

import android.app.AlertDialog
import android.content.Context

class DialogHelper {
    companion object {
        fun showAlertDialog(context: Context, title: String, message: String) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(title)
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                // Handle positive button click
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}
