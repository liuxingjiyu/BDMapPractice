package com.taxi.bdmappractice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.taxi.bdmappractice.baseactivity.RouteLineAdapter;
import com.taxi.bdmappractice.baseactivity.RoutePlanSearchBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taxi01 on 2017/12/8.
 */

public class MyRoutePlanSearch extends RoutePlanSearchBaseActivity implements RadioGroup.OnCheckedChangeListener {

    boolean hasShownDialogue = false;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private DrivingRouteResult nowResultDrive;
    RouteLine route = null;
    OverlayManager routeOverlay = null;

    @Override
    protected void routePlanSearchInit() {
        initView();
    }

    private void initView() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.bottom_layout);
        View group = View.inflate(this,R.layout.route_search_select,null);
        RadioGroup radioGroup = group.findViewById(R.id.rg_route_search);
        radioGroup.setOnCheckedChangeListener(this);
        ll.addView(group);
        setTitle("MyRoutePlanSearch");
    }

    private DrivingRoutePlanOption getDrivingRouteSearchParams() {
        DrivingRoutePlanOption routePlanOption = new DrivingRoutePlanOption();
        List<PlanNode> planNodes = new ArrayList<>();
        planNodes.add(PlanNode.withCityNameAndPlaceName("北京市","清华大学"));
        routePlanOption.from(PlanNode.withLocation(hmPos));
        routePlanOption.passBy(planNodes);
        routePlanOption.to(PlanNode.withLocation(czPos));
        return routePlanOption;
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        /*DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(drivingRouteResult.getRouteLines().get(0));
        overlay.addToMap();
        overlay.zoomToSpan();*/
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyRoutePlanSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
            nodeIndex = -1;
            if (drivingRouteResult.getRouteLines().size() >= 1){
                nowResultDrive = drivingRouteResult;
                if (!hasShownDialogue){
                    MyTransitDlg myTransitDlg = new MyTransitDlg(MyRoutePlanSearch.this,drivingRouteResult.getRouteLines(), RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });

                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            route = nowResultDrive.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                            baiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultDrive.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }
                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            }
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.rb_mass_transit:
                break;
            case R.id.rb_driving:
                routePlanSearch.drivingSearch(getDrivingRouteSearchParams());
                break;
            case R.id.rb_transit:
                break;
            case R.id.rb_biking:
                break;
            case R.id.rb_walking:
                break;
            default:
        }
    }

    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mRouteLineAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> routeLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = routeLines;
            mRouteLineAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mRouteLineAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }
}


