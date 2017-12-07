package com.taxi.bdmappractice;

import android.support.annotation.NonNull;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.taxi.bdmappractice.util.LogUtil;

/**
 * Created by taxi01 on 2017/12/7.
 */

public class SearchInBoundActivity extends BaseActivity implements OnGetPoiSearchResultListener {

    private LatLng latLng1 = new LatLng(40.048459,116.302072);
    private LatLng latLng2 = new LatLng(40.050675,116.304317);

    @Override
    public void init() {
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
        poiSearch.searchInBound(getPoiBoundSearchOption());
    }

    @NonNull
    private PoiBoundSearchOption getPoiBoundSearchOption() {
        LatLngBounds lngBounds = new LatLngBounds.Builder().include(latLng1).include(latLng2).build();
        PoiBoundSearchOption searchOption = new PoiBoundSearchOption();
        searchOption.bound(lngBounds);
        searchOption.keyword("加油站");
        return searchOption;
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR){
            LogUtil.showToast(this,"没有搜索到结果");
        }
        PoiOverlay poiOverlay = new PoiOverlay(baiduMap){
            @Override
            public boolean onPoiClick(int i) {
                PoiInfo poiInfo = getPoiResult().getAllPoi().get(i);
                LogUtil.showToast(SearchInBoundActivity.this,poiInfo.name + "," + poiInfo.address);
                return true;
            }
        };
        poiOverlay.setData(poiResult);
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
