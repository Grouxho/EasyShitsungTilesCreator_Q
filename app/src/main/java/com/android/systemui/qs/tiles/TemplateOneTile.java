package com.android.systemui.qs.tiles;
/* 

Created by Grouxho on 28/12/2018. 

*/

/*

TemplateOneTile:

- Simple on-off tile
- Introducing main Tiles logic and methods
- Introducing Tile Simulator (at the end of the class)
- How to add your tiles to SystemUI (at the end of the class)

 */

import android.content.Intent;
import android.service.quicksettings.Tile;

import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;


/*

 - your tile class name has to finish in "Tile"
 - your tile classes has to extend QSTileImpl<QSTile.BooleanState>

  */

public class TemplateOneTile extends QSTileImpl<QSTile.BooleanState> {

    /*** add your vars - the following vars are for this basic tile example */

    private int mIconId;
    private int mTileStringId;

    private boolean mIsEnabled = false;

    /******/



    // constructor

    public TemplateOneTile(QSHost host){
        super(host); // this call is needed for a right tile init.
        setUpResources();  // You could call here to the method initializing your vars, etc.
    }



    /*
    *
        ***********************************************************************************************************

        The following methods are a must. They are the implementation of the abstract methods you see in QsTileImpl.

        ***********************************************************************************************************
     */



    @Override
    public Intent getLongClickIntent(){

        /*
            This method is called when we long press on the tile.

            Return null if you want nothing to happen or the intent you want to be run.

         */

        return null;
    }

    @Override
    public int getMetricsCategory(){

        return 663;
        // this is the basic implementation. Not sure what is this for, but you have to implement it. return any number. Have a look to the existing used metrics categories in the tiles of your systemui and use any different one.
    }

    @Override
    public CharSequence getTileLabel(){

        /*

            This is the tile label

            However, the updating of the tile text is done in handleUpdateState method. (see below)

         */

        /*

        in the example we are going to return here our tile text.

         */
        return  mContext.getString(mTileStringId);
    }








    @Override
    public void setListening(boolean listening){

        /*
               this method is called when tile is created, or when tile is destroyed, or when the tile is refreshed.
               You could use this for to unregister observers, etc. I have not used it never.
         */

    }

    @Override
    public void handleSetListening(boolean listening) {

        //  this method is needed for pie compatibility. In oreo this is not called it does not exists.

        //same than oreo setlistening

    }



    @Override
    public QSTile.BooleanState newTileState() {
        /*

        This method is called when the tile is created, when tiles order are customized  or when configuration changed (screen rotation, languaje change
        screen resolution changes, etc) through the clearstate method in qsimpl.

        After this method is called, for sure handleupdateState methiod is called.

        Finally, remember than when you rotate the screen in your phone, running activities are recreated and the tile will be also recreated.

        You are returning here the BooleanState object that will be managed in QSTileImpl. This boolean state object will be the one passed
        to the handleupdatestate method.

        Look below in the handleUpdateState(BooleanState state, Object arg) method for the explanations i wrote about this.

        This method could be very useful to know when to update resources, like tile strings languaje based. You could, for example, to keep a boolean variable indicating if passed state to handleUpdateState needs or not to be updated.

        In short, you can take advantage of the call to this method to update the resources used in your tile (icons, string used in gettilelabel method, etc.)

         */

        return new QSTile.BooleanState();  // this is the basic implementation of this method
    }



    @Override
    protected void handleClick(){

        /*

            This is the method called when user click on the tile.
            Here usually we add the main logic of our tile.

         */

          /* In this example we just change the tile state from enable to disable.


            In this example we do not persist the current state, it will be always disable by default when we run this app. You should store your current state in system settings (or global or secure)
                This app generate code compatible with samsung oreo and pie. Since API 23 only privileged apps can write values in settings system. You can add the code here, but unless you install the app in
                priv-app the values will be not saved. Wrap your code for writting and reading settings keys in try-catch to avoid fcs of this app. For sure the code will work when you install the generated
                smalies in your system ui.


             if you need your tile to be updated, as in this example, just call to the refresState method .

            */

        mIsEnabled=!mIsEnabled;
        refreshState(); //in this example, we need to refresh the state of the tile in order to update tile string, tile icon, icon color , etc..

    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        /*

            a.- What is this method for

            The state of the tile is managed in QsTileImpl (the parent class). After this method is called, the system will
            draw the icon, will update the state and will draw the string found in the passed state object.
            So, we can control icon, text and state shown to the user by modifying the state object.

            b.- When this method is called

            - this method is called many times. When refreshing the tile state, when the user pulls down the panel, after a configuration change
            (languaje, orientation, display density, etc.) ...

            - We can force this method call by calling to refreshState() method. (this method is implemented, if you do not override it, in QsTileImpl)

            c.- What should we do inside this method

            You should updated the passed BooleanState object in the call, this is state.
            Basic things to be updated in state var are: icon, string and state (enabled, disabled, unavailable).

            To do so, write something like this:
                    state.state = Tile.STATE_ACTIVE for example;
                    state.icon= the QSTile.Icon to show
                    state.label= the text to show;


            You will see in most of the tiles (aosp sources or samsung smalies) that these values are updated everytime the method is called.

            d.- Optimization trick

            However, you could save some cpu processing by using a control variable, let´s say for example mNeedsHandleUpdateState boolean type.
            We control the value of this variable to true or false depending on when we want to update the state object passed to this method.

            .. if (mNeedsHandleUpdateState){
                    //update the state (the state is kept in QsTileImpl

                    state.state -> mycurrent state
                    state.icon ..
                    state.label..
                    mNeedsHandleUpdateState = false;  //next time we do not need to update the state, saving some cpu cycles.

                    }

             Have a look to the  newTileState method explanations above to have a better idea about how you could optimize this.
             If for example you in that method set our example control var ( mNeedsHandleUpdateState ) to false, then in this method you will update
             the tile resources.  If when you call to the refreshState() you also set this control var to false, then you have full control on when ot update state object.
             This is just an idea, of course.

         */


            /* in this template example we are going to use the common way you see in most of the tiles in systemui
                Every time this method is called we get the icon and the tile string and we put them in state var. We will also update the state

                In this example we do not persist the current state, it will be always disable by default when we run this app. You should store your current state in system settings (or global or secure)
                This app generate code compatible with samsung oreo and pie. Since API 23 only privileged apps can write values in settings system. You can add the code here, but unless you install the app in
                priv-app the values will be not saved. Wrap your code for writting and reading settings keys in try-catch to avoid fcs of this app. For sure the code will work when you install the generated
                smalies in your system ui.


             */

            state.state = mIsEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;  // let´s say the tile host what is the state.
            state.icon = ResourceIcon.get(mIconId);   // ResourceIcon is a method implemented in QsTileImp that returns the Icon object, just need to call it with the icon drawable id we want to use.
            state.label = mContext.getString(mTileStringId);

            /*

            Done, this is a very simple working tile, it only changes its state of course.  In this way, if you added support for several languajes for the tile text, you are sure
            that the right one will be used, because we are looking for it everythime we the method is called.

             */


    }



    /*
        ********************************************************************************
                    Methods useful to override
        ********************************************************************************

     */

    @Override
    protected void handleDestroy(){
        super.handleDestroy();
        // Add here the code you want to run when tile is destroyed.
        // For example to unregister callbacks, unregister observers, etc.

    }

    @Override
    public boolean isAvailable() {
        return true;

        /**
         * Is a startup check whether this device currently supports this tile.
         * Should not be used to conditionally hide tiles.  Only checked on tile
         * creation or whether should be shown in edit screen.

         You can override it to show or not the tile depending on some boot conditions for example.

         Remember than when you rotate the screen in your phone, running activities are recreated and the tile will be also recreated, so, you could enable or disable the tile, for example, depending
         on the current orientation, or other things. But .. usually we will return true, obviously.

         */
    }



    @Override
    protected void handleLongClick(){
        /*
            If you do not override this method then the stock implementation will call to getLongClickIntent() and it will
            launch the intent if it is not null.

            Override this method to customize long click behavior

         */

        /*
         in this example we are going to launch Settings app.
         */

        try{

            mContext.startActivity(new Intent("android.settings.SETTINGS"));

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    /************************************************************************/

    /**   Your specific logic here  *******************************************/

    /**************************************************************************/

    /** In this template we just are going to change the state of the tile from enable to disable.
       In handleUpdateState we will check the current state adn we will change it.
     */

    /** vars with string and icon ids */
    /* in smali you should match the ids in code. If you use this app to create tiles, find the ids by code. Most of the times you only need this when tile is created, but you could
    add more complex logic depending on configuration changes, observers, etc... Doing this in java with this tool will make it much easier than in smali, obviously.
     */



    private void setUpResources(){

        mIconId = mContext.getResources().getIdentifier("template1", "drawable", mContext.getPackageName());
        mTileStringId = mContext.getResources().getIdentifier("template1_tiletext", "string", mContext.getPackageName());

    }

    }


    /***********************************************************************************************************/
    /***********************************************************************************************************/
    /***********************************************************************************************************/

    /*          HOW TO TEST YOUR TILE IN THE SIMULATOR                                                         */

    /***********************************************************************************************************/
    /***********************************************************************************************************/
    /***********************************************************************************************************/

    /*

    Edit MainActivity.java in Android Studio, then add the line in Oncreate method. I have indicated the exact place to add it.

            new GrxTileSimulator(this, new TemplateOneTile(mQsTileHost),mTilesContainer);

    Then run the app: Run - > Run app

    Of course while developing your tile code use debugger etc... I think i have added all checks to avoid fcs in the app, but... you never know.

    Do the same for any tile you want to build and test, changing its class name obviously.. :)

     */



    /***********************************************************************************************************/
    /***********************************************************************************************************/
    /***********************************************************************************************************/

    /*          HOW TO BUILD AND TO INSTALL YOUR TILE IN SYSTEMUI                                               */

    /***********************************************************************************************************/
    /***********************************************************************************************************/
    /***********************************************************************************************************/


    /*

    If you followed the instructions of this template example, the app will generate 100% compatible smali code for current Samsung Oreo and PIE firmware.

    - Once you have tested your tile logic in the simulator, build the app: Build -> Build Bundle(s) / App(s) -> Build APK(s)

    - An app-debug.apk will be generated. Take it and decompile it using the apktool.jar tool you use.

    - Go to smali - com - android -system - qs - Tiles and take all the smalies belonging to your tile. Put them all in your decompiled SystemUI.
    If you need to add some smali code to your generated code, do it. As you know there are some classes in the API that is not accessible in Android Studio but in SystemUI
     (a lot in fact, local services, some system services, and many things related to permissions that are not granted in non privileged apps).
     In another moment i will write a guide to use hidden api classes in android studio.. :)

    - Add to your SystemUi all the resources you use to build your tile: pngs, strings, arrays, layouts, etc..

    - Enable the tile in systemui and in CSC (in sammy phones..)

    To do so,

            - Add your tile to the systemui string called <string name="quick_settings_tiles_default"> . Remember that your tile class has to be named finishing with the Tile word. However, in this
            string you will remove the Tile from the class name. So, it should look something like this:

            <string name="quick_settings_tiles_default">TemplateOne, Wifi, ...

            Declaring the tile in this way will make the tile to be shown in clean rom installations. You can try to force the tile to be shown by using a settings editor apk and deleting from
            secure table the key "sysui_qs_tiles" and rebooting the phone


            - To test not on clean (this also will work on clean installations, of course) in samsung phones add to your cscfeatures.sml or others.xml edit / add a line containing our tile

            <CscFeature_SystemUI_ConfigDefQuickSettingItem>TemplateOne,Wifi .... </CscFeature_SystemUI_ConfigDefQuickSettingItem>

            if you have not got that line, add it, and add all tiles you see in the ui string quick_settings_tiles_default to test..


            - Finally, shitlagsungwiz analitycs throws a logcat error line everytime you click on the tile if you do not add our tile to its analytics framework... This is not important,
            you will see that line if you use any custom tile...even samsung custom tiles... Well, nothing happens with this, of course, but if you want to avoid that line, you have to add our tile
            to the SystemUI array called  <string-array name="tile_ids">

            Add your tile there. For this example, add this TemplateOne tile like this..

             <string-array name="tile_ids">
             ...

                     <item>TemplateOne</item>
                     <item>123</item>
                     <item>1234</item>
                      <item>1234</item>

              </string-array name="tile_ids">

            use numbers not used by other tiles and done.



            Time to compile your UI and test!!!


     */