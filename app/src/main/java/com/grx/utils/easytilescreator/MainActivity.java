package com.grx.utils.easytilescreator;

import android.app.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.GrxTileSimulator;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.tiles.GrxExampleDarkModeTile;
import com.android.systemui.qs.tiles.GrxMultiActionTile;
import com.android.systemui.qs.tiles.GrxOutdoorModeTCTile;
import com.android.systemui.qs.tiles.GrxOutdoorModeTile;
import com.android.systemui.qs.tiles.TemplateOneTile;
import com.android.systemui.qs.tiles.TemplateTwoTile;

public class MainActivity extends Activity {

    LinearLayout mTilesContainer;
    QSTileHost mQsTileHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTilesContainer = (LinearLayout) findViewById(R.id.tiles_container);
        mQsTileHost = new QSTileHost(this);


        /**  add your tile simulators here  */
        new GrxTileSimulator(this, new GrxOutdoorModeTile((mQsTileHost)), mTilesContainer);
        new GrxTileSimulator(this, new GrxOutdoorModeTCTile((mQsTileHost)), mTilesContainer);
        new GrxTileSimulator(this, new TemplateOneTile(mQsTileHost),mTilesContainer); // TemplateOneTile
        new GrxTileSimulator(this, new TemplateTwoTile(mQsTileHost),mTilesContainer); // TemplateTwoTile
        new GrxTileSimulator(this, new GrxMultiActionTile(mQsTileHost), mTilesContainer);
        new GrxTileSimulator(this, new GrxExampleDarkModeTile(mQsTileHost), mTilesContainer);

    //    new GrxTileSimulator(this, new GrxMultiActionTile(mQsTileHost), mTilesContainer);
    //    new GrxTileSimulator(this, new GrxMultiActionTile(mQsTileHost), mTilesContainer);

    }

    private void forceRefreshTiles(){
        for(int i=0; i<mTilesContainer.getChildCount(); i++) {
            View child = mTilesContainer.getChildAt(i);
            if(child instanceof GrxTileSimulator){
                ((GrxTileSimulator) child).refreshTileState();
            }
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        forceRefreshTiles();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        forceRefreshTiles();

    }
}
