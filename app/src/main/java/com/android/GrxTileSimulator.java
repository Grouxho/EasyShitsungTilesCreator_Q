package com.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.grx.utils.easytilescreator.R;

import org.w3c.dom.Text;


public class GrxTileSimulator extends LinearLayout implements QSTileImpl.SimulateRefreshState {

    private Context mContext;
    public QSTileImpl mTile;

    QSTile.BooleanState mState;

    DetailAdapter mDetailAdapter;

    Button mButtonDone, mButtonDetails;
    LinearLayout mDetailAdapterContainer;
    ImageView mTileIconView;
    TextView mTileTextView;

    AlphaOptimizedLinearLayout mDetailAdapterHeader;
    TextView mQsDetailHeaderText;
    TextView mQsDetailHeaderTitle;
    Switch mHeaderToggle;
    LinearLayout mDetailAdapterWrapper;

    TextView mErrorTextView;


    int mColor;

    public GrxTileSimulator(Context context){
        super(context);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public GrxTileSimulator(Context context, QSTileImpl tile, ViewGroup rootview){
        super(context);
        mContext=context;
        mTile = tile;

        if(mTile==null) return;


        View tileview = inflate(context,R.layout.qs_simulator_view,new LinearLayout(mContext));
        mErrorTextView = tileview.findViewById(R.id.error);



        mTile.setSimulateRefresCallBack(this);
        mState = new QSTile.BooleanState();

        mDetailAdapterHeader = tileview.findViewById(R.id.adapter_header);
        mTileTextView = tileview.findViewById(R.id.tile_text);
        mTileIconView = tileview.findViewById(R.id.tile_icon);
        mDetailAdapterContainer = tileview.findViewById(R.id.tile_adapter);
        mButtonDone = tileview.findViewById(R.id.done);
        mButtonDetails=tileview.findViewById(R.id.details);

        mDetailAdapterWrapper = (LinearLayout) tileview.findViewById(R.id.adapterwrapper);
        mQsDetailHeaderText = (TextView) tileview.findViewById(R.id.infoadaptertext);
        mQsDetailHeaderTitle = (TextView) tileview.findViewById(android.R.id.title);
        mHeaderToggle=(Switch) tileview.findViewById(android.R.id.toggle);

        mTileTextView.setVisibility(View.VISIBLE);
        mTileIconView.setVisibility(View.VISIBLE);


        mTileIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTile.simulateClick();
            }
        });

        mTileIconView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mTile.simulateLongClick();


                return true;
            }
        });




        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDetailAdapter();
            }
        });


        mButtonDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mDetailAdapter.getSettingsIntent();
                closeDetailAdapter();
                if(intent!=null){
                    try {
                        mContext.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        mTileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // SIMULATING dualTarget - ONLY FOR Q in samsung.
                if(!mTile.mDualTarget && mTile.getDetailAdapter()!=null) {
                    Toast.makeText(mContext,"In Samung Q you need to add state.dualTarget=true; at the end of the handleUpdateState method in tile !!!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(mTile.getDetailAdapter()!=null) {

        /*
        For oreo and pie samsung shows the header switch even when the adapter view is null. However in Q for the switch to be shown the
        adapter view also has to be not null, apart from returning a non null boolean in getToggleState

         */
                    if(mTile.getDetailAdapter().createDetailView(mContext,null,null) ==null) {
                        Log.d("grx ", " header switch will be not shown in Q even if getToggleState does not retturn a null");

                    }else {
                        mTileTextView.setVisibility(View.GONE);
                        mTileIconView.setVisibility(View.GONE);
                        mButtonDone.setVisibility(View.VISIBLE);
                        showDetailView();
                    }

                    /* original code for pie and oreo

                                        mTileTextView.setVisibility(View.GONE);
                    mTileIconView.setVisibility(View.GONE);
                    mButtonDone.setVisibility(View.VISIBLE);
                    showDetailView();


                     */

                }


            }
        });

        mState = new QSTile.BooleanState();

        mDetailAdapter = mTile.getDetailAdapter();


        CardView cardView = tileview.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor( (rootview.getChildCount() % 2==0) ? 0xffeaeaea : 0xfffafafa  );

        TextView cardtitle = tileview.findViewById(R.id.cardtitle);
        String classname = mTile.getClass().getSimpleName();
        cardtitle.setText(classname);
        mColor = (rootview.getChildCount() % 2==0) ? 0xff2352ff : 0xffc75151 ;
        cardtitle.setTextColor( mColor);
        mQsDetailHeaderText.setTextColor(mColor);
        mDetailAdapterWrapper.setVisibility(GONE);

        mErrorTextView.setTextColor(mColor);

       this.addView(tileview, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        rootview.addView(this,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);


//        rootview.addView(tileview,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        if(!mTile.isAvailable()){
            showError("Tile isAvailable method is returning false");
        }
        mTile.refreshState();


    }



    public void closeDetailAdapter(){
        mTileTextView.setVisibility(View.VISIBLE);
        mTileIconView.setVisibility(View.VISIBLE);
        mButtonDone.setVisibility(View.GONE);
        mButtonDetails.setVisibility(View.GONE);
        mDetailAdapterContainer.removeAllViews();
        mDetailAdapterWrapper.setVisibility(GONE);
    }

    public void showDetailView(){

        mDetailAdapter=mTile.getDetailAdapter();
        if(mDetailAdapter==null) return;

        if(mDetailAdapter.hasHeader()){
            mQsDetailHeaderTitle.setText(mDetailAdapter.getTitle());
            Boolean toggleEnabled = mTile.getDetailAdapter().getToggleEnabled();
            if(!toggleEnabled || mDetailAdapter.getToggleState()==null ) { // in pie and oreo samsung does not check adapterview = null in order to show the header switch.
                mHeaderToggle.setVisibility(INVISIBLE);
                mHeaderToggle.setEnabled(false);
            }else {
                boolean swichstate = mTile.getDetailAdapter().getToggleState();
                mHeaderToggle.setChecked(swichstate);
                mHeaderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        mHeaderToggle.setChecked(checked);
                        mTile.getDetailAdapter().setToggleState(checked);
                        //boolean newstate =  mDetailAdapter.setToggleState(checked); // for oreo - pie
                        // mHeaderToggle.setChecked(newstate);  // for oreo and pie

                        mDetailAdapter.setToggleState(checked); // for q
                        mHeaderToggle.setChecked(checked);  // for Q

                    }
                });
            }
        }else mDetailAdapterHeader.setVisibility(GONE);
        if(mDetailAdapter.getSettingsIntent()!=null){
               mButtonDetails.setVisibility(VISIBLE);
        }




        /*

        For oreo and pie samsung shows the header switch even when the adapter view is null. However in Q for the switch to be shown the
        adapter view also has to be not null, apart from returning a non null boolean in getToggleState

         */


        View adapterview = mDetailAdapter.createDetailView(mContext,null,null);
        if(adapterview!=null) mDetailAdapterContainer.addView(adapterview);
        mDetailAdapterWrapper.setVisibility(VISIBLE);




    }



    public void onTileRefreshed(QSTile.State state){
        mState=(QSTile.BooleanState) state;
        if(mState==null) return;

        //CharSequence text =  mTile.getTileLabel();

        CharSequence text = state.label;

        Drawable icon = null;
        if(mState.icon!=null) icon = mState.icon.getDrawable(mContext);

        mTileTextView.setText(text);
        mTileIconView.setImageDrawable(icon);

        switch (mState.state){
            case Tile.STATE_ACTIVE:
                mTileIconView.setColorFilter(mColor);
                break;
            case Tile.STATE_INACTIVE:
                mTileIconView.clearColorFilter();
                break;
            case Tile.STATE_UNAVAILABLE:
                mTileIconView.setColorFilter(mColor&0x60ffffff);
                break;
        }
    }

    public void refreshTileState(){
        if(mTile!=null){
            if(!mTile.isAvailable()) showError("The tile will not added to UI because isAvailable is returning false");
            mTile.simulateClearState();
        }
    }


    private void showError(String error){
            mErrorTextView.setVisibility(VISIBLE);
            mErrorTextView.setText(error);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mErrorTextView.setVisibility(GONE);
                }
            }, 10000);
    }

}
