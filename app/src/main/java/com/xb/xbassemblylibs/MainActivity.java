package com.xb.xbassemblylibs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xb.xblibrary.view.InputDatePopwindow;

import java.util.Calendar;

/**
 * Created by binbin1058 on 2018/4/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initview();
    }

    private void initview() {
        findViewById(R.id.scan_btn).setOnClickListener(this);
        findViewById(R.id.list_test_btn).setOnClickListener(this);
        findViewById(R.id.inputdata_test_btn).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan_btn:
                Intent scanintent = new Intent();
                scanintent.setClass(MainActivity.this, QRScanActivity.class);
                scanintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                scanintent.putExtra("zoomto",1);
                scanintent.putExtra("mylight",true);
                startActivityForResult(scanintent, R.id.scan_btn);
                break;
            case R.id.list_test_btn:
                Intent listintent = new Intent();
                listintent.setClass(MainActivity.this, RefreshSwipeMenuListActivity.class);
                startActivity(listintent);
                break;
            case R.id.inputdata_test_btn:
                final String[] str = new String[10];
                InputDatePopwindow InputDate = new InputDatePopwindow(MainActivity.this);
                Calendar c = Calendar.getInstance();
                InputDate.setTime(c.get(Calendar.YEAR)+"", (c.get(Calendar.MONTH) + 1)+"", c.get(Calendar.DAY_OF_MONTH)+"",c.get(Calendar.HOUR_OF_DAY)+"",c.get(Calendar.MINUTE)+"");
                InputDate.showAtLocation(((LinearLayout)findViewById(R.id.main_panel)), Gravity.BOTTOM, 0, 0);
                InputDate.setBirthdayListener(new InputDatePopwindow.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day,String hour,String mintue) {
                        // TODO Auto-generated method stub
                        StringBuilder sb = new StringBuilder();
                        sb.append(year.substring(0, year.length() - 1)).append("-").append(month.substring(0, day.length() - 1)).append("-").append(day);
                        str[0] = year + "-" + month + "-" + day+" "+hour+":"+mintue;
                        str[1] = sb.toString();
                        ((Button)findViewById(R.id.inputdata_test_btn)).setText(str[0]);
                    }
                });
                break;
        }
    }
}
