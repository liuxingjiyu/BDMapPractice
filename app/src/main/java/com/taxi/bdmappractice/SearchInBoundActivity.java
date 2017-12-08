package com.taxi.bdmappractice;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.taxi.bdmappractice.baseactivity.PoiSearchBaseActivity;
import com.taxi.bdmappractice.util.LogUtil;

/**
 * Created by taxi01 on 2017/12/7.
 */

public class SearchInBoundActivity extends PoiSearchBaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private LatLng latLng1 = new LatLng(40.048459,116.302072);
    private LatLng latLng2 = new LatLng(40.050675,116.304317);
    private CheckBox checkBox;
    private boolean isChecked = false;

    @Override
    public void poiSearchInit() {
        initView();
        poiSearch.searchInBound(getPoiBoundSearchOption());
    }

    @Override
    public void onPoiClick(int i) {
        PoiInfo poiInfo = poiOverlay.getPoiResult().getAllPoi().get(i);
        if (isChecked) {
            poiSearch.searchPoiDetail(getPoiDetailSearchOption(poiInfo.uid));
        }else {
            LogUtil.showToast(this,poiInfo.name + "," + poiInfo.address);
        }
    }

    private void initView() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.bottom_layout);
        /*RadioGroup group = new RadioGroup(this);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        group.setLayoutParams(params);
        group.setOrientation(HORIZONTAL);
        group.setGravity(Gravity.CENTER_HORIZONTAL);
        group.setOnCheckedChangeListener(this);

        RadioButton radioButton1 = new RadioButton(this);
        radioButton1.setText("边界搜索");
        radioButton1.setChecked(true);

        RadioButton radioButton2 = new RadioButton(this);
        radioButton2.setText("城市搜索");

        RadioButton radioButton3 = new RadioButton(this);
        radioButton3.setText("周边搜索");

        group.addView(radioButton1);
        group.addView(radioButton2);
        group.addView(radioButton3);*/

        View group = View.inflate(this,R.layout.search_layout,null);
        RadioGroup radioGroup = group.findViewById(R.id.rg_search);
        checkBox = group.findViewById(R.id.cb_detail);
        checkBox.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        ll.addView(group);
    }

    @NonNull
    private PoiBoundSearchOption getPoiBoundSearchOption() {
        LatLngBounds lngBounds = new LatLngBounds.Builder().include(latLng1).include(latLng2).build();
        PoiBoundSearchOption searchOption = new PoiBoundSearchOption();
        searchOption.bound(lngBounds);
        searchOption.keyword("加油站");
        return searchOption;
    }

    private PoiCitySearchOption getPoiCitySearchOption(){
        PoiCitySearchOption searchOption = new PoiCitySearchOption();
        searchOption.city("北京");
        searchOption.keyword("加油站");
        searchOption.pageCapacity(15);
        return searchOption;
    }

    private PoiDetailSearchOption getPoiDetailSearchOption(String poiUid){
        PoiDetailSearchOption searchOption = new PoiDetailSearchOption();
        searchOption.poiUid(poiUid);
        return searchOption;
    }

    private PoiNearbySearchOption getPoiNearbySearchOption(){
        PoiNearbySearchOption searchOption = new PoiNearbySearchOption();
        searchOption.location(czPos);
        searchOption.keyword("银行");
        searchOption.radius(1000);
        return searchOption;
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rb_bound:
                poiSearch.searchInBound(getPoiBoundSearchOption());
                break;
            case R.id.rb_city:
                poiSearch.searchInCity(getPoiCitySearchOption());
                break;
            case R.id.rb_nearby:
                poiSearch.searchNearby(getPoiNearbySearchOption());
                break;
            default:
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        if (poiDetailResult == null || poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR){
            LogUtil.showToast(this,"没有搜索到");
        }else {
            LogUtil.showToast(this,poiDetailResult.getShopHours() + "," + poiDetailResult.getTelephone());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cb_detail){
            if (checkBox.isChecked()){
                isChecked = true;
            }else {
                isChecked = false;
            }
        }
    }
}
