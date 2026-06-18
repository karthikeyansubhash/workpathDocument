package com.hp.workpath.sample.copysample.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.hp.workpath.api.copier.CopyAttributes
import com.hp.workpath.api.copier.StampOption
import com.hp.workpath.api.copier.StampPolicyType
import com.hp.workpath.api.copier.StampType
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment.Companion.PREF_STAMP

class StampOptionDialogPreference(context: Context?, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {
    private var mStampPositionList: List<CopyAttributes.StampPosition>? = null
    private var mStampTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampType>>? = null
    private var mStampPolicyTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampPolicyType>>? =
        null
    private var mStampFontListMap: MutableMap<CopyAttributes.StampPosition, List<String>>? = null
    private var mStampTextSizeListMap: MutableMap<CopyAttributes.StampPosition, List<Int>>? = null
    private var mStampTextColorListMap: MutableMap<CopyAttributes.StampPosition, List<String>>? = null
    private var mStampWhiteBackGroundListMap: MutableMap<CopyAttributes.StampPosition, List<Boolean>>? = null
    private var mStampOptionMap: MutableMap<CopyAttributes.StampPosition, StampOption>? = null
    fun getmStampPositionList(): List<CopyAttributes.StampPosition>? {
        return mStampPositionList
    }

    fun setmStampPositionList(mStampPositionList: List<CopyAttributes.StampPosition>?) {
        this.mStampPositionList = mStampPositionList
    }

    fun getmStampTypeListMap(): Map<CopyAttributes.StampPosition, List<StampType>>? {
        return mStampTypeListMap
    }

    fun setmStampTypeListMap(mStampTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampType>>?) {
        this.mStampTypeListMap = mStampTypeListMap
    }

    fun getmStampPolicyTypeListMap(): Map<CopyAttributes.StampPosition, List<StampPolicyType>>? {
        return mStampPolicyTypeListMap
    }

    fun setmStampPolicyTypeListMap(mStampPolicyTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampPolicyType>>?) {
        this.mStampPolicyTypeListMap = mStampPolicyTypeListMap
    }

    fun getmStampFontListMap(): Map<CopyAttributes.StampPosition, List<String>>? {
        return mStampFontListMap
    }

    fun setmStampFontListMap(mStampFontListMap: MutableMap<CopyAttributes.StampPosition, List<String>>?) {
        this.mStampFontListMap = mStampFontListMap
    }

    fun getmStampTextSizeListMap(): Map<CopyAttributes.StampPosition, List<Int>>? {
        return mStampTextSizeListMap
    }

    fun setmStampTextSizeListMap(mStampTextSizeListMap: MutableMap<CopyAttributes.StampPosition, List<Int>>?) {
        this.mStampTextSizeListMap = mStampTextSizeListMap
    }

    fun getmStampTextColorListMap(): Map<CopyAttributes.StampPosition, List<String>>? {
        return mStampTextColorListMap
    }

    fun setmStampTextColorListMap(mStampTextColorListMap: MutableMap<CopyAttributes.StampPosition, List<String>>?) {
        this.mStampTextColorListMap = mStampTextColorListMap
    }

    fun getmStampWhiteBackGroundListMap(): MutableMap<CopyAttributes.StampPosition, List<Boolean>>? {
        return mStampWhiteBackGroundListMap
    }

    fun setmStampWhiteBackGroundListMap(mStampWhiteBackGroundListMap: MutableMap<CopyAttributes.StampPosition, List<Boolean>>?) {
        this.mStampWhiteBackGroundListMap = mStampWhiteBackGroundListMap
    }

    fun getmStampOptionMap(): MutableMap<CopyAttributes.StampPosition, StampOption>? {
        return mStampOptionMap
    }

    fun setmStampOptionMap(mStampOptionMap: MutableMap<CopyAttributes.StampPosition, StampOption>?) {
        this.mStampOptionMap = mStampOptionMap
    }

    fun saveStampOptionMap() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(
            context
        )
        val editor = sharedPref.edit()
        val gson = Gson()
        for (stampPosition in mStampOptionMap!!.keys) {
            val jsonStampOption = gson.toJson(mStampOptionMap!![stampPosition])
            editor.putString(PREF_STAMP.toString() + stampPosition.name, jsonStampOption)
        }
        editor.apply()
        setStampSummary();
    }


    private fun setStampSummary() {
        val stamptype= mutableListOf<String>()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val top_left=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.TOP_LEFT,null)
        val top_right=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.TOP_RIGHT,null)
        val top_center=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.TOP_CENTER,null)
        val bottom_left=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.BOTTOM_LEFT,null)
        val bottom_right=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.BOTTOM_RIGHT,null)
        val bottom_center=sharedPref.getString(PREF_STAMP.toString()+CopyAttributes.StampPosition.BOTTOM_CENTER,null)
        val top_left_stampOption=gson.fromJson(top_left,StampOption::class.java)
        val top_right_stampOption=gson.fromJson(top_right,StampOption::class.java)
        val top_center_stampOption=gson.fromJson(top_center,StampOption::class.java)
        val bottom_left_stampOption=gson.fromJson(bottom_left,StampOption::class.java)
        val bottom_right_stampOption=gson.fromJson(bottom_right,StampOption::class.java)
        val bottom_center_stampOption=gson.fromJson(bottom_center,StampOption::class.java)

        if(top_left_stampOption!=null){
            if(top_left_stampOption.type!!.name!="NONE"){
                stamptype.add(CopyAttributes.StampPosition.TOP_LEFT.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.TOP_LEFT.name)
            }
        }
        if(top_right_stampOption!=null){
            if(top_right_stampOption.type!!.name!="NONE"){
                stamptype.add(CopyAttributes.StampPosition.TOP_RIGHT.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.TOP_RIGHT.name)
            }
        }
        if(top_center_stampOption!=null) {
            if (top_center_stampOption.type!!.name != "NONE") {
                stamptype.add(CopyAttributes.StampPosition.TOP_CENTER.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.TOP_CENTER.name)
            }
        }
        if(bottom_left_stampOption!=null) {
            if (bottom_left_stampOption.type!!.name != "NONE") {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_LEFT.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_LEFT.name)
            }
        }
        if(bottom_right_stampOption!=null) {
            if (bottom_right_stampOption.type!!.name != "NONE") {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_RIGHT.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_RIGHT.name)
            }
        }
        if(bottom_center_stampOption!=null) {
            if (bottom_center_stampOption.type!!.name != "NONE") {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_CENTER.name)
            }else{
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_CENTER.name)
            }
        }
        if(stamptype.isEmpty()){
            stamptype.add("NONE")
        }
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable { summary = stamptype.joinToString(",") }
        handler.post(r)
    }
}