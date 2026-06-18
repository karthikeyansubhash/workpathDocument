// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.authorization.EmailAddressInfo
import com.hp.workpath.sample.authorization.DialogType
import com.hp.workpath.sample.authorization.MainActivity
import com.hp.workpath.sample.authorization.R
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener

class MailFragment : DialogFragment() {
    private lateinit var mMailNameEditText: EditText
    private lateinit var mMailAddressEditText: EditText
    private lateinit var mMailType: DialogType.Email
    private lateinit var mListener: IDialogFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IDialogFragmentListener) {
            mListener = context
        } else {
            Toast.makeText(
                context,
                "${context.javaClass.simpleName} must implement IDialogFragmentListener",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(MainActivity.TAG, "$context must implement IDialogFragmentListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMailType = it.getSerializable(DialogType.DIALOG_TYPE) as? DialogType.Email
                ?: DialogType.Email.ADD_TO // Provide a default value if null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_mail, null)
        findViewElements(view)
        var title = getString(R.string.add)
        title += when (mMailType) {
            DialogType.Email.ADD_TO -> " (${getString(R.string.to)})"
            DialogType.Email.ADD_CC -> " (${getString(R.string.cc)})"
            DialogType.Email.ADD_BCC -> " (${getString(R.string.bcc)})"
            else -> ""
        }
        return AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(android.R.string.ok, mOKListener)
            .setNegativeButton(android.R.string.cancel, mCancelListener)
            .setCancelable(false)
            .create()
    }

    override fun onResume() {
        super.onResume()
        if (mListener == null) {
            dismiss()
        }
    }

    private fun findViewElements(view: View) {
        mMailNameEditText = view.findViewById(R.id.mailNameEditText)
        mMailAddressEditText = view.findViewById(R.id.mailAddrEditText)
    }

    private val mOKListener = DialogInterface.OnClickListener { dialog, _ ->
        val mailName = mMailNameEditText?.text?.toString()
        val mailAddress = mMailAddressEditText?.text?.toString()
        if (!mailAddress.isNullOrEmpty()) {
            val result = HashMap<String, Any>()
            val emailAddress = EmailAddressInfo(mailAddress, mailName)
            result[DialogType.DIALOG_TYPE] = mMailType
            result[getString(R.string.email)] = emailAddress
            mListener?.onDialogResult(result)
            dialog.dismiss()
        } else {
            Toast.makeText(activity, getString(R.string.email_address_empty), Toast.LENGTH_LONG)
                .show()
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        fun addMailView(
            viewGroup: ViewGroup,
            parent: LinearLayout,
            mail: EmailAddressInfo,
            mailList: ArrayList<EmailAddressInfo>
        ) {
            val textView = viewGroup.findViewById<TextView>(R.id.textView)
            val mailAddress = mail.address
            val mailName = mail.name
            val mailViewText = if (mailName.isNullOrEmpty()) {
                mailAddress
            } else {
                "$mailName <$mailAddress>"
            }
            textView.text = mailViewText

            val deleteButton = viewGroup.findViewById<Button>(R.id.deleteButton)
            deleteButton.setOnClickListener {
                mailList.remove(mail)
                parent.removeView(viewGroup)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 10, 0)
            viewGroup.layoutParams = params
            parent.addView(viewGroup)
        }
    }
}