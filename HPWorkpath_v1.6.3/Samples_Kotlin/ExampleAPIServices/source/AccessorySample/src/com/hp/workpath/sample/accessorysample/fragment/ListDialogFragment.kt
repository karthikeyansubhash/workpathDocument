// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.RegistrationType
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.sample.accessorysample.Action
import com.hp.workpath.sample.accessorysample.Logger
import com.hp.workpath.sample.accessorysample.R
import com.hp.workpath.sample.accessorysample.databinding.DialogRadioListBinding
import com.hp.workpath.sample.accessorysample.fragment.AccessoryFragment.Companion.REQUEST_KEY

class ListDialogFragment : DialogFragment() {

    private lateinit var emptyTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private var accessoryInfoList: ArrayList<AccessoryInfo>? = null
    private var reservedAccessory: AccessoryInfo? = null
    private var action: Action? = null
    private var currentSelected = 0

    private lateinit var dialogView: View
    private var mBindingFragment: DialogRadioListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingListDialogFragment get() = mBindingFragment!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogRadioListBinding.inflate(inflater)
        dialogView = mBindingFragment!!.root
        emptyTextView = dialogView.findViewById(R.id.emptyTextView)
        radioGroup = dialogView.findViewById(R.id.radioGroup)

        currentSelected = requireArguments().getInt(CURRENT_SELECTED)
        accessoryInfoList = requireArguments().getParcelableArrayList(ACCESSORY_INFO_LIST)
        reservedAccessory = requireArguments().getParcelable(RESERVED_ACCESSORY)
        action = requireArguments().getSerializable(ACTION) as Action?
        var title = ""
        action?.let {
            if (Action.GET_OWNED == it) {
                title = requireActivity().getString(R.string.get_owned_accessories)
            } else if (Action.ENUMERATE == it) {
                title = requireActivity().getString(R.string.attached_accessories)
            }
        }
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, mCancelListener)
            .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewElements()
    }

    private fun findViewElements() {
        accessoryInfoList?.let {
            if (it.size == 0) {
                mBindingListDialogFragment.emptyTextView.visibility = View.VISIBLE
            } else {
                for (i in it.indices) {
                    val radio = AppCompatRadioButton(activity)
                    val params = RadioGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 5, 0, 5)
                    radio.layoutParams = params
                    val hidAccessoryInfo = it[i].getDetails<HIDAccessoryInfo>()
                    val info = Logger.build(hidAccessoryInfo)
                    radio.text = info
                    radio.tag = i
                    if (i == currentSelected) {
                        radio.isChecked = true
                    }
                    if (reservedAccessory != null && reservedAccessory is HIDAccessoryInfo) {
                        val reservedAccessoryInfo =
                            (reservedAccessory as HIDAccessoryInfo).getDetails<HIDAccessoryInfo>()
                        if (hidAccessoryInfo.productId == reservedAccessoryInfo.productId
                            && hidAccessoryInfo.vendorId == reservedAccessoryInfo.vendorId
                            && hidAccessoryInfo.serialNumber == reservedAccessoryInfo.serialNumber
                        ) {
                            radio.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.reserved
                                )
                            )
                        }
                    }
                    radio.setOnCheckedChangeListener { _, _ ->
                        parentFragmentManager.setFragmentResult(
                            REQUEST_KEY, // Same request key StoreJobFragment used to register its listener
                            bundleOf(CURRENT_SELECTED to radio.tag as Int, ACTION to action) // The data to be passed to StoreJobFragment
                        )
                        dialog?.dismiss()
                    }
                    if (Action.ENUMERATE == action) {
                        if (RegistrationType.SHARED != hidAccessoryInfo.registrationType) {
                            radio.isEnabled = false
                        }
                    }
                    mBindingListDialogFragment.radioGroup.addView(radio)
                }
            }
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        const val ACTION = "action"
        const val CURRENT_SELECTED = "currentSelected"
        const val RESERVED_ACCESSORY = "reservedAccessory"
        const val ACCESSORY_INFO_LIST = "accessoryInfoList"

        fun newInstance(
            action: Action?, accessoryInfoList: List<AccessoryInfo>,
            currentSelected: Int, reservedAccessory: AccessoryInfo?
        ): ListDialogFragment {
            val f = ListDialogFragment()
            val args = Bundle()
            args.putSerializable(ACTION, action)
            args.putInt(CURRENT_SELECTED, currentSelected)
            args.putParcelable(RESERVED_ACCESSORY, reservedAccessory)
            args.putParcelableArrayList(ACCESSORY_INFO_LIST, ArrayList(accessoryInfoList))
            f.arguments = args
            return f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}