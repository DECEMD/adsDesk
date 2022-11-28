package com.example.adsdesk.dialogHelper

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.example.adsdesk.databinding.ProgressDialogLayoutBinding
import com.example.adsdesk.databinding.SignDialogBinding

object ProgressDialog {

    fun createProgressDialog(act: Activity): AlertDialog{
        val builder = AlertDialog.Builder(act)
        val binding = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        val view = binding.root
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

}