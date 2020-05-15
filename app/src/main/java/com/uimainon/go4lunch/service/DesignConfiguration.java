package com.uimainon.go4lunch.service;

import android.app.SearchManager;
import android.text.Html;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.uimainon.go4lunch.R;

import java.util.Objects;

public class DesignConfiguration {

    public void configureSearchViewDesign(SearchView sv, SearchManager searchManager, FragmentActivity activity, String placeholder){
        sv.setQueryHint(Html.fromHtml(placeholder));
        sv.setBackgroundColor(activity.getResources().getColor(R.color.colorBgNavBar));
        sv.setIconifiedByDefault(false);
        sv.setSubmitButtonEnabled(false);
        assert searchManager != null;
        sv.setSearchableInfo(searchManager.getSearchableInfo(Objects.requireNonNull(activity).getComponentName()));

        int searchVoiceId = sv.getContext().getResources().getIdentifier("android:id/search_voice_btn", null, null);
        int searchId = sv.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        TextView textView = (TextView) sv.findViewById(id);
        ImageView searchIconVoice = sv.findViewById(searchVoiceId);
        ImageView searchIcon = sv.findViewById(searchId);

        textView.setTextColor(activity.getResources().getColor(R.color.colortopBarLog));
        searchIconVoice.setColorFilter(R.color.colortopBarLog);
        searchIcon.setColorFilter(R.color.colortopBarLog);
    }
}
