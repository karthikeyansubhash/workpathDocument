// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hp.workpath.api.authorization.Permission
import com.hp.workpath.api.authorization.PermissionToSignInMethod
import com.hp.workpath.sample.authorization.DialogType
import com.hp.workpath.sample.authorization.MainActivity
import com.hp.workpath.sample.authorization.MainActivity.Companion.SCREEN_4_3_INCH
import com.hp.workpath.sample.authorization.R
import com.hp.workpath.sample.authorization.adapter.SelectableAdapter
import com.hp.workpath.sample.authorization.databinding.DialogPermissionsBinding
import com.hp.workpath.sample.authorization.exception.ResultException
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener
import com.hp.workpath.sample.authorization.task.GetPermissionsTask
import com.hp.workpath.sample.authorization.task.GetSignInMethodsTask
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors


class PermissionsFragment : DialogFragment() {
    private lateinit var binding: DialogPermissionsBinding
    private lateinit var adapter: SelectableAdapter<Parcelable>
    private lateinit var listener: IDialogFragmentListener
    private lateinit var dataType: DialogType.Data
    private var isVisibleCheckBox = false
    private var selectedItems = ArrayList<Parcelable>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IDialogFragmentListener) {
            listener = context
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
            dataType = it.getSerializable(DialogType.DIALOG_TYPE) as? DialogType.Data
                ?: DialogType.Data.GET_PERMISSIONS // Provide a default value if null
            selectedItems = it.getParcelableArrayList(DialogType.DIALOG_DATA) ?: ArrayList()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = DialogPermissionsBinding.inflate(inflater, null, false)
        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
        dialogBuilder.setTitle(dataType.name)
        dialogBuilder.setView(binding.root)
        dialogBuilder.setCancelable(false)
        when (dataType) {
            DialogType.Data.GET_PERMISSIONS, DialogType.Data.GET_SIGN_IN_METHODS -> {
                dialogBuilder.setPositiveButton(android.R.string.ok, okListener)
            }

            DialogType.Data.GUEST_PERMISSION_SET -> {
                dialogBuilder.setPositiveButton(android.R.string.ok, okListener)
                dialogBuilder.setNegativeButton(android.R.string.cancel, cancelListener)
                isVisibleCheckBox = true
            }

            else -> {}
        }
        return dialogBuilder.create()
    }

    override fun onStart() {
        super.onStart()
        try {
            val window = dialog?.window
            if (window != null) {
                var width = (resources.displayMetrics.widthPixels * 0.7).toInt()
                if (SCREEN_4_3_INCH == binding.permissionsDialog.tag) {
                    width = (resources.displayMetrics.widthPixels * 0.9).toInt()
                }
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                window.setLayout(width, height)
            }

            val executorService = Executors.newSingleThreadExecutor()
            val clickListener = getOnItemClickListener()

            when (dataType) {
                DialogType.Data.GET_PERMISSIONS, DialogType.Data.GUEST_PERMISSION_SET, DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID -> {
                    val future = executorService.submit(GetPermissionsTask(requireContext()))
                    val permissions = future.get()
                    adapter = SelectableAdapter(
                        ArrayList(permissions),
                        selectedItems,
                        clickListener,
                        isVisibleCheckBox
                    )
                }

                DialogType.Data.GET_SIGN_IN_METHODS, DialogType.Data.DEFAULT_SIGN_IN_METHOD, DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD -> {
                    val future =
                        executorService.submit(
                            GetSignInMethodsTask(
                                requireContext(),
                                getString(R.string.en_us)
                            )
                        )
                    val signInMethods = future.get()
                    adapter =
                        SelectableAdapter(
                            ArrayList(signInMethods),
                            selectedItems,
                            clickListener,
                            isVisibleCheckBox
                        )
                }

                else -> {}
            }
            binding.permissionList.layoutManager = LinearLayoutManager(requireContext())
            binding.permissionList.adapter = adapter
        } catch (ee: ExecutionException) {
            val cause = ee.cause
            if (cause is ResultException) {
                dismiss()
                listener.onDialogError(cause.result)
            }
            Log.e(MainActivity.TAG, cause?.message ?: "Error occurred")
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, e.message ?: "Error occurred")
        }
    }

    private fun getOnItemClickListener(): SelectableAdapter.OnItemClickListener<Parcelable>? {
        return when (dataType) {
            DialogType.Data.DEFAULT_SIGN_IN_METHOD, DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID, DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD -> {
                SelectableAdapter.OnItemClickListener { item ->
                    val currentDataType = dataType
                    if (currentDataType != null) {
                        val result = hashMapOf<String, Any>(
                            DialogType.DIALOG_TYPE to currentDataType,
                            currentDataType.name to item
                        )
                        listener.onDialogResult(result)
                        dismiss()
                    }
                }
            }

            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private val okListener = DialogInterface.OnClickListener { dialog, _ ->
        val currentDataType = dataType
        if (!::adapter.isInitialized) {
            dialog.dismiss()
            return@OnClickListener
        }
        if (currentDataType != null) {
            val result = hashMapOf<String, Any>(
                DialogType.DIALOG_TYPE to currentDataType,
                currentDataType.name to adapter.getSelectedItems()
            )
            listener?.onDialogResult(result)
            dialog.dismiss()
        }
    }

    private val cancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        fun <E> addView(parent: LinearLayout, data: E, dataSet: MutableSet<E>) {
            val inflater = LayoutInflater.from(parent.context)
            val viewGroup = inflater.inflate(R.layout.layout_box, parent, false) as ViewGroup

            val textView = viewGroup.findViewById<TextView>(R.id.textView)
            textView.text = when (data) {
                is Permission -> data.localizedName.value
                is PermissionToSignInMethod -> data.permissionId
                else -> ""
            }

            val deleteButton = viewGroup.findViewById<Button>(R.id.deleteButton)
            deleteButton.setOnClickListener {
                dataSet.remove(data)
                parent.removeView(viewGroup)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 10, 0)
            }
            viewGroup.layoutParams = params
            parent.addView(viewGroup)
        }
    }
}