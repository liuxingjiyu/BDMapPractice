package com.taxi.bdmappractice;
import android.view.View;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;

public class HelloBaiduMap extends BaseActivity implements View.OnClickListener{

    @Override
    public void init() {
        initBDMap();
    }

    private void initBDMap(){

        findViewById(R.id.zoom_in).setOnClickListener(this);
        findViewById(R.id.zoom_out).setOnClickListener(this);
        findViewById(R.id.overlook).setOnClickListener(this);
        findViewById(R.id.rotate).setOnClickListener(this);
        findViewById(R.id.scroll_by).setOnClickListener(this);
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
