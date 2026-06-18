// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesReader
import com.hp.workpath.sample.printsample.R
import com.hp.workpath.sample.printsample.databinding.DialogCheckboxListBinding

class CheckboxListDialogFragment : DialogFragment() {

    private lateinit var emptyTextView: TextView
    private lateinit var checkboxContainer: LinearLayoutCompat
    private lateinit var mListener: BatchJobListInterface

    private var printAttributes = ArrayList<PrintAttributes?>()
    private lateinit var dialogView: View
    private var mBindingFragment: DialogCheckboxListBinding? = null
    private var mPrintObjectdMap: MutableMap<Int, Boolean> = HashMap()
    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingListDialogFragment get() = mBindingFragment!!

    interface BatchJobListInterface {
        fun onRemovePrintAttributeFromList(mPrintObjectdMap: MutableMap<Int, Boolean>)
        fun showJobSelectionError()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = activity as BatchJobListInterface
        } catch (e: ClassCastException) {
            Toast.makeText(activity, activity?.javaClass?.simpleName
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogCheckboxListBinding.inflate(inflater)
        dialogView = mBindingFragment!!.root
        emptyTextView = dialogView.findViewById(R.id.emptyTextView)
        checkboxContainer = dialogView.findViewById(R.id.checkbox_container)

        printAttributes = requireArguments().getParcelableArrayList(PRINT_ATTRIBUTE_LIST)!!

        printAttributes.forEachIndexed { index, _ -> mPrintObjectdMap[index] = false }

        val title = requireActivity().getString(R.string.batch_job_list)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(requireActivity().getString(R.string.done)) { dialog, _ ->
                dialog?.dismiss()
            }
            .setNegativeButton(requireActivity().getString(R.string.remove), null)
            .setCancelable(false)

        return dialogBuilder.create()
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton = d.getButton(Dialog.BUTTON_NEGATIVE) as Button
            positiveButton.setOnClickListener(View.OnClickListener {
                if (mPrintObjectdMap.filter { it.value }.isNotEmpty()) {
                    mListener.onRemovePrintAttributeFromList(mPrintObjectdMap)
                    dismiss()
                } else {
                    mListener.showJobSelectionError()
                }
            })
        }
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
        printAttributes.forEachIndexed { index, printAttribute ->
            val checkBox = AppCompatCheckBox(requireContext())
            val params = LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 5, 0, 5)
            checkBox.layoutParams = params
            checkBox.text = PrintAttributesReader(printAttribute).uri.lastPathSegment

            checkBox.tag = index
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                mPrintObjectdMap[index] = isChecked
            }

            mBindingListDialogFragment.checkboxContainer.addView(checkBox)
        }
    }

    companion object {
        const val ACTION = "action"
        const val PRINT_ATTRIBUTE_LIST = "print_attributes"
        const val REQUEST_KEY = "result-listener-request-key"

        fun newInstance(
            accessoryInfoList: ArrayList<PrintAttributes>
        ): CheckboxListDialogFragment {
            val f = CheckboxListDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(PRINT_ATTRIBUTE_LIST, accessoryInfoList)
            f.arguments = args
            return f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}