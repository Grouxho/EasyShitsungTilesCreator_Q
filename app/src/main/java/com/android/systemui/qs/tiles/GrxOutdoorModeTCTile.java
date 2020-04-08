package com.android.systemui.qs.tiles;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class GrxOutdoorModeTCTile extends QSTileImpl<QSTile.BooleanState> {


    // outdoor mode with maximun time control

    private GrxOutdoorModeTCTile.MyAdapter mDetailAdapter;

    private int mIconId;
    private int mTileStringId;
    private GrxOutdoorModeTCTile.modeObserver mObserver;

    private boolean mIsEnabled = false;

    private boolean mSimulationMode= false;

    public GrxOutdoorModeTCTile(QSHost host){
        super(host);
        setUpResources();
        mObserver= new GrxOutdoorModeTCTile.modeObserver(new Handler());
        getOutdoorState();
    }

    @Override
    public Intent getLongClickIntent(){

        return null;
    }


    @Override
    public DetailAdapter getDetailAdapter(){   // override this method if you are using a tile adapter
        return mDetailAdapter;
    }

    @Override
    public int getMetricsCategory(){
        return 444;
    }

    @Override
    public CharSequence getTileLabel(){

        return  mContext.getString(mTileStringId);
    }


    @Override
    public void setListening(boolean listening){

    }

    @Override
    public void handleSetListening(boolean listening) {


    }



    @Override
    public QSTile.BooleanState newTileState() {

        return new QSTile.BooleanState();  // this is the basic implementation of this method
    }



    @Override
    protected void handleClick(){
        setOutdoorState(!mIsEnabled);
        refreshState();
    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {

        state.state = mIsEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;  // let´s say the tile host what is the state.
        state.icon = ResourceIcon.get(mIconId);   // ResourceIcon is a method implemented in QsTileImp that returns the Icon object, just need to call it with the icon drawable id we want to use.
        state.label = mContext.getString(mTileStringId);

        state.dualTarget=true; // new in samsung Q, we need to set it to true or the adapter will not be shown.
    }



    @Override
    protected void handleDestroy(){
        super.handleDestroy();

    }

    @Override
    public boolean isAvailable() {
        return true;

    }



    @Override
    protected void handleLongClick(){

    }

    public void getOutdoorState(){
        mIsEnabled = (Settings.System.getInt(mContext.getContentResolver(),"display_outdoor_mode",0) == 0) ? false : true;
    }

    public void setOutdoorState(boolean state){

        // this will fc, since api 23 if the app is not a system app. So, to simulate it we will catch the exception.
        int newstate = state ? 1 : 0;
        try {
            Settings.System.putInt(mContext.getContentResolver(),"display_outdoor_mode",newstate);
        }catch (Exception e) { // to test logic in VD or without settings permissions.
            mSimulationMode=true;
            mIsEnabled=state;
        }

    }


    private void setUpResources(){

        mIconId = mContext.getResources().getIdentifier("grx_tile_brightness", "drawable", mContext.getPackageName());
        mTileStringId = mContext.getResources().getIdentifier("outdoor_mode", "string", mContext.getPackageName());
        mDetailAdapter = new GrxOutdoorModeTCTile.MyAdapter();
    }

    public class modeObserver extends ContentObserver {

        public modeObserver(android.os.Handler handler){
            super(handler);
            register();
        }


        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            getOutdoorState();
            refreshState();
        }

        public void register() {
            ContentResolver contentResolver = mContext.getContentResolver();

            Uri uri = Settings.System.getUriFor("display_outdoor_mode");
            contentResolver.registerContentObserver(uri, false, this);
        }

        public  void unregister(){
            mContext.getContentResolver().unregisterContentObserver(this);
        }

    }




    public class MyAdapter implements DetailAdapter{  // our adapter class has to implement DetailAdapter

        int mLayoutId = 0;

        int mTilteStringId = 0;  // id for the title of the adapter shown in the header
        SeekBar mSeekbar;
        int mMin = 1;
        int mMax = 60;
        int mDefault = 15;
        TextView mTextView;
        int mAuxValue;


        /** methods that must be implemented */

        public CharSequence getTitle(){ //MANDATORY METHOD
            if(mTilteStringId==0) { // let´s look for the string id the first time we create the adapter, this is a just a way of doing this.
                mTilteStringId = mContext.getResources().getIdentifier("outdoor_mode","string",mContext.getPackageName());
            }
            return mContext.getString(mTilteStringId) ;
        }

        public Boolean getToggleState(){ //MANDATORY METHOD

            return (mIsEnabled ? new Boolean(true) : new Boolean(false));

        }

        public void updateViewsState(){
            mSeekbar.setEnabled(mIsEnabled);
            mTextView.setAlpha(mIsEnabled ? 1.0f : 0.5f);

        }

        public void saveMaxTime(int value){

            try {
                Settings.System.putInt(mContext.getContentResolver(),"display_outdoor_mode_maxtime",value);
            }catch (Exception e) { // to test logic in VD or without settings permissions.
                mSimulationMode=true;
            }


        }


        public View createDetailView(Context context, View view, ViewGroup viewGroup){  // MANDATORY METHOD

            // NOTE: REMEMBER THAT FOR Q THE DETAIL ADAPTER VIEW HAS NOT TO BE NULL IF YOU WANT THE SWITCH TO BE SHOWN!!
            // IN PIE AND OREO YOU COULD RETURN A NULL AND A NON NULL IN getToggleState AND THE HEADER SWITCH WAS SHOWN!!

            //IMPORTANT:  I AM USING sec_qs_detail_text AS THE DETAIL VIEW. THIS LAYOUT EXISTS IN SAMSUNG S9/PLUS Q FIRM
            // HAVE A LOOK IN YOUR Q DEVICE TO SEE WHAT SIMPLE DETAIL VIEW IS BEING USED OR ADD THIS LAYOUT AND STYLES, DIMENS ,ETC!!!!!!

            int currval = Settings.System.getInt(context.getContentResolver(), "display_outdoor_mode_maxtime",15);

            if(mLayoutId==0) {
                mLayoutId = mContext.getResources().getIdentifier("qs_detail_outdoormodetc","layout",mContext.getPackageName());
            }

           View myview =  LayoutInflater.from(mContext).inflate(mLayoutId,viewGroup,false);
            mTextView = (TextView) myview.findViewWithTag("currtime");
            mTextView.setText(String.valueOf(currval) + " min.");

            mSeekbar = myview.findViewWithTag("grxseekbar");
            mSeekbar.setMax(mMax-mMin);
            mSeekbar.setProgress(currval-mMin);
            mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mTextView.setText(String.valueOf(i+mMin) + " min.");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mAuxValue=mSeekbar.getProgress() + mMin;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if( (mSeekbar.getProgress()+mMin) != mAuxValue ){
                        //   Toast.makeText(mContext,"hay que actualizar", Toast.LENGTH_SHORT).show();
                        saveMaxTime(mSeekbar.getProgress()+mMin);
                    }
                }
            });


            updateViewsState();

            return myview;
        }


        public Intent getSettingsIntent(){// MANDATORY METHOD
            return getLongClickIntent(); // for this example we are returning same intent than long press on tile.
        }


        public void setToggleState(boolean state){ //MANDATORY METHOD

            mIsEnabled = state; // in this example we align the tile state with the user selection.
            setOutdoorState(state);
            refreshState(); // we refresh our tile appearance and text!
            updateViewsState(); // anable disable seekbar and change current max time alpha.

        }

        public int getMetricsCategory(){ // MANDATORY METHOD
            return 444;
        }


        /** Defined methods in DetailAdapter interface that can be overriden */

        @Override
        public boolean getToggleEnabled(){
            return true;  // we override this method in this example just to explain what is it for.
        }

    }

}


