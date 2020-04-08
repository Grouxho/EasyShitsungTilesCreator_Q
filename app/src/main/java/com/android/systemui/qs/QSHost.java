package com.android.systemui.qs;

import android.content.Context;

import com.android.systemui.plugins.qs.QSTile;
//import com.android.systemui.qs.external.TileServices;

import java.util.Collection;

public interface QSHost {
    void warn(String message, Throwable t);
    void collapsePanels();
    void forceCollapsePanels();
    void openPanels();
    Context getContext();
    Collection<QSTile> getTiles();
    void addCallback(Callback callback);
    void removeCallback(Callback callback);
  //  TileServices getTileServices();
    void removeTile(String tileSpec);

    int indexOf(String tileSpec);

    interface Callback {
        void onTilesChanged();
    }
}