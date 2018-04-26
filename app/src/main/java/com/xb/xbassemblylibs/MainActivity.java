package com.xb.xbassemblylibs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/20.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findViewById(R.id.scan_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent scanintent = new Intent();
                scanintent.setClass(MainActivity.this, QRScanActivity.class);
                scanintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(scanintent, R.id.scan_btn);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case R.id.scan_btn:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    String name = bundle.getString("name");
                    Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
