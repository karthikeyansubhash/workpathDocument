package com.hp.workpath.sample.copysample.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.preference.PreferenceDialogFragmentCompat
import com.hp.workpath.api.copier.*
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import java.lang.Exception

class StampOptionPreferenceFragment : PreferenceDialogFragmentCompat() {
    private var mStampPositionSpinner: Spinner? = null
    private var mStampTypeSpinner: Spinner? = null
    private var mStampPolicyTypeSpinner: Spinner? = null
    private var mStampTextEditText: EditText? = null
    private var mStampFormatFontSpinner: Spinner? = null
    private var mStampFormatTextSizeSpinner: Spinner? = null
    private var mStampFormatTextColorSpinner: Spinner? = null
    private var mStampFormatWhiteBackgroundSpinner: Spinner? = null
    private var mStampFormatStatingPageEditText: EditText? = null
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        try {
            mStampPositionSpinner = view.findViewById(R.id.stampPositionSpinner)
            mStampTypeSpinner = view.findViewById(R.id.stampTypeSpinner)
            mStampPolicyTypeSpinner = view.findViewById(R.id.stampPolicyTypeSpinner)
            mStampTextEditText = view.findViewById(R.id.stampTextEditText)
            mStampFormatFontSpinner = view.findViewById(R.id.stampFormatFontSpinner)
            mStampFormatTextSizeSpinner = view.findViewById(R.id.stampFormatTextSizeSpinner)
            mStampFormatTextColorSpinner = view.findViewById(R.id.stampFormatTextColorSpinner)
            mStampFormatWhiteBackgroundSpinner =
                view.findViewById(R.id.stampFormatWhiteBackgroundSpinner)
            mStampFormatStatingPageEditText =
                view.findViewById(R.id.stampFormatStartingPageEditText)
            val mainActivity = activity as MainActivity?
            if (mainActivity!!.capabilities != null) {
                setStampCapabilities()
            }
        } catch (e: Exception) {
        }
    }

    private fun setStampCapabilities() {
        val stampPositionAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            stampPreference.getmStampPositionList()!!
        )
        Log.d("TAG capabilities", "setStampCapabilities: ${stampPreference.getmStampPositionList()!!.size}")
        mStampPositionSpinner!!.adapter = stampPositionAdapter
        mStampPositionSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val stampPosition = CopyAttributes.StampPosition.valueOf(
                        mStampPositionSpinner!!.getItemAtPosition(position).toString()
                    )
                    val stampTypeAdapter = ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampTypeListMap()!![stampPosition]!!
                    )

                    val stampPolicyTypeAdapter= ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampPolicyTypeListMap()!![stampPosition]!!
                    )
                    val stampFontAdapter = ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampFontListMap()!![stampPosition]!!
                    )
                    val stampTextSizeAdapter = ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampTextSizeListMap()!![stampPosition]!!
                    )
                    val stampTextColorAdapter = ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampTextColorListMap()!![stampPosition]!!
                    )
                    val stampWhiteBackgroundAdapter = ArrayAdapter(
                        context!!,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        stampPreference.getmStampWhiteBackGroundListMap()!![stampPosition]!!
                    )
                    mStampTypeSpinner!!.adapter = stampTypeAdapter
                    mStampPolicyTypeSpinner!!.adapter = stampPolicyTypeAdapter
                    mStampFormatFontSpinner!!.adapter = stampFontAdapter
                    mStampFormatTextSizeSpinner!!.adapter = stampTextSizeAdapter
                    mStampFormatTextColorSpinner!!.adapter = stampTextColorAdapter
                    mStampFormatWhiteBackgroundSpinner!!.adapter = stampWhiteBackgroundAdapter
                    val stampOption = stampPreference.getmStampOptionMap()!![stampPosition]
                    if (stampOption != null) {
                        mStampTypeSpinner!!.setSelection(
                            stampTypeAdapter.getPosition(
                                stampOption.type
                            )
                        )
                        mStampPolicyTypeSpinner!!.setSelection(
                            stampPolicyTypeAdapter.getPosition(
                                stampOption.policyType
                            )
                        )
                        mStampFormatFontSpinner!!.setSelection(
                            stampFontAdapter.getPosition(
                                stampOption.format.font
                            )
                        )
                        mStampFormatTextSizeSpinner!!.setSelection(
                            stampTextSizeAdapter.getPosition(
                                stampOption.format.textSize
                            )
                        )
                        mStampFormatTextColorSpinner!!.setSelection(
                            stampTextColorAdapter.getPosition(
                                stampOption.format.textColor
                            )
                        )
                        mStampFormatWhiteBackgroundSpinner!!.setSelection(
                            stampWhiteBackgroundAdapter.getPosition(stampOption.format.whiteBackground)
                        )
                        mStampFormatStatingPageEditText!!.setText(stampOption.format.startingPage.toString())
                        mStampTextEditText!!.setText(stampOption.text)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    // get the NumberPickerPreference instance
    private val stampPreference: StampOptionDialogPreference
         get() = preference as StampOptionDialogPreference

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            if (mStampPositionSpinner!!.selectedItem == null) {
                return
            }
            val stampPosition = CopyAttributes.StampPosition.valueOf(
                mStampPositionSpinner!!.selectedItem.toString()
            )
            val font = mStampFormatFontSpinner!!.selectedItem.toString()
            val textSize = mStampFormatTextSizeSpinner!!.selectedItem.toString().toInt()
            val textColor = mStampFormatTextColorSpinner!!.selectedItem.toString()
            val whiteBackground = java.lang.Boolean.parseBoolean(
                mStampFormatWhiteBackgroundSpinner!!.selectedItem.toString()
            )
            var startingPage = 1
            try {
                startingPage = mStampFormatStatingPageEditText!!.text.toString().toInt()
            } catch (e: Exception) {
            }
            val stampType = StampType.valueOf(
                mStampTypeSpinner!!.selectedItem.toString()
            )
            var stampPolicyType = StampPolicyType.NONE
            try {
                stampPolicyType = StampPolicyType.valueOf(
                    mStampPolicyTypeSpinner!!.selectedItem.toString()
                )
            } catch (e: Exception) {
            }
            val text = mStampTextEditText!!.text.toString()
            val stampFormat = StampFormat(font, textSize, textColor, whiteBackground, startingPage)
            val stampOption = StampOption(stampFormat, stampPolicyType, text, stampType)
            stampPreference.getmStampOptionMap()!![stampPosition] = stampOption
            stampPreference.saveStampOptionMap()
        }
    }

    companion object {
        fun newInstance(key: String?): StampOptionPreferenceFragment {
            val fragment = StampOptionPreferenceFragment()
            val bundle = Bundle()
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}