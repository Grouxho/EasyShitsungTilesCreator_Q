package com.android.systemui.qs.tileimpl;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.service.quicksettings.Tile;
import android.util.ArraySet;
import android.util.SparseArray;

import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;

import java.util.ArrayList;

public abstract  class QSTileImpl<TState extends QSTile.State> implements QSTile{

    protected  QSHost mHost;
    protected final Context mContext;

    /*********************/
    /*** FAKE METHODS FOR SIMULATION ***/

    /*    You have to add them in your Tile code in order to use TileSimulator */

    /*  Remove the generated code of the tiles from the smalies if you want to */


    //public abstract void simulateUpdateState(BooleanState state);

    //public abstract void simulateLongClick();

    public void simulateLongClick(){
        handleLongClick();
    }

    public void simulateClick(){

        if(mState!=null && mState.state!= Tile.STATE_UNAVAILABLE) handleClick();
    };



    /*********************/


    protected TState mState = newTileState();
    private TState mTmpState = newTileState();

    private final Object mStaleListener = new Object();
    private final ArraySet<Object> mListeners = new ArraySet<>();

    public abstract Intent getLongClickIntent();

    abstract public int getMetricsCategory();

    public abstract CharSequence getTileLabel();

    abstract protected void handleClick();

    protected abstract void handleSetListening(boolean listening);

    abstract protected void handleUpdateState(TState state, Object arg);

    public abstract TState newTileState();

    protected abstract void setListening(boolean listening);


    public void showDetail(boolean show) {

    }

    public boolean isAvailable(){
        return false;
    }


    public DetailAdapter getDetailAdapter(){
        return null;
    }

    protected void handleClearState() {
        mTmpState = newTileState();
        mState = newTileState();
    }


    public void simulateClearState(){

        handleClearState();
        refreshState();


    }

    protected void handleRefreshState(Object arg) {
        handleUpdateState(mTmpState, arg);
        final boolean changed = mTmpState.copyTo(mState);
        if (changed) {
            handleStateChanged();
        }
/*        mHandler.removeMessages(H.STALE);
        mHandler.sendEmptyMessageDelayed(H.STALE, getStaleTimeout());*/

        setListening(mStaleListener, false);

    }

    private final ArrayList<Callback> mCallbacks = new ArrayList<>();

    private void handleStateChanged() {

        if (mCallbacks.size() != 0) {
            for (int i = 0; i < mCallbacks.size(); i++) {
                mCallbacks.get(i).onStateChanged(mState);
            }
                }

    }

    /** samsung stuff */

    public int getLoggingValue(){
        return 1;
    }

    private void handleAddCallback(Callback callback) {
        mCallbacks.add(callback);
        callback.onStateChanged(mState);
    }

    private void handleRemoveCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    private void handleRemoveCallbacks() {
        mCallbacks.clear();
    }

  //  public QSTileImpl(){}

    public QSTileImpl(QSHost host) {
        mHost = host;
        mContext = host.getContext();
   //     handleStale(); // Tile was just created, must be stale.
    }

    protected void handleStale() {
        setListening(mStaleListener, true);
    }

    public void setListening(Object listener, boolean listening) {
        if (listening) {
            if (mListeners.add(listener) && mListeners.size() == 1) {
            //    if (DEBUG) Log.d(TAG, "setListening " + true);
              //  mHandler.obtainMessage(H.SET_LISTENING, 1, 0).sendToTarget();
                refreshState(); // Ensure we get at least one refresh after listening.
            }
        } else {
            if (mListeners.remove(listener) && mListeners.size() == 0) {
             //   if (DEBUG) Log.d(TAG, "setListening " + false);
             //   mHandler.obtainMessage(H.SET_LISTENING, 0, 0).sendToTarget();
            }
        }
    }

    /*** simulator stuff added for simulating refresh state */

    public interface SimulateRefreshState{
        public void onTileRefreshed(State state);

       // public void onToggleStateChanged(boolean state);
    }

    SimulateRefreshState mCallback = null;

    public void fireToggleStateChanged(boolean state) {

    }

    public void setSimulateRefresCallBack(SimulateRefreshState callBack){
        mCallback = callBack;
    }

    /****
     *
     */
    /**
     * Is a startup check whether this device currently supports this tile.
     * Should not be used to conditionally hide tiles.  Only checked on tile
     * creation or whether should be shown in edit screen.
     */

    protected void handleDestroy() { // you could override it
        if (mListeners.size() != 0) {
            handleSetListening(false);
        }
        mCallbacks.clear();
    }


    public void refreshState() {
        refreshState(null);
    }



    public boolean mDualTarget = false; // SIMULATING dualTarget - ONLY FOR Q in samsung.



    protected final void refreshState(Object arg) {
        handleUpdateState(mState,arg);

        mDualTarget  = mState.dualTarget; // SIMULATING dualTarget - ONLY FOR Q in samsung.

        if(mCallback!=null) mCallback.onTileRefreshed(mState);
     //   mHandler.obtainMessage(H.REFRESH_STATE, arg).sendToTarget();
    }


    protected void handleLongClick() {
        Intent intent = getLongClickIntent();
        if(intent!=null){
            try{

                mContext.startActivity(intent);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
     //   Dependency.get(ActivityStarter.class).postStartActivityDismissingKeyguard(
       //         getLongClickIntent(), 0);
    }

    public static class DrawableIcon extends Icon {
        protected final Drawable mDrawable;
        protected final Drawable mInvisibleDrawable;

        public DrawableIcon(Drawable drawable) {
            mDrawable = drawable;
            mInvisibleDrawable = drawable.getConstantState().newDrawable();
        }

        @Override
        public Drawable getDrawable(Context context) {
            return mDrawable;
        }

        @Override
        public Drawable getInvisibleDrawable(Context context) {
            return mInvisibleDrawable;
        }
    }

    public static class ResourceIcon extends Icon {
        private static final SparseArray<Icon> ICONS = new SparseArray<Icon>();

        protected final int mResId;

        private ResourceIcon(int resId) {
            mResId = resId;
        }

        public static Icon get(int resId) {
            Icon icon = ICONS.get(resId);
            if (icon == null) {
                icon = new ResourceIcon(resId);
                ICONS.put(resId, icon);
            }
            return icon;
        }

        @Override
        public Drawable getDrawable(Context context) {
            return context.getDrawable(mResId);
        }

        @Override
        public Drawable getInvisibleDrawable(Context context) {
            return context.getDrawable(mResId);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ResourceIcon && ((ResourceIcon) o).mResId == mResId;
        }

        @Override
        public String toString() {
            return String.format("ResourceIcon[resId=0x%08x]", mResId);
        }
    }
}
