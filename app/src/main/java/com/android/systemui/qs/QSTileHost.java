package com.android.systemui.qs;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.systemui.plugins.qs.QSTile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class QSTileHost implements QSHost {

    private final Context mContext;
    private final LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>();
    private final List<Callback> mCallbacks = new ArrayList<>();


    public QSTileHost(Context context){
        mContext = context;
    }

    @Override
    public void warn(String message, Throwable t) {
        // already logged
    }

    @Override
    public void collapsePanels() {
    //    mStatusBar.postAnimateCollapsePanels();
    }
    @Override
    public void forceCollapsePanels() {
     //   mStatusBar.postAnimateForceCollapsePanels();
    }

    @Override
    public void openPanels() {
     //   mStatusBar.postAnimateOpenPanels();
    }

    @Override
    public Context getContext() {
        return mContext;
    }
    @Override
    public Collection<QSTile> getTiles() {
        return mTiles.values();
    }
    @Override
    public void addCallback(Callback callback) {
        mCallbacks.add(callback);
    }
    @Override
    public void removeCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeTile(String tileSpec) {
     /*
        ArrayList<String> specs = new ArrayList<>(mTileSpecs);

        specs.remove(tileSpec);
        Settings.Secure.putStringForUser(mContext.getContentResolver(), TILES_SETTING,
                TextUtils.join(",", specs), ActivityManager.getCurrentUser());

        */
    }

    public int indexOf(String spec) {
//        return mTileSpecs.indexOf(spec);
        return 0;
    }

}
