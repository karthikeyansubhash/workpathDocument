package com.hp.workpath.sample.copysample.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.StampOption;
import com.hp.workpath.api.copier.StampPolicyType;
import com.hp.workpath.api.copier.StampType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment.PREF_STAMP;

public class StampOptionDialogPreference extends DialogPreference {

    private List<CopyAttributes.StampPosition> mStampPositionList;
    private Map<CopyAttributes.StampPosition, List<StampType>> mStampTypeListMap;
    private Map<CopyAttributes.StampPosition, List<StampPolicyType>> mStampPolicyTypeListMap;
    private Map<CopyAttributes.StampPosition, List<String>> mStampFontListMap;
    private Map<CopyAttributes.StampPosition, List<Integer>> mStampTextSizeListMap;
    private Map<CopyAttributes.StampPosition, List<String>> mStampTextColorListMap;
    private Map<CopyAttributes.StampPosition, List<Boolean>> mStampWhiteBackGroundListMap;

    private Map<CopyAttributes.StampPosition, StampOption> mStampOptionMap;


    public StampOptionDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<CopyAttributes.StampPosition> getmStampPositionList() {
        return mStampPositionList;
    }

    public void setmStampPositionList(List<CopyAttributes.StampPosition> mStampPositionList) {
        this.mStampPositionList = mStampPositionList;
    }

    public Map<CopyAttributes.StampPosition, List<StampType>> getmStampTypeListMap() {
        return mStampTypeListMap;
    }

    public void setmStampTypeListMap(Map<CopyAttributes.StampPosition, List<StampType>> mStampTypeListMap) {
        this.mStampTypeListMap = mStampTypeListMap;
    }

    public Map<CopyAttributes.StampPosition, List<StampPolicyType>> getmStampPolicyTypeListMap() {
        return mStampPolicyTypeListMap;
    }

    public void setmStampPolicyTypeListMap(Map<CopyAttributes.StampPosition, List<StampPolicyType>> mStampPolicyTypeListMap) {
        this.mStampPolicyTypeListMap = mStampPolicyTypeListMap;
    }

    public Map<CopyAttributes.StampPosition, List<String>> getmStampFontListMap() {
        return mStampFontListMap;
    }

    public void setmStampFontListMap(Map<CopyAttributes.StampPosition, List<String>> mStampFontListMap) {
        this.mStampFontListMap = mStampFontListMap;
    }

    public Map<CopyAttributes.StampPosition, List<Integer>> getmStampTextSizeListMap() {
        return mStampTextSizeListMap;
    }

    public void setmStampTextSizeListMap(Map<CopyAttributes.StampPosition, List<Integer>> mStampTextSizeListMap) {
        this.mStampTextSizeListMap = mStampTextSizeListMap;
    }

    public Map<CopyAttributes.StampPosition, List<String>> getmStampTextColorListMap() {
        return mStampTextColorListMap;
    }

    public void setmStampTextColorListMap(Map<CopyAttributes.StampPosition, List<String>> mStampTextColorListMap) {
        this.mStampTextColorListMap = mStampTextColorListMap;
    }

    public Map<CopyAttributes.StampPosition, List<Boolean>> getmStampWhiteBackGroundListMap() {
        return mStampWhiteBackGroundListMap;
    }

    public void setmStampWhiteBackGroundListMap(Map<CopyAttributes.StampPosition, List<Boolean>> mStampWhiteBackGroundListMap) {
        this.mStampWhiteBackGroundListMap = mStampWhiteBackGroundListMap;
    }

    public Map<CopyAttributes.StampPosition, StampOption> getmStampOptionMap() {
        return mStampOptionMap;
    }

    public void setmStampOptionMap(Map<CopyAttributes.StampPosition, StampOption> mStampOptionMap) {
        this.mStampOptionMap = mStampOptionMap;
    }


    public void saveStampOptionMap() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();


        for(CopyAttributes.StampPosition stampPosition : mStampOptionMap.keySet()){
            String jsonStampOption = gson.toJson(mStampOptionMap.get(stampPosition));
            editor.putString(PREF_STAMP+stampPosition.name(),jsonStampOption);
        }

        editor.apply();
        setStampSummary();
    }
    private void setStampSummary() {
        List<String> stamptype = new ArrayList<String>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        Gson gson = new Gson();
        String top_left = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.TOP_LEFT, (String) null);
        String top_right = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.TOP_RIGHT, (String) null);
        String top_center = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.TOP_CENTER, (String) null);
        String bottom_left = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.BOTTOM_LEFT, (String) null);
        String bottom_right = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.BOTTOM_RIGHT, (String) null);
        String bottom_center = sharedPref.getString("pref_stamp" + CopyAttributes.StampPosition.BOTTOM_CENTER, (String) null);
        StampOption top_left_stampOption = (StampOption) gson.fromJson(top_left, StampOption.class);
        StampOption top_right_stampOption = (StampOption) gson.fromJson(top_right, StampOption.class);
        StampOption top_center_stampOption = (StampOption) gson.fromJson(top_center, StampOption.class);
        StampOption bottom_left_stampOption = (StampOption) gson.fromJson(bottom_left, StampOption.class);
        StampOption bottom_right_stampOption = (StampOption) gson.fromJson(bottom_right, StampOption.class);
        StampOption bottom_center_stampOption = (StampOption) gson.fromJson(bottom_center, StampOption.class);

        if (top_left_stampOption != null) {
            if (!top_left_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.TOP_LEFT.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.TOP_LEFT.name());
            }
        }
        if (top_right_stampOption != null) {
            if (!top_right_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.TOP_RIGHT.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.TOP_RIGHT.name());
            }
        }
        if (top_center_stampOption != null) {
            if (!top_center_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.TOP_CENTER.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.TOP_CENTER.name());
            }
        }
        if (bottom_left_stampOption != null) {
            if (!bottom_left_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_LEFT.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_LEFT.name());
            }
        }
        if (bottom_right_stampOption != null) {
            if (!bottom_right_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_RIGHT.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_RIGHT.name());
            }
        }
        if (bottom_center_stampOption != null) {
            if (!bottom_center_stampOption.type.name().equals("NONE")) {
                stamptype.add(CopyAttributes.StampPosition.BOTTOM_CENTER.name());
            } else {
                stamptype.remove(CopyAttributes.StampPosition.BOTTOM_CENTER.name());
            }
        }
        if (stamptype.isEmpty()) {
            stamptype.add("NONE");
        }

        Handler handler = new Handler();
        Runnable r = (Runnable) (new Runnable() {
            public final void run() {
                StampOptionDialogPreference.this.setSummary(String.join(",",stamptype));
            }
        });
        handler.post(r);
    }
}