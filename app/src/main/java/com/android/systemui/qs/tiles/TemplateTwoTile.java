package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;



/*

TemplateTwoTile:

  Please, read first TemplateOneTile class annotations.

  Tile Features:

  - On - Off Tile, with different strings and icons for both states.
  - Added example code to persist tile state
  - We are going to optimize the handleUpdateState for to save a little bit of cpu processing.
  - We will support languaje strings.
  - Introduction to Tile Detail adapter. We will use the most simple tile adapter, enabling the detail header switch and the Details button.
  In GrxMultActionTile we will see a full detail adapter and not header switch. The detail adapter is shown in tiles when we press the Tile text!!


 */


public class TemplateTwoTile extends QSTileImpl<QSTile.BooleanState> {

    /* vars used in this example */

    public int mIconOffId, mIconOnId;  // ids for icon in states on and off
    public int mStringOnId, mStringOffId; // ids for tile texts in states on and off

    public int mStringAdapterHeaderTitle; // id for the header of header adapter
    public boolean mIsEnabled; // keeps current state. By default we want it enabled in this example. We will init this var value in setupResources when the tile is created


    public boolean mNeedsHandleUpdateState = true; // we will control when is needed to update the state object passed when handleUpdateState method is called.
    // we will change this var value when we receive a newtilestate method call, when we force a refreshstate call. Remember that newtilestate is called in the tile creation or when
    // some configuration (locale, font size, etc) is changed. To simultate this in a virtual device in android studio run the custom locale app in the emulator and change the languaje
    // by default true, to force state uupdate when tile is created.

    final public static String MY_SETTINGS_KEY = "templatetwo_tile_state";  // name of the settings key we are going to use to persist the state of this tile.
    public static final int METRICS_CATEGORY = 654; // our metrics category. I am adding this constant value because we will use it both in tile and detail adapter


    public TemplateTwoTile.MyAdapter mDetailAdapter;


    public TemplateTwoTile(QSHost host){
        super(host); // this call is needed for a right tile init.
        setUpResources();  // You could call here to the method initializing your vars, etc.
    }



    /** Methods that must be implemented  */


    @Override
    public Intent getLongClickIntent(){
       // Remember, if we return null nothing will happen when we long click on the tile.
        // in this example we are going to customize the intent, depending on the tile state.
        // if tile is on -> we will run display settings, if not enabled main settings activity

        return (!mIsEnabled ? new Intent("android.settings.SETTINGS") : new Intent("com.android.settings.DISPLAY_SETTINGS"));
    }

    @Override
    public int getMetricsCategory(){
        return METRICS_CATEGORY;
    }

    @Override
    public CharSequence getTileLabel(){
        return  null;
    }

    @Override
    public void setListening(boolean listening){
    }

    @Override
    public void handleSetListening(boolean listening) {

    }



    @Override
    public QSTile.BooleanState newTileState() {
        mNeedsHandleUpdateState=true; // When tile is created or in phone config changes we want to update the state in handleUpdateState.
                                      // i have added spanish and english strings. In order to see if this work, change the languaje in your phone
                                      // If you are using an Android Studio Virtual Device to check your tile,
                                      // run the included app in you VD called Custom Locale to test it. NOTE: for spanish, select es-es in the app, since i added the spanish strings in values-es foleder!!




        return new QSTile.BooleanState();
    }



    @Override
    protected void handleClick(){

        /*
            We just change the tile state from enabled to disabled and we persist the state in settings. Then we refresh  the tile state and we configure the updateState var control
         */

        mIsEnabled=!mIsEnabled;
        mNeedsHandleUpdateState=true;
        persistTileState(mIsEnabled);
        refreshState(); // force tile updating. handleUpdateState will be called, so we want to update the passed state values (icon, text, state) to the method in order to the tile system to refresh everything.

    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {

        if(mNeedsHandleUpdateState){
            state.icon = ResourceIcon.get(mIsEnabled ? mIconOnId : mIconOffId);
            state.label = mContext.getString(mIsEnabled ? mStringOnId : mStringOffId);
            state.state = (mIsEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            mNeedsHandleUpdateState=false;
        }

        state.dualTarget=true; // new in samsung Q, we need to set it to true or the adapter will not be shown.

    }


        /* Tile overriden methods */




        /*** your methods */


    public void persistTileState(boolean enabled){
        try{  // catch the exception writting the settings key for not to have fcs in android studio virtual device or in your phone when testing the tile with this app

            Settings.System.putInt(mContext.getContentResolver(),MY_SETTINGS_KEY,mIsEnabled ? 1 : 0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpResources(){

        Resources resources = mContext.getResources();
        String pkgname = mContext.getPackageName();

        mStringOffId = resources.getIdentifier("templatetwo_tileoff","string",pkgname);
        mStringOnId = resources.getIdentifier("templatetwo_tileon","string",pkgname);
        mStringAdapterHeaderTitle = resources.getIdentifier("templatetwo_detail_header_label","string",pkgname);
        mIconOffId = resources.getIdentifier("template2_on","drawable",pkgname);
        mIconOnId = resources.getIdentifier("template2_off","drawable",pkgname);

        // let´s init our persisted state by reading our saved pref key in settings.

        int persistedvalue = 1;

        try {  // usually we do not get fc reading settings but writting. Anyway this is a good practice while developing your tile.

            persistedvalue = Settings.System.getInt(mContext.getContentResolver(),MY_SETTINGS_KEY,1); // by default we want state enabled..
        }catch (Exception e){
            e.printStackTrace();
        }

        mIsEnabled = ( persistedvalue == 1 ) ? true: false;

        // lets create our detail adapter

        mDetailAdapter = new TemplateTwoTile.MyAdapter();



    }



    /**************************************************************************************************************/
            /**************   TILE  DETAIL ADAPTER ******************************************/
    /**************************************************************************************************************/
    // FOR Q YOU HAVE TO PROVIDE A REAL DETAIL ADAPTER, EVEN A SIMPLE ONE WITH JUST TEST.

    // THIS EXAMPLE CODE WAS MADE FOR PIE - Q.

    // In Oreo and Pie SystemUI you can add options / information, whatever .. to the tile in such a way that when you press on the  tile text you can show this info or options.
    // This is called Detail Adapter. You design your detail adapter in a layout where you place yor info, options.
    // You have to implement the logic for your layout in your tile, in a class implementing the stock abstract class DetailAdapter included in systemui.
    // I have added support in this app to do this, reproducing the paths and logic used in SystemUI. You can find the base interface class DetailAdapter in com.android.systemui.plugins.qs.
    // I added to the abstract the methods we need, so we have to implement in our adapter class all those methods.

    // For the SystemUI to show this detail adapter when  we press the tile text you have to override the following method and to return a non null DetailAdapter object
                // public DetailAdapter getDetailAdapter()

    // SystemUI will wrap your detail adapter in a view containig a header (on top) and a buttons area (on bottom area). Between them your detail adapter view will be shown.

    // The header has got a String and a Switch. Both are optional. Usually, the String is the title of your adapter or the name of your tile.
    // Usually the switch is used to enable or disable the function that the tile carries on. It is used mostly for enabling or disabling what the tile does. When you press on it, the detail adapter
    // is kept on the screen, and the SystemUI logic performs some actions, as to call to your detail adapter saying "hey, the switch changed!!", then you can update the logic of your tile and adapter

    // The botton area will always show the "Done" button. It close the detail adapter.
    // Another button can be show, "Details" if your detail adapter returns a non null intent in a method called getSettingsIntent()



    // Let´s make a very simple detail adapter. We will add the header title, we will enable the header switch and the botton Details Button.
    // the switch will be synchronized with the tile state. So we can enable / disable our tile through it.
    // We are not going to add any view to our adapter in this example. In TemplateThreeTile we will add functionality to our adapter view.


    // let´s override getDetailAdapter method to return our adapter.

    @Override
    public DetailAdapter getDetailAdapter(){   // override this method if you are using a tile adapter
        return mDetailAdapter;
    }



     // this is our adapter implementation for this example

    public class MyAdapter implements DetailAdapter{  // our adapter class has to implement DetailAdapter

        int mTilteStringId = 0;  // id for the title of the adapter shown in the header

        /** methods that must be implemented */

        public CharSequence getTitle(){ //MANDATORY METHOD
            if(mTilteStringId==0) { // let´s look for the string id the first time we create the adapter, this is a just a way of doing this.
                mTilteStringId = mContext.getResources().getIdentifier("templatetwo_adapter_title","string",mContext.getPackageName());
            }
            return mContext.getString(mTilteStringId) ;
        }

        public Boolean getToggleState(){ //MANDATORY METHOD
                /*
                return a null if you want the switch not to be shown. If you are using the detail adapter header switch (for enable, disable
                 or for on -off state for example, return the corresponding Boolean value

                 So, if we return:
                    - null -> the switch is not shown
                    - Boolean(true) -> switch is shown and checked
                    - Boolean(false) -> switch is shown and unchecked

                  The toggle is enabled or disabled depending on the value returned in the below method getToggleEnabled

                  NOTE: In Q Samsung requires the the adapter view not to be null if you want the switch to be shown.

                 */

            return (mIsEnabled ? new Boolean(true) : new Boolean(false));
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup){  // MANDATORY METHOD
            // return null; // in this example we return a null view, so nothing will shown in the detail body.
                            // this will done for oreo and pie to show an example with header switch and no custom detail view.
            // In Q we have to return a view if we want to show the header switch.
            // So. If you return a non null in getToggleState and a detail view the header switch will be shown
            // if you return null in getToggleState but we return here a view the header switch will not be shown but the the adapter view will be.

            // we are using the sec_qs_detail_text.xml layout. This layout is present in s9 and s9 plus.

            int mLayoutId = mContext.getResources().getIdentifier("sec_qs_detail_text","layout",mContext.getPackageName());

            int mMessageId = mContext.getResources().getIdentifier("message","id",mContext.getPackageName());
            View myview =  LayoutInflater.from(mContext).inflate(mLayoutId,viewGroup,false);

            TextView textView = myview.findViewById(mMessageId);
            textView.setText("Example text");


            return myview;

        }


        public Intent getSettingsIntent(){// MANDATORY METHOD
            // you could return same intent than tile by getLongClickIntent() or a different one.
            // if not null the Details Button will be shown beside Done Button

            return getLongClickIntent(); // for this example we are returning same intent than long press on tile.
        }


        public void setToggleState(boolean state){ //MANDATORY METHOD  - in oreo and pie public boolean setToggleState(boolean state) ...

                /*
                This method is called when the adapter switch is enabled and the user changed the switch state.
                You have to add here your logic here if you need to do something (disable something, etc.)

                if you tile supports enable - disabled states, you should call to some method in your tile class to
                change whatever and to call to refreshstate. Or, for example you could hide or show views depending on the selected switch state.

                we return the resulting state of the switch if we want to override the user selection. Usually we will use this to update our tile logic

                 */


            mIsEnabled = state; // in this example we align the tile state with the user selection.
            persistTileState(state);// now we should persist our new state in this example.
            mNeedsHandleUpdateState=true; // we are trying to save some cpu cycles when the panel is pulled down and update state method is called. When the header switch changes we have to update this var.
            refreshState(); // we refresh our tile appearance and text!

        }

        public int getMetricsCategory(){ // MANDATORY METHOD
            return METRICS_CATEGORY;
        }





        /** Defined methods in DetailAdapter interface that can be overriden */

        @Override
        public boolean getToggleEnabled(){

                /*
                // if not overriden, the default value taken is true

                In this method we return if enabled or not. Enabled does not mean hidden.

                The final visibility of the toggle depends on the value returned in getToggleState method: If you return null not shown even if this method is returning true (default).

                The state of the swich will be taken from getToggleState if you return a true or false Boolean object.

                 */

            return true;  // we override this method in this example just to explain what is it for.
        }




    }






}
