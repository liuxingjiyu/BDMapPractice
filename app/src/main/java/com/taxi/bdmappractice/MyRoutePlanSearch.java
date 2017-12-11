package com.taxi.bdmappractice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.MassTransitRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
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
    private TransitRouteResult nowResultransit;
    MassTransitRouteLine massroute = null;
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    private TextView popupText = null; // 泡泡view
    private WalkingRouteResult nowResultwalk;
    private BikingRouteResult nowResultbike;
    private MassTransitRouteResult nowResultmass;

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
        //routePlanOption.passBy(planNodes);
        routePlanOption.to(PlanNode.withLocation(czPos));
        return routePlanOption;
    }

    private TransitRoutePlanOption getTransitRouteSearchParams() {
        TransitRoutePlanOption routePlanOption = new TransitRoutePlanOption();
        routePlanOption.city("北京市").from(PlanNode.withLocation(hmPos)).to(PlanNode.withLocation(czPos)).policy(TransitRoutePlanOption.TransitPolicy.EBUS_TIME_FIRST);
        return routePlanOption;
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyRoutePlanSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            if (transitRouteResult.getRouteLines().size() > 1) {
                nowResultransit = transitRouteResult;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(MyRoutePlanSearch.this,
                            nowResultransit.getRouteLines(),
                            RouteLineAdapter.Type.TRANSIT_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {

                            route = nowResultransit.getRouteLines().get(position);
                            TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);
                            baiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultransit.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (transitRouteResult.getRouteLines().size() == 1) {
                // 直接显示
                route = transitRouteResult.getRouteLines().get(0);
                TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);
                baiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(transitRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }


        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
        if (massTransitRouteResult == null || massTransitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyRoutePlanSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (massTransitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点模糊，获取建议列表
            massTransitRouteResult.getSuggestAddrInfo();
            return;
        }
        if (massTransitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nowResultmass = massTransitRouteResult;

            nodeIndex = -1;

            if (!hasShownDialogue) {
                // 列表选择
                MyTransitDlg myTransitDlg = new MyTransitDlg(MyRoutePlanSearch.this,
                        massTransitRouteResult.getRouteLines(),
                        RouteLineAdapter.Type.MASS_TRANSIT_ROUTE);
                nowResultmass = massTransitRouteResult;
                myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hasShownDialogue = false;
                    }
                });
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    public void onItemClick(int position) {

                        MassTransitRouteOverlay overlay = new MassTransitRouteOverlay(baiduMap);
                        baiduMap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        massroute = nowResultmass.getRouteLines().get(position);
                        overlay.setData(nowResultmass.getRouteLines().get(position));

                        MassTransitRouteLine line = nowResultmass.getRouteLines().get(position);
                        overlay.setData(line);
                        if (nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId()) {
                            // 同城
                            overlay.setSameCity(true);
                        } else {
                            // 跨城
                            overlay.setSameCity(false);

                        }
                        baiduMap.clear();
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                });
                myTransitDlg.show();
                hasShownDialogue = true;
            }
        }
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
            if (drivingRouteResult.getRouteLines().size() > 1){
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
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);;
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
            }else if (drivingRouteResult.getRouteLines().size() == 1) {
                route = drivingRouteResult.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
                routeOverlay = overlay;
                baiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyRoutePlanSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(MyRoutePlanSearch.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;

            if (bikingRouteResult.getRouteLines().size() > 1) {
                nowResultbike = bikingRouteResult;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(MyRoutePlanSearch.this,
                            bikingRouteResult.getRouteLines(),
                            RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultbike.getRouteLines().get(position);
                            BikingRouteOverlay overlay = new BikingRouteOverlay(baiduMap);
                            baiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultbike.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (bikingRouteResult.getRouteLines().size() == 1) {
                route = bikingRouteResult.getRouteLines().get(0);
                BikingRouteOverlay overlay = new BikingRouteOverlay(baiduMap);
                routeOverlay = overlay;
                baiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(bikingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MyRoutePlanSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(MyRoutePlanSearch.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;

            if (walkingRouteResult.getRouteLines().size() > 1) {
                nowResultwalk = walkingRouteResult;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(MyRoutePlanSearch.this,
                            walkingRouteResult.getRouteLines(),
                            RouteLineAdapter.Type.WALKING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultwalk.getRouteLines().get(position);
                            WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                            baiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultwalk.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (walkingRouteResult.getRouteLines().size() == 1) {
                // 直接显示
                route = walkingRouteResult.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                baiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(walkingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.rb_mass_transit:
                baiduMap.clear();
                routePlanSearch.masstransitSearch(new MassTransitRoutePlanOption().from(PlanNode.withCityNameAndPlaceName("北京", "天安门"))
                        .to(PlanNode.withCityNameAndPlaceName("上海", "东方明珠")));
                break;
            case R.id.rb_driving:
                baiduMap.clear();
                routePlanSearch.drivingSearch(getDrivingRouteSearchParams());
                break;
            case R.id.rb_transit:
                baiduMap.clear();
                routePlanSearch.transitSearch(getTransitRouteSearchParams());
                break;
            case R.id.rb_biking:
                baiduMap.clear();
                routePlanSearch.bikingSearch(new BikingRoutePlanOption().from(PlanNode.withLocation(hmPos))
                        .to(PlanNode.withLocation(czPos)));
                break;
            case R.id.rb_walking:
                baiduMap.clear();
                routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(PlanNode.withLocation(hmPos))
                        .to(PlanNode.withLocation(czPos)));
                break;
            default:
        }
    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onRouteNodeClick(int i) {
            LatLng nodeLocation = null;
            String nodeTitle = null;
            Object step = null;
            step = route.getAllStep().get(i);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                if (i == 0) {
                    nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                    nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
                }else if(i == 1){
                    nodeLocation = ((DrivingRouteLine.DrivingStep) route.getAllStep().get(0)).getExit().getLocation();
                    nodeTitle = ((DrivingRouteLine.DrivingStep) route.getAllStep().get(0)).getExitInstructions();
                }else{
                    step = route.getAllStep().get(i-1);
                    nodeLocation = ((DrivingRouteLine.DrivingStep) step).getExit().getLocation();
                    nodeTitle = ((DrivingRouteLine.DrivingStep) step).getExitInstructions();
                }
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
            // show popup
            if (popupText == null){
                popupText = new TextView(MyRoutePlanSearch.this);
                popupText.setBackgroundResource(R.drawable.popup);
                //popupText.setMaxEms(6);
                popupText.setSingleLine(false);
                popupText.setTextColor(0xFF000000);
                popupText.setPadding(5,2,5,2);
                mMapView.addView(popupText,getLayoutParams(nodeLocation));
                //baiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
            }else {
                mMapView.updateViewLayout(popupText,getLayoutParams(nodeLocation));
            }
            popupText.setText(nodeTitle);
            Log.i("baidumapsdk", "DrivingRouteOverlay onRouteNodeClick:" +i +"  in坐标:" +
                    ((DrivingRouteLine.DrivingStep) step).getEntranceInstructions()
                    +"   out坐标:"+((DrivingRouteLine.DrivingStep) step).getExitInstructions());
            return true;
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

    @NonNull
    private MapViewLayoutParams getLayoutParams(LatLng position) {
        return new MapViewLayoutParams.Builder()
                .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                .position(position)
                .yOffset(-30)
                .build();
    }
}


