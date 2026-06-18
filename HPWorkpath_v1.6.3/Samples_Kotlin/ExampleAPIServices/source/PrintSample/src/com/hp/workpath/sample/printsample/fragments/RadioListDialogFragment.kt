package com.hp.workpath.sample.printsample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.MainActivity.Companion.REQUEST_KEY
import com.hp.workpath.sample.printsample.R
import com.hp.workpath.sample.printsample.databinding.DialogRadioListBinding


class RadioListDialogFragment : DialogFragment() {
    private lateinit var emptyTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private var batchjobsList: List<PrintAttributes>? = null
    private var currentSelected = 0
    private var backgroundjob = false

    private lateinit var dialogView: View
    private var mBindingFragment: DialogRadioListBinding? = null
    lateinit var batch_jobslist: Array<String>
    lateinit var stream_joblist: Array<String>

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingListDialogFragment get() = mBindingFragment!!
    lateinit var mPrefs: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogRadioListBinding.inflate(inflater)
        dialogView = mBindingFragment!!.root
        emptyTextView = dialogView.findViewById(R.id.emptyTextView)
        radioGroup = dialogView.findViewById(R.id.radioGroup)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        batch_jobslist = resources.getStringArray(R.array.batch_backgroundjob)
        stream_joblist = resources.getStringArray(R.array.batch_stream)

        batchjobsList = requireArguments().getParcelableArrayList(BATCH_JOB_LIST)
        backgroundjob = requireArguments().getBoolean(BACKGROUND_JOB)


        var title = "Select print mode"

        batchjobsList?.let {
            title +=
                if (it.size > 1) " (${it.size} Jobs are listed)"
                else " (${it.size} Job listed)"
        }

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, mCancelListener)
            .setPositiveButton("Print", mPrintListener)
            .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewElements()
    }

    private fun findViewElements() {
        val source = PrintAttributes.Source.valueOf(
            mPrefs.getString(
                PrintConfigureFragment.PREF_SOURCE,
                PrintAttributes.Source.STORAGE.name
            )
                ?: PrintAttributes.Source.STORAGE.name
        )

        if (PrintAttributes.Source.STREAM.equals(source)) {
            radioList(stream_joblist)
        } else {
            radioList(batch_jobslist)
        }
    }

    private fun radioList(list: Array<String>) {
        list.let {
            for (i in it.indices) {
                val radio = RadioButton(activity)
                val params = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 5, 0, 5)
                radio.layoutParams = params

                radio.text = it[i]
                radio.tag = i
                radio.id = i
                if (i == currentSelected) {
                    radio.isChecked = true
                    backgroundjob = false
                }
                mBindingListDialogFragment.radioGroup.addView(radio)
            }
            mBindingListDialogFragment.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                Log.d(MainActivity.TAG, "findViewElements: $checkedId")
                backgroundjob = currentSelected != checkedId
            }

        }
    }


    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }
    private val mPrintListener = DialogInterface.OnClickListener { dialog, _ ->
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY, // Same request key StoreJobFragment used to register its listener
            bundleOf(BACKGROUND_JOB to backgroundjob) // The data to be passed to StoreJobFragment
        )
        dialog.cancel()
    }

    companion object {
        const val BATCH_JOB_LIST = "batchjoblist"
        const val BACKGROUND_JOB = "backgroundjob"
        fun newInstance(
            batchJobs: ArrayList<PrintAttributes>?,
            backgroundJob: Boolean
        ): RadioListDialogFragment {
            val f = RadioListDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(BATCH_JOB_LIST, batchJobs)
            args.putBoolean(BACKGROUND_JOB, backgroundJob)
            f.arguments = args
            return f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}