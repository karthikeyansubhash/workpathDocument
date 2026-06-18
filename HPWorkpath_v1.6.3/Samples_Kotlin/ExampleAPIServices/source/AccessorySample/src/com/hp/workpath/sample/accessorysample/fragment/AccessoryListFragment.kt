// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.RegistrationType
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.sample.accessorysample.Action
import com.hp.workpath.sample.accessorysample.Logger
import com.hp.workpath.sample.accessorysample.MainActivity
import com.hp.workpath.sample.accessorysample.R
import com.hp.workpath.sample.accessorysample.databinding.FragmentAccessoryListBinding
import com.hp.workpath.sample.accessorysample.fragment.AccessoryFragment.Companion.REQUEST_KEY
import com.hp.workpath.sample.accessorysample.task.ActionTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccessoryListFragment : Fragment(), View.OnClickListener {

    private var ownedAccessories: List<AccessoryInfo> = ArrayList()
    private var enumeratedAccessories: List<AccessoryInfo> = ArrayList()
    private var reservedAccessory: AccessoryInfo? = null
    private var accessoryContextId: String? = null
    private var ownedValueIndex = 0
    private var enumerateValueIndex = 0
    private lateinit var mBindingAccessoryList: FragmentAccessoryListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBindingAccessoryList = FragmentAccessoryListBinding.inflate(inflater, container, false)
        return mBindingAccessoryList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingAccessoryList.ownedListLayout.setOnClickListener(this)
        mBindingAccessoryList.enumerateListLayout.setOnClickListener(this)
        mBindingAccessoryList.getOwnedButton.setOnClickListener(this)
        mBindingAccessoryList.enumerateButton.setOnClickListener(this)
        mBindingAccessoryList.resendButton.setOnClickListener(this)
        mBindingAccessoryList.reserveButton.setOnClickListener(this)
        mBindingAccessoryList.releaseButton.setOnClickListener(this)
    }

    fun loadAccessories(action: Action?, accessoryInfos: List<AccessoryInfo>) {
        if (action == Action.GET_OWNED) {
            ownedAccessories = accessoryInfos
            ownedValueIndex = 0
            showSelectedOwnedAccessory()
        } else if (action == Action.ENUMERATE) {
            enumeratedAccessories = accessoryInfos
            enumerateValueIndex = 0
            showSelectedEnumerateAccessory()
        }
    }

    override fun onClick(v: View) {
        val action: Action
        when (v) {
            mBindingAccessoryList.ownedListLayout -> {
                action = Action.GET_OWNED
                showListDialog(
                    ListDialogFragment.newInstance(
                        action,
                        ownedAccessories,
                        ownedValueIndex,
                        reservedAccessory
                    )
                )
            }
            mBindingAccessoryList.enumerateListLayout -> {
                action = Action.ENUMERATE
                showListDialog(
                    ListDialogFragment.newInstance(
                        action,
                        enumeratedAccessories,
                        enumerateValueIndex,
                        reservedAccessory
                    )
                )
            }
            mBindingAccessoryList.getOwnedButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(requireActivity() as MainActivity).execute(Action.GET_OWNED)
                }
            }
            mBindingAccessoryList.resendButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(requireActivity() as MainActivity).execute(
                        Action.RESEND_OWNED,
                        selectedOwnedAccessory
                    )
                }
            }
            mBindingAccessoryList.enumerateButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(requireActivity() as MainActivity).execute(Action.ENUMERATE)
                }
            }
            mBindingAccessoryList.reserveButton -> {
                val accessoryInfo = selectedEnumerateAccessory
                if (accessoryInfo != null &&
                    !accessoryInfo.registrationType.equals(RegistrationType.SHARED)
                ) {
                    if (activity != null) {
                        Logger.showResult(activity, getString(R.string.shared_only))
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Default) {
                        ActionTask(requireActivity() as MainActivity).execute(
                            Action.RESERVE_SHARED,
                            selectedEnumerateAccessory
                        )
                    }
                }
            }
            mBindingAccessoryList.releaseButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(requireActivity() as MainActivity).execute(
                        Action.RELEASE_SHARED,
                        accessoryContextId
                    )
                }
            }
        }
    }

    private fun showListDialog(dialog: ListDialogFragment) {
        dialog.show(parentFragmentManager, "dialog")
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            this
        ) { requestKey, result ->
            if (REQUEST_KEY == requestKey) {
                if (Action.GET_OWNED == result.getSerializable(ListDialogFragment.ACTION)) {
                    ownedValueIndex = result.getInt(ListDialogFragment.CURRENT_SELECTED, 0)
                    showSelectedOwnedAccessory()
                } else if (Action.ENUMERATE == result.getSerializable(ListDialogFragment.ACTION)) {
                    enumerateValueIndex = result.getInt(ListDialogFragment.CURRENT_SELECTED, 0)
                    showSelectedEnumerateAccessory()
                }
            }
        }
    }

    fun updateReservedAccessory(accessoryInfo: AccessoryInfo?, accessoryContextId: String) {
        this.reservedAccessory = accessoryInfo
        this.accessoryContextId = accessoryContextId

        if (accessoryInfo == null) {
            mBindingAccessoryList.getOwnedCheckBox.visibility = View.GONE
            mBindingAccessoryList.enumerateCheckBox.visibility = View.GONE
        } else {
            when (accessoryInfo.registrationType) {
                RegistrationType.OWNED -> {
                    mBindingAccessoryList.getOwnedCheckBox.visibility = View.VISIBLE
                    mBindingAccessoryList.enumerateCheckBox.visibility = View.GONE
                }
                RegistrationType.SHARED -> {
                    mBindingAccessoryList.getOwnedCheckBox.visibility = View.GONE
                    mBindingAccessoryList.enumerateCheckBox.visibility = View.VISIBLE
                }
                else -> Unit
            }
        }
    }

    private val selectedOwnedAccessory: AccessoryInfo?
        get() = if (ownedAccessories.size > ownedValueIndex) {
            ownedAccessories[ownedValueIndex]
        } else {
            null
        }

    private val selectedEnumerateAccessory: AccessoryInfo?
        get() = if (enumeratedAccessories.size > enumerateValueIndex) {
            enumeratedAccessories[enumerateValueIndex]
        } else {
            null
        }

    private fun showSelectedOwnedAccessory() {
        if (ownedAccessories.isNotEmpty() && ownedAccessories.size > ownedValueIndex) {
            val ownedAccessory = ownedAccessories[ownedValueIndex]
            if (ownedAccessory is HIDAccessoryInfo) {
                mBindingAccessoryList.ownedTextView.text = Logger.build(ownedAccessory)
            }
        }
    }

    private fun showSelectedEnumerateAccessory() {
        if (enumeratedAccessories.isNotEmpty() && enumeratedAccessories.size > enumerateValueIndex) {
            val enumeratedAccessory = enumeratedAccessories[enumerateValueIndex]
            if (enumeratedAccessory is HIDAccessoryInfo) {
                mBindingAccessoryList.enumerateTextView.text = Logger.build(enumeratedAccessory)
            }
        }
    }
}