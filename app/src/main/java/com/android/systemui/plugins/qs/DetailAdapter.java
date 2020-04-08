package com.android.systemui.plugins.qs;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JLG76 on 24/07/2018.


 */

public interface DetailAdapter  {
    public static final int VERSION = 1;

    CharSequence getTitle();
    Boolean getToggleState();

    View createDetailView(Context context, View convertView, ViewGroup parent);
    Intent getSettingsIntent();
    void setToggleState(boolean state);  // aosp roms return void, sammy roms returns boolean in pie-oreo, but void in Q!!
    int getMetricsCategory();

    default boolean getToggleEnabled() {
        return true;
    }

    /**
     * Indicates whether the detail view wants to have its header (back button, title and
     * toggle) shown.
     */


    default boolean hasHeader() {
        return true;
    }

}
