package com.taxi.bdmappractice;

import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.taxi.bdmappractice.util.LogUtil;

/**
 * Created by shizhengui on 2017/12/7.
 */

public abstract class PoiSearchBaseActivity extends BaseActivity implements OnGetPoiSearchResultListener {

    protected PoiSearch poiSearch;
    protected PoiOverlay poiOverlay;

    @Override
    public final void init() {
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);

        poiOverlay = new PoiOverlay(baiduMap){
            @Override
            public boolean onPoiClick(int i) {
                PoiSearchBaseActivity.this.onPoiClick(i);
                return true;
            }

        };

        poiSearchInit();
    }
    public void onPoiClick(int i) {
        PoiInfo poiInfo = poiOverlay.getPoiResult().getAllPoi().get(i);
    }

    public abstract void poiSearchInit();

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR){
            LogUtil.showToast(this,"没有搜索到结果");
            return;
        }
        poiOverlay.setData(poiResult);
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
        baiduMap.setOnMarkerClickListener(poiOverlay);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
