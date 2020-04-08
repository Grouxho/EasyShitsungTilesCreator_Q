package com.android.systemui.qs.tiles;
/* 

Created by Grouxho on 21/01/2019. 

*/

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.util.Log;

import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class GrxExampleDarkModeTile extends QSTileImpl<QSTile.BooleanState> {

    int[] mIcons = new int[3];
    String[] mStrings;

    int mStringArrayId;

    public boolean mNeedsHandleUpdateState=true;

    private int mCurrentMode = 1;  // default is light mode in pie  0 -> auto, 1->light , 2 -> dark

    UiModeManager mUiModeMananger;



    Intent mLongIntent = null;

    public GrxExampleDarkModeTile(QSHost host){
        super(host); // this call is needed for a right tile init.
        mUiModeMananger = (UiModeManager) mContext.getSystemService(Context.UI_MODE_SERVICE);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            mLongIntent = new Intent("android.settings.NIGHT_THEME_SETTINGS");
        }

        setUpResources();  // You could call here to the method initializing your vars, etc.
    }

    private void setUpResources(){

        String pkgname = mContext.getPackageName();
        Resources resources = mContext.getResources();
        int id = resources.getIdentifier("dark_modes","array",pkgname);
        mStringArrayId=id;
        mStrings = resources.getStringArray(id);

        id = resources.getIdentifier("gi_night_auto","drawable",pkgname);
        mIcons[0]=id;

        id = resources.getIdentifier("gi_night_off","drawable",pkgname);
        mIcons[1]=id;

        id = resources.getIdentifier("gi_night_on","drawable",pkgname);
        mIcons[2]=id;

    //    Log.d("GrxExampleDarkModeTile", "setUpResources");

    }




    @Override
    public Intent getLongClickIntent(){
        return mLongIntent;

    }

    @Override
    public int getMetricsCategory(){
        return 787;
    }


    @Override
    public CharSequence getTileLabel(){
        return  mStrings[mCurrentMode];
    }

    @Override
    public void setListening(boolean listening){
    }

    @Override
    public void handleSetListening(boolean listening) {
    }



    @Override
    public QSTile.BooleanState newTileState() {
        mNeedsHandleUpdateState = true;
        return new QSTile.BooleanState();  // this is the basic implementation of this method
    }



    @Override
    protected void handleClick(){

        mNeedsHandleUpdateState = true;

        int newmode;

        if(mCurrentMode!=2) newmode = mCurrentMode +1 ;
        else newmode=0;
        mUiModeMananger.setNightMode(newmode);
        refreshState();

    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {

        if(mNeedsHandleUpdateState) {
            mNeedsHandleUpdateState=false;
            mStrings = mContext.getResources().getStringArray(mStringArrayId);  // just in case languaje was changed
            mCurrentMode = mUiModeMananger.getNightMode();
            state.state = Tile.STATE_ACTIVE;
            state.icon = ResourceIcon.get(mIcons[mCurrentMode]);
            state.label = mStrings[mCurrentMode];
          //  Log.d("GrxExampleDarkModeTile", "handleUpdateState");
        }

    }

    @Override
    public boolean isAvailable() {  // if this return false then tile is not shown. Add there your conditions for enabling or not the tile.
        return true;
    }



}
