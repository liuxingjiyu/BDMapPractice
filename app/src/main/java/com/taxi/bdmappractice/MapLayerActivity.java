package com.taxi.bdmappractice;


import android.support.annotation.IdRes;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.taxi.bdmappractice.baseactivity.BaseActivity;

/**
 * Created by shizhengui on 2017/12/5.
 */

public class MapLayerActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioButton nomal,statellite,traffic;

    @Override
    public void init() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.bottom_layout);
        View view = View.inflate(this,R.layout.maplayer_button,layout);

        nomal = view.findViewById(R.id.rb_normal);
        statellite = view.findViewById(R.id.rb_statellite);
        traffic = view.findViewById(R.id.rb_traffic);

        RadioGroup group = view.findViewById(R.id.rg_button);
        group.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.rb_normal:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                baiduMap.setTrafficEnabled(false);
                break;
            case R.id.rb_statellite:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                baiduMap.setTrafficEnabled(false);
                break;
            case R.id.rb_traffic:
                baiduMap.setTrafficEnabled(true);
                break;
            default:
        }
    }
}
