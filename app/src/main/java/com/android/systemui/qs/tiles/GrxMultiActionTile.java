package com.android.systemui.qs.tiles;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;

import java.util.Arrays;

public class GrxMultiActionTile extends QSTileImpl<QSTile.BooleanState> {

    // constants

    public static final String TILE_PREF_KEY = "tma_config";
    public static final int METRICS_CATEGORY = 0x333;


    private Runnable DoubleClickRunnable;
    private Handler mHandler;
    private boolean mDoubleClickPending;
    private Long mLongClickTimeOut;
    private int mNumClicks;



    //private Context mContext;

    String[] mDrawableNames;
    String[] mTileTexts;
    public int mConfirmationMsgId;
    public int mNumActions;
    QSTile.Icon mCurrentTileIcon;
    String mCurrentTileText;
    int[] mSwitchesCurrentAction;
    int mAdapterTitleID;
    GrxMultiActionTile.GrxMultiUseTileDetailAdapter mDetailAdapter;

    public int mSelectionDialogTitleId;

    private boolean mShouldApplyWindowType= false;

    public int mSelectTextId;
    public int mRunTextId;


    //tests

    public boolean mNeedsHandleUpdateState=true;

    /******************************************************************************************/
    /*                             constructors                                               */
    /******************************************************************************************/


    public GrxMultiActionTile(QSHost host){
        super(host); // this call is needed for a right tile init.
        setUpResources();  // call to the method initializing your vars, etc.
    }



    /******************************************************************************************/
    /****************  FAKE METHODS - SIMULATION METHODS **************************************/
    /*** YOU SHOULD REMOVE THEM FROM GENERATED SMALI CODE WHEN ADDING TO UI!!!!  */
    /******************************************************************************************/

    /*
    public GrxMultiActionTile(Context context){  // fake constructor for simulations

        mContext=context;
        setUpResources();
    }

*/
  /*  public void simulateUpdateState(BooleanState state){
        handleUpdateState(state, null);
    }
*/
 //   public void simulateLongClick(){handleLongClick();}

 //   public void simulateClick(){handleClick();}


    /******************************************************************************************/
    /************** Methods to be implemented  - abstracts in QsTileImple **********************/
    /******************************************************************************************/

    @Override
    public Intent getLongClickIntent(){
        return null;  // add the code to return the right intent for your tile, or return null if you want to customize your long click
    }

    @Override
    public int getMetricsCategory(){
        return METRICS_CATEGORY;
    }

    @Override
    public CharSequence getTileLabel(){
        return mCurrentTileText;
    }

    @Override
    protected void handleClick(){

        if(mHandler ==null) setUpDoubleClick();
        if(DoubleClickRunnable ==null) handleRunAction(mSwitchesCurrentAction[mNumActions]);
        else{
            mNumClicks++;
            if(!mDoubleClickPending){
                mHandler.removeCallbacks(DoubleClickRunnable);
                mDoubleClickPending =true;
                mHandler.postDelayed(DoubleClickRunnable, mLongClickTimeOut);
            }
        }
    }

    @Override
    public void handleSetListening(boolean listening) {  // pie compatibility

    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {  // called when you pull down the panel or if state changed
        if(mNeedsHandleUpdateState) {
            state.state = Tile.STATE_ACTIVE;
            state.icon=mCurrentTileIcon;
            state.label=mCurrentTileText;
            mNeedsHandleUpdateState=false;
        }
        state.dualTarget=true; // new in samsung Q, we need to set it to true or the adapter will not be shown.

    }

    /* add/check this method in generated smali if something went wrong and adapt it with the class name (GrxMultiuseTile)

        .method protected bridge synthetic handleUpdateState(Lcom/android/systemui/plugins/qs/QSTile$State;Ljava/lang/Object;)V
            .locals 0

            check-cast p1, Lcom/android/systemui/plugins/qs/QSTile$BooleanState;

            invoke-virtual {p0, p1, p2}, Lcom/android/systemui/qs/tiles/GrxMultiActionTile;->handleUpdateState(Lcom/android/systemui/plugins/qs/QSTile$BooleanState;Ljava/lang/Object;)V

            return-void
        .end method


     */


    @Override
    public QSTile.BooleanState newTileState() {
        Log.d("GrxMultiAction ", "newTileState");
        mNeedsHandleUpdateState=true;
        return new QSTile.BooleanState();
    }



    /******************************************************************************************/
    /**********     methods to override   - i have not added all existing in qstileimp     ****/
    /****** but you can see + -  what is called in the tile  *********************************/
    /******************************************************************************************/

    @Override
    protected void handleLongClick(){   // overrides this method if you want to control longclick
        int currentaction = mSwitchesCurrentAction[mNumActions];
        currentaction++;
        if((currentaction >= mNumActions) ) currentaction = 0;
        mSwitchesCurrentAction[mNumActions] = currentaction;
        setPreferenceKey();
        setCurrentTileIconAndText();
        mNeedsHandleUpdateState=true;
        refreshState();
    }


    @Override
    public DetailAdapter getDetailAdapter(){   // override this method if you are using a tile adapter


        return mDetailAdapter;

    }

    public int getLoggingValue(){return 1;}     // add this method, it is a shitsung thing...

    @Override
    public void setListening(boolean listening){
        Log.d("GrxMultiAction ", "setlistening");
    }



    @Override
    public boolean isAvailable() {  // if this return false then tile is not shown. Add there your conditions for enabling or not the tile.
        return true;
    }

    protected void handleDestroy(){

    }


    protected void handleSecondaryClick(){
        showDetail(true);  // add in this method what you want to do when tile text is clicked. If you call to that showdetail(true) then the tile detail is shown
    }


    // there are more method you could override depending on what your tile does:    refreshState() clearState() , userSwitch(int newUserId) destroy() getstate..
    // look for aosp code for QsTileImpl.java and have a look to the samsung QstileImpl.smali..


    /************************************************************************************************/

    /*********                       YOUR METHODS      **********************************************/
    /************************************************************************************************/

    private void setUpDoubleClick(){
        mHandler = new Handler();
        mLongClickTimeOut = Long.valueOf(ViewConfiguration.getDoubleTapTimeout());
        mDoubleClickPending =false;

        DoubleClickRunnable = new Runnable() {
            @Override
            public void run() {
                if(!mDoubleClickPending){
                    actionClick();
                }else {
                    if(mNumClicks ==0) actionClick();
                    else if(mNumClicks !=2) {
                        actionClick();
                    }else {
                        actionDoubleClick();
                    }

                }
            }
        };
    }


    private void handleRunAction(int action){

        //int currentaction = mSwitchesCurrentAction[mNumActions];
        boolean requereconfirmation = (mSwitchesCurrentAction[action]==1 ? true : false);

        if(requereconfirmation && action>1 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mTileTexts[action]);
            builder.setMessage(mConfirmationMsgId);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    runCurrentAction(action);
                }
            });

            AlertDialog alertDialog = builder.create();
            if(mShouldApplyWindowType) {
                Window window = alertDialog.getWindow();
                window.setType(0x7d9);
            }

            alertDialog.show();
        }else runCurrentAction(action);
    }



    private void actionClick(){
        mNumClicks =0;
        mDoubleClickPending =false;
        mHandler.removeCallbacks(DoubleClickRunnable);
        handleRunAction(mSwitchesCurrentAction[mNumActions]);
    }

    private void actionDoubleClick(){
        mNumClicks =0;
        mDoubleClickPending =false;
        mHandler.removeCallbacks(DoubleClickRunnable);
        runActionSelectionDialog();

    }

    public int mSelectRunItem;



    private void runActionSelectionDialog(){
        mSelectRunItem = mSwitchesCurrentAction[mNumActions];

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mSelectionDialogTitleId);
        builder.setSingleChoiceItems(mTileTexts, mSwitchesCurrentAction[mNumActions], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectRunItem=which;
            }
        });

        builder.setPositiveButton(mRunTextId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleRunAction(mSelectRunItem);
            }
        });
        builder.setNegativeButton(mSelectTextId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSwitchesCurrentAction[mNumActions] = mSelectRunItem;
                setPreferenceKey();
                setCurrentTileIconAndText();
                mNeedsHandleUpdateState=true;
                refreshState();

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        AlertDialog alertDialog = builder.create();
        if(mShouldApplyWindowType) {
            Window window = alertDialog.getWindow();
            window.setType(0x7d9);
        }
        alertDialog.show();

        if(!mShouldApplyWindowType) Toast.makeText(mContext,"double click",Toast.LENGTH_SHORT).show();
    }


    /**** final actions - some have to be made in smali **/

    private void showGlobalActions(){
        //Toast.makeText(mContext,"global actions",Toast.LENGTH_SHORT).show();
        mHost.collapsePanels();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction("com.mods.grx.SERVICES");
                intent.putExtra("ACTION", "GLOBALACTIONS");
                mContext.sendBroadcast(intent);
            }
        };

        new Handler().postDelayed(runnable,300);

    }





    private void gotoRecovery(){
        if(!mShouldApplyWindowType) Toast.makeText(mContext,"Recovery",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.mods.grx.SERVICES");
        intent.putExtra("ACTION", "RECOVERY");
        mContext.sendBroadcast(intent);
    }

    private void gotoDownLoad(){
        if(!mShouldApplyWindowType) Toast.makeText(mContext,"Download",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.mods.grx.SERVICES");
        intent.putExtra("ACTION", "DOWNLOAD");
        mContext.sendBroadcast(intent);
    }

    private void powerOff(){
        if(!mShouldApplyWindowType) Toast.makeText(mContext,"Power Off",Toast.LENGTH_SHORT).show();
        /*  Replace the method in smali by the following code

.method private powerOff()V
	.locals 2

	const-string/jumbo v0, "statusbar"

	:try_start_0

    invoke-static {v0}, Landroid/os/ServiceManager;->getService(Ljava/lang/String;)Landroid/os/IBinder;

    move-result-object v0

    invoke-static {v0}, Lcom/android/internal/statusbar/IStatusBarService$Stub;->asInterface(Landroid/os/IBinder;)Lcom/android/internal/statusbar/IStatusBarService;

    move-result-object v0

	const/4 v1, 0x0

	invoke-interface {v0}, Lcom/android/internal/statusbar/IStatusBarService;->shutdown()V

    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    return-void

	:catch_0

	move-exception v0

	return-void

.end method



         */
    }


    private void restartPhone(boolean safemode){
        if(!mShouldApplyWindowType) {
            if(!safemode) Toast.makeText(mContext,"Restart Phone",Toast.LENGTH_SHORT).show();
            else Toast.makeText(mContext,"Restart Safe Modee",Toast.LENGTH_SHORT).show();
        }

        /** replace this method in smali with the folloting code

.method private restartPhone(Z)V
    .locals 2

    const-string/jumbo v0, "statusbar"

    :try_start_0

    invoke-static {v0}, Landroid/os/ServiceManager;->getService(Ljava/lang/String;)Landroid/os/IBinder;

    move-result-object v0

    invoke-static {v0}, Lcom/android/internal/statusbar/IStatusBarService$Stub;->asInterface(Landroid/os/IBinder;)Lcom/android/internal/statusbar/IStatusBarService;

    move-result-object v0

    invoke-interface {v0, p1}, Lcom/android/internal/statusbar/IStatusBarService;->reboot(Z)V

    :try_end_0
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_0} :catch_0

    return-void

    :catch_0

    move-exception v0

    return-void

.end method


         */

    }


    private void restartUI(){
        //if(!mShouldApplyWindowType) Toast.makeText(mContext,"Restart UI",Toast.LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void screenOff(){
        if(!mShouldApplyWindowType) Toast.makeText(mContext,"Screen Off",Toast.LENGTH_SHORT).show();
        /* Replace the method in smali
.method private screenOff()V
    .locals 4

    iget-object v0, p0, Lcom/android/systemui/qs/tiles/GrxMultiActionTile;->mContext:Landroid/content/Context;

    const-string/jumbo v1, "power"

    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/os/PowerManager;

    :try_start_0

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v2

    invoke-virtual {v0, v2, v3}, Landroid/os/PowerManager;->goToSleep(J)V

    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    :goto_0
    return-void
    :catch_0
    move-exception v0

    invoke-virtual {v0}, Landroid/os/RemoteException;->printStackTrace()V

    goto :goto_0
.end method


         */

    }

    public void runCurrentAction(int action){
        //int currentaction = mSwitchesCurrentAction[mNumActions];
        switch (action){
            case 0: screenOff();
                    break;
            case 1 : showGlobalActions();
                     break;
            case 2: gotoRecovery();
                    break;
            case 3: restartPhone(false);
                    break;
            case 4: powerOff();
                    break;
            case 5: gotoDownLoad();
                    break;
            case 6: restartUI();
                    break;
            case 7: restartPhone(true);
                break;
             default:break;
        }

    }


    private void setUpResources(){

        mNumClicks =0;

        mDetailAdapter = new GrxMultiActionTile.GrxMultiUseTileDetailAdapter();

        Resources resources = mContext.getResources();
        String packagename = mContext.getPackageName();

        mShouldApplyWindowType = (packagename.equals("com.android.systemui"));

        int id = resources.getIdentifier("tmu_drawable_names","array",packagename);
        mDrawableNames = resources.getStringArray(id);

        mSelectionDialogTitleId= resources.getIdentifier("tmss_select_action_title","string",packagename);

        id = resources.getIdentifier("tmu_titles","array",packagename);

        mNumActions = mDrawableNames.length;

        mTileTexts = resources.getStringArray(id);
        mConfirmationMsgId = resources.getIdentifier("tmus_msgdialog","string",packagename);

        mAdapterTitleID = resources.getIdentifier("tmus_adaptertitle","string",packagename);

        mSelectTextId =resources.getIdentifier("tmus_select","string",packagename);
        mRunTextId = resources.getIdentifier("tmus_run","string",packagename);


        mSwitchesCurrentAction = new int[mNumActions+1];  // last "switch" will be the current selected option. We will only use 1 key in settings to save switches state and current selected action
        initConfigArray();

        readSettingsKey();
        setCurrentTileIconAndText();

    }

    public void readSettingsKey(){  // read settings key containing switches saved states and last tile action configured
        boolean result=true;
        String prefvalue="";
        String[] array;
        int numelements = mNumActions+1;
        try{
            prefvalue = Settings.System.getString(mContext.getContentResolver(),TILE_PREF_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(prefvalue==null || prefvalue.isEmpty()) result = false;
        else {
            array = prefvalue.split(";");
            if(array.length<(numelements)) result = false;
            else {
                for(int i = 0;i<(numelements);i++){
                    mSwitchesCurrentAction[i] = Integer.valueOf(array[i]);
                }
                result = true;
            }

        }
        if(!result) initConfigArray();
    }

    private void initConfigArray(){
        Arrays.fill(mSwitchesCurrentAction,1); // let´s init the array, just in case we have problems reading the settings key.
        mSwitchesCurrentAction[0] = 0;
        mSwitchesCurrentAction[1] = 0;
        mSwitchesCurrentAction[mNumActions]=2; // default selection

    }


    public void setPreferenceKey(){  // we save in settings current config  -> "first switch value";"second switch" ... "last switch";"current selected action"
        String value ="";

        for(int i=0;i<mSwitchesCurrentAction.length;i++){
            value+=String.valueOf(mSwitchesCurrentAction[i]);
            value+=";";
        }

        try{
            Settings.System.putString(mContext.getContentResolver(),TILE_PREF_KEY,value);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void setCurrentTileIconAndText(){
        int currentposition = mSwitchesCurrentAction[mNumActions];
        int id=  mContext.getResources().getIdentifier(mDrawableNames[currentposition],"drawable",mContext.getPackageName());
        mCurrentTileIcon= ResourceIcon.get(id);
        mCurrentTileText = mTileTexts[currentposition];
    }






        /******************  ADD YOUR ADATER CLASS AND ITS METHODS AND LOGIC ***/


    public class GrxMultiUseTileDetailAdapter implements DetailAdapter {

        int mLayoutId = 0;

        CompoundButton.OnCheckedChangeListener mListener;

            public CharSequence getTitle(){ //MANDATORY METHOD

                return mContext.getString(mAdapterTitleID) ;
            }


        public View createDetailView(Context context, View view, ViewGroup viewGroup){  // MANDATORY METHOD

                /// !! PAY ATTENTION IN YOUR DETAIL ADAPTERS VIEWS TO USED SYTLES IN YOUR ITEMSSSSS !!

            // I AM USING SAME STYLES THAN S9 / PLUS EXISTING STYLES IN SYSTEMUI.
            // SO, HAVE A LOOK TO YOUR SYSTEMUI STYLES AND ADD THE NEEDED TO THIS APK IF YOU WANTT TO.


            if(mLayoutId==0) {
                mLayoutId = mContext.getResources().getIdentifier("qs_detail_multiaction","layout",mContext.getPackageName());
            }
            if(mListener==null){
                mListener=new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String tag = (String) compoundButton.getTag();
                        mSwitchesCurrentAction[Integer.valueOf(tag)] = (b) ? 1 : 0;
                        setPreferenceKey();
                    }
                };
            }


            View myview =  LayoutInflater.from(mContext).inflate(mLayoutId,viewGroup,false);

            for(int i = 0; i<mNumActions;i++){
                Switch mswitch = (Switch) myview.findViewWithTag(String.valueOf(i));
                if(mswitch!=null) {
                    mswitch.setChecked(mSwitchesCurrentAction[i] == 1 ? true : false);
                    mswitch.setOnCheckedChangeListener(mListener);
                }

            }
            return myview;
        }


            public Intent getSettingsIntent(){
                // MANDATORY METHOD - you could return same intent than tile by getLongClickIntent() or a different one. if not null the Details Button will be shown beside Done Button

                return null;
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

                  IN Q we also would need the detail view not to be null in order the header switch to be shown.

                 */

               return null;
            }

            public void setToggleState(boolean state){ //MANDATORY METHOD

            }

            public int getMetricsCategory(){ // MANDATORY METHOD
                return METRICS_CATEGORY;
            }

            /** Defined methods in DetailAdapter interface that can be overriden */

            @Override
            public boolean getToggleEnabled(){

                /*
                // if not overriden, the default value taken is true

                In this method we return if enabled or not.

                The visibility of the toggle depends on the value returned in getToggleState method:

                 */

                return false;
            }


            /*** here add your methods if needed */














    }

}
