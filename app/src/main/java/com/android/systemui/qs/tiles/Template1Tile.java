package com.android.systemui.qs.tiles;
/* 

Created by Grouxho on 28/12/2018. 

*/

/*

Template1Tile:

- Simple on-off tile

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

public class Template1Tile extends QSTileImpl<QSTile.BooleanState> {

    // constructor

    public Template1Tile(QSHost host){
        super(host); // this call is needed for a right tile init.
       // setUpResources();  // You could call here to the method initializing your vars, etc.
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
        // this is the basic implementation. Not sure what is this for, but you have to implement it. return any number
    }

    @Override
    public CharSequence getTileLabel(){

        /*

            This is the tile label

            However, the updating of the tile text is done in handleUpdateState method. (see below)

         */
        return null;
    }




    @Override
    protected void handleClick(){

        /*

            This is the method called when user click on the tile.


         */

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

        You are returning here the BooleanState object that will be managed in QSTileImpl. This boolean state object will be the one passed
        to the handleupdatestate method.

        Look below in the handleUpdateState(BooleanState state, Object arg) method for the explanations i wrote about this.

        This method could be very useful to know when to update resources, like tile strings languaje based. You could, for example, to keep a boolean variable indicating if passed state to handleUpdateState needs or not to be updated.

        In short, you can take advantage of the call to this method to update the resources used in your tile (icons, string used in gettilelabel method, etc.)

         */

        return new QSTile.BooleanState();  // this is the basic implementation of this method
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

         */
    }



    @Override
    protected void handleLongClick(){
        /*
            If you do not override this method then the stock implementation will call to getLongClickIntent() and it will
            launch the intent if it is not null.

            Override this method to customize long click behavior

         */


    }



    }
