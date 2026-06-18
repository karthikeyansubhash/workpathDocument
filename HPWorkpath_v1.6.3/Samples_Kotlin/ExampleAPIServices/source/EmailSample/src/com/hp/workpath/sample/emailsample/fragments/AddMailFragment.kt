// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.hp.workpath.sample.emailsample.EmailDialog
import com.hp.workpath.sample.emailsample.MainActivity
import com.hp.workpath.sample.emailsample.R
import com.hp.workpath.sample.emailsample.databinding.DialogAddMailBinding
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener
import com.hp.workpath.sample.emailsample.model.EmailAddress

class AddMailFragment : AppCompatDialogFragment() {

    private lateinit var mAddMailType: EmailDialog.Type
    private lateinit var mListener: IDialogFragmentListener
    private lateinit var customView: View
    private var mBindingFragment: DialogAddMailBinding? = null
    private val mBindingDialogAddMail get() = mBindingFragment!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var activity: Activity? = null
        if (context is Activity) {
            activity = context
        }
        try {
            mListener = activity as IDialogFragmentListener
        } catch (e: ClassCastException) {
            Toast.makeText(activity, activity?.javaClass?.simpleName
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$activity must implement IDialogFragmentListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            mAddMailType = args.getSerializable(EmailDialog.DIALOG_TYPE) as EmailDialog.Type
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogAddMailBinding.inflate(inflater)
        customView = mBindingDialogAddMail.root
        var title = resources.getString(R.string.add)
        when (mAddMailType) {
            EmailDialog.Type.ADD_TO -> title += " (" + resources.getString(R.string.to) + ")"
            EmailDialog.Type.ADD_CC -> title += " (" + resources.getString(R.string.cc) + ")"
            EmailDialog.Type.ADD_BCC -> title += " (" + resources.getString(R.string.bcc) + ")"
            else -> Unit
        }

        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                .setTitle(title)
                .setView(customView)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return customView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private val mOKListener = DialogInterface.OnClickListener { _, _ ->
        val mailName = mBindingDialogAddMail.mailNameEditText.text.toString()
        val mailAddress = mBindingDialogAddMail.mailAddrEditText.text.toString()
        if (!TextUtils.isEmpty(mailAddress)) {
            val result = HashMap<String, Any>()
            val emailAddress = EmailAddress(mailAddress, mailName)
            result[EmailDialog.DIALOG_TYPE] = mAddMailType
            result[requireActivity().getString(R.string.email)] = emailAddress
            mListener.onReturnValue(result)
            dialog?.dismiss()
        } else {
            Toast.makeText(activity, requireActivity().getString(R.string.email_address_empty),
                    Toast.LENGTH_LONG).show()
        }
    }
    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        private const val TAG = MainActivity.TAG
    }
}