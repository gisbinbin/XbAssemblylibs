package com.xb.xbassemblylibs;

import android.content.Intent;
import android.view.View;

import com.xb.xblibrary.view.BaseScanActivity;

/**
 * Created by binbin1058 on 2018/4/20.
 */

public class QRScanActivity extends BaseScanActivity {
    @Override
    public int intiLayout() {
        return R.layout.activity_qrscan;
    }

    @Override
    public void initView() {
        findViewById(R.id.openlight_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OpenAndCloseLight();
            }
        });
        findViewById(R.id.openxc_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ScanPicQR();
            }
        });
        findViewById(R.id.openother_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //根据需要定义
            }
        });
    }

    @Override
    public int initSurfaceView() {
        return R.id.preview_view;
    }

    @Override
    public int initViewfinderView() {
        return R.id.myqrscan_view;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
