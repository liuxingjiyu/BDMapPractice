package com.taxi.bdmappractice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;


public abstract class BaseActivity extends AppCompatActivity {

    protected MapView mMapView;
    protected BaiduMap baiduMap;

    /** 黑马坐标（北京市海淀区东北旺南路45号）*/
    protected LatLng hmPos = new LatLng(40.050513, 116.30361);
    /** 传智坐标 */
    protected LatLng czPos = new LatLng(40.065817,116.349902);
    /** 天安门坐标 */
    protected LatLng tamPos = new LatLng(39.915112,116.403963);

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        //mMapView.showScaleControl(false);
        //mMapView.showZoomControls(false);
        /*float MinLevel = baiduMap.getMinZoomLevel();
        float MaxLevel = baiduMap.getMaxZoomLevel();
        Log.w("BaiDuMap","MinLevel:" + MinLevel +", MaxLevel:" + MaxLevel );*/
        MapStatusUpdate newStatus = MapStatusUpdateFactory.newLatLngZoom(hmPos,15f);
        baiduMap.setMapStatus(newStatus);
        init();
    }

    public abstract void init();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
