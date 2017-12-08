package com.taxi.bdmappractice;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.taxi.bdmappractice.baseactivity.BaseActivity;

public class HelloBaiduMap extends BaseActivity implements View.OnClickListener{

    @Override
    public void init() {
        initBDMap();
    }

    private void initBDMap(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.bottom_layout);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_button_layout, layout,false);
        layout.addView(view);
        view.findViewById(R.id.zoom_in).setOnClickListener(this);
        view.findViewById(R.id.zoom_out).setOnClickListener(this);
        view.findViewById(R.id.overlook).setOnClickListener(this);
        view.findViewById(R.id.rotate).setOnClickListener(this);
        view.findViewById(R.id.scroll_by).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MapStatusUpdate newStatus = null;
        switch (v.getId()){
            case R.id.zoom_in:
                newStatus = MapStatusUpdateFactory.zoomIn();
                break;
            case R.id.zoom_out:
                newStatus = MapStatusUpdateFactory.zoomOut();
                break;
            case R.id.rotate:
                MapStatus currentStatus = baiduMap.getMapStatus();
                float rotate = currentStatus.rotate + 30;
                MapStatus mapStatus = new MapStatus.Builder().rotate(rotate).build();
                newStatus = MapStatusUpdateFactory.newMapStatus(mapStatus);
                break;
            case R.id.overlook:
                currentStatus = baiduMap.getMapStatus();
                float overlook = currentStatus.overlook - 5;
                mapStatus = new MapStatus.Builder().overlook(overlook).build();
                newStatus = MapStatusUpdateFactory.newMapStatus(mapStatus);
                break;
            case R.id.scroll_by:
                newStatus = MapStatusUpdateFactory.newLatLng(czPos);
                baiduMap.animateMapStatus(newStatus,2000);
                return;
            default:
        }
        baiduMap.setMapStatus(newStatus);
    }
}
