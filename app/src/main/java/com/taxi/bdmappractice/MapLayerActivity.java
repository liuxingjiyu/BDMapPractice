package com.taxi.bdmappractice;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * Created by shizhengui on 2017/12/5.
 */

public class MapLayerActivity extends BaseActivity {

    private LinearLayout layout;

    @Override
    public void init() {
        layout = (LinearLayout) findViewById(R.id.bottom_layout);
        layout.setVisibility(View.GONE);

        addButtonGroup();
    }

    private void addButtonGroup(){

    }
}
