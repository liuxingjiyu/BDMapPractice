package com.taxi.bdmappractice;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by shizhengui on 2017/12/6.
 */

public class OverlayActivity extends BaseActivity {
    @Override
    public void init() {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(hmPos)
                    .radius(1000)
                    .stroke(new Stroke(6,0x55FF0000))
                    .fillColor(0x5500FF00);
        baiduMap.addOverlay(circleOptions);

        TextOptions textOptions = new TextOptions();
        textOptions.position(hmPos)
                    .text("黑马程序员")
                    .fontSize(20)
                    .fontColor(0xFF000000)
                    .bgColor(0x55FF0000)
                    .rotate(30f);
        baiduMap.addOverlay(textOptions);

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_eat);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(hmPos)
                    .title("黑马")
                    .icon(descriptor)
                    .anchor(0.5f,1.0f)
                    .perspective(true)
                    .draggable(true);
        baiduMap.addOverlay(markerOptions);
        // 添加一个向北的标志
        markerOptions = new MarkerOptions().icon(descriptor)
                .title("向北")
                .position(new LatLng(hmPos.latitude + 0.001, hmPos.longitude))
                .draggable(true);
        baiduMap.addOverlay(markerOptions);

        // 添加一个向东的标志
        markerOptions = new MarkerOptions().icon(descriptor)
                .title("向东")
                .position(new LatLng(hmPos.latitude, hmPos.longitude + 0.001))
                .draggable(true);
        baiduMap.addOverlay(markerOptions);

        // 添加一个向西南的标志
        markerOptions = new MarkerOptions().icon(descriptor)
                .title("向西南")
                .position(new LatLng(hmPos.latitude - 0.001, hmPos.longitude - 0.001))
                .draggable(true);
        baiduMap.addOverlay(markerOptions);

    }
}
