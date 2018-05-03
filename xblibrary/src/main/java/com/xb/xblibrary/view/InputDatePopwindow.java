package com.xb.xblibrary.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xb.xblibrary.Dateinput.WheelView;
import com.xb.xblibrary.Dateinput.adapter.AbstractWheelTextAdapter1;
import com.xb.xblibrary.Dateinput.interfaces.OnWheelChangedListener;
import com.xb.xblibrary.Dateinput.interfaces.OnWheelScrollListener;
import com.xb.xblibrary.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by binbin1058 on 2018/1/11.
 */
public class InputDatePopwindow  extends PopupWindow implements View.OnClickListener {
    private Context context;
    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;
    private WheelView wvHour;
    private WheelView wvMintue;

    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private ArrayList<String> arry_days = new ArrayList<String>();
    private ArrayList<String> arry_hours = new ArrayList<String>();
    private ArrayList<String> arry_mintues = new ArrayList<String>();
    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDaydapter;
    private CalendarTextAdapter mHourdapter;
    private CalendarTextAdapter mMintuedapter;

    private String month;
    private String day;
    private String hour;
    private String mintue;

    private String currentYear = getYear();
    private String currentMonth = getMonth();
    private String currentDay = getDay();
    private String currentHour = getHour();
    private String currentMintue = getMintue();

    private int maxTextSize = 24;
    private int minTextSize = 14;

    private boolean issetdata = false;

    private String selectYear;
    private String selectMonth;
    private String selectDay;
    private String selectHour;
    private String selectMintue;

    private OnBirthListener onBirthListener;

    public InputDatePopwindow(final Context context) {
        super(context);
        this.context = context;
        View view= View.inflate(context, R.layout.dialog_inuttime,null);
        wvYear = (WheelView) view.findViewById(R.id.wv_birth_year);
        wvMonth = (WheelView) view.findViewById(R.id.wv_birth_month);
        wvDay = (WheelView) view.findViewById(R.id.wv_birth_day);
        wvHour = (WheelView) view.findViewById(R.id.wv_birth_hour);
        wvMintue = (WheelView) view.findViewById(R.id.wv_birth_minute);
        btnSure = (TextView) view.findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) view.findViewById(R.id.btn_myinfo_cancel);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (!issetdata) {
            initData();
        }
        initYears();
        mYearAdapter = new CalendarTextAdapter(context, arry_years, setYear(currentYear), maxTextSize, minTextSize);
        wvYear.setVisibleItems(5);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(currentYear));

        initMonths(Integer.parseInt(month));
        mMonthAdapter = new CalendarTextAdapter(context, arry_months, setMonth(currentMonth), maxTextSize, minTextSize);
        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(currentMonth));

        initDays(Integer.parseInt(day));
        mDaydapter = new CalendarTextAdapter(context, arry_days, Integer.parseInt(currentDay), maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDaydapter);
        wvDay.setCurrentItem(Integer.parseInt(currentDay) - 1);

        initHours(Integer.parseInt(hour));
        mHourdapter = new CalendarTextAdapter(context, arry_hours, Integer.parseInt(currentHour), maxTextSize, minTextSize);
        wvHour.setVisibleItems(5);
        wvHour.setViewAdapter(mHourdapter);
        wvHour.setCurrentItem(Integer.parseInt(currentHour));

        initMintues(Integer.parseInt(mintue));
        mMintuedapter = new CalendarTextAdapter(context, arry_mintues, Integer.parseInt(currentMintue), maxTextSize, minTextSize);
        wvMintue.setVisibleItems(5);
        wvMintue.setViewAdapter(mMintuedapter);
        wvMintue.setCurrentItem(Integer.parseInt(currentMintue));

        wvYear.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectYear = currentText;
                setTextviewSize(currentText, mYearAdapter);
                currentYear = currentText.substring(0, currentText.length()-1).toString();
                Log.d("currentYear==",currentYear);
                setYear(currentYear);
                initMonths(Integer.parseInt(month));
                mMonthAdapter = new CalendarTextAdapter(context, arry_months, 0, maxTextSize, minTextSize);
                wvMonth.setVisibleItems(5);
                wvMonth.setViewAdapter(mMonthAdapter);
                if(arry_months.size()<12)
                    wvMonth.setCurrentItem(0);
                calDays(currentYear, month);
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectMonth = currentText;
                currentMonth = currentText.substring(0, currentText.length()-1).toString();
                setTextviewSize(currentText, mMonthAdapter);
                setMonth(currentText.substring(0, 1));
                initDays(Integer.parseInt(day));
                mDaydapter = new CalendarTextAdapter(context, arry_days, 0, maxTextSize, minTextSize);
                wvDay.setVisibleItems(5);
                wvDay.setViewAdapter(mDaydapter);
                if(currentYear.equals(getYear())&&currentMonth.equals(getMonth()))
                    wvDay.setCurrentItem(0);
                calDays(currentYear, month);
            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
                selectDay = currentText;
                currentDay = currentText.substring(0, currentText.length()-1).toString();
                calhour(currentYear,currentMonth,currentDay);
                initHours(Integer.parseInt(hour));
                mHourdapter = new CalendarTextAdapter(context, arry_hours, 0, maxTextSize, minTextSize);
                wvHour.setVisibleItems(5);
                wvHour.setViewAdapter(mHourdapter);
                if(currentYear.equals(getYear())&&currentMonth.equals(getMonth())&&currentDay.equals(getDay())&& Integer.parseInt(currentHour)> Integer.parseInt(getHour()))
                    wvHour.setCurrentItem(0);
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
            }
        });

        wvHour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mHourdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mHourdapter);
                selectHour = currentText;
                currentHour = currentText.substring(0, currentText.length()-1).toString();
                //setTime(currentYear,month,day,hour,getMintue());
                calmintue(currentYear,currentMonth,currentDay,currentHour);
                initMintues(Integer.parseInt(mintue));
                mMintuedapter = new CalendarTextAdapter(context, arry_mintues, 0, maxTextSize, minTextSize);
                wvMintue.setVisibleItems(5);
                wvMintue.setViewAdapter(mMintuedapter);
                if(currentYear.equals(getYear())&&currentMonth.equals(getMonth())&&currentHour.equals(getHour())&&currentDay.equals(getDay()))
                    wvMintue.setCurrentItem(0);
            }
        });

        wvHour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mHourdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mHourdapter);
                selectHour = currentText;
            }
        });

        wvMintue.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMintuedapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMintuedapter);
                selectMintue = currentText;
                currentMintue = currentText.substring(0, currentText.length()-1).toString();
            }
        });

        wvMintue.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMintuedapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMintuedapter);
            }
        });
    }


    public void initYears() {
        for (int i = Integer.parseInt(getYear()); i > 1950; i--) {
            arry_years.add(i + "年");
        }
    }

    public void initMonths(int months) {
        arry_months.clear();
        for (int i = 1; i <= months; i++) {
            arry_months.add(i + "月");
        }
    }

    public void initDays(int days) {
        arry_days.clear();
        for (int i = 1; i <= days; i++) {
            arry_days.add(i + "日");
        }
    }

    public void initHours(int hours) {
        arry_hours.clear();
        for (int i = 0; i <= hours; i++) {
            arry_hours.add(i+"时");
        }
    }

    public void initMintues(int mintues) {
        arry_mintues.clear();
        for (int i = 0; i <= mintues; i++) {
            arry_mintues.add(i+"分");
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter1 {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_time, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onBirthListener != null) {
                onBirthListener.onClick(currentYear, currentMonth, currentDay,currentHour,currentMintue);
                Log.d("cy",""+selectYear+""+selectMonth+""+selectDay);
            }
        } else if (v == btnSure) {

        }  else {
            dismiss();
        }
        dismiss();

    }

    public interface OnBirthListener {
        public void onClick(String year, String month, String day, String hour, String mintue);
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    public String getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR)+"";
    }

    public String getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1+"";
    }

    public String getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE)+"";
    }

    public String getHour() {
        Calendar mCalendar= Calendar.getInstance();
        return mCalendar.get(Calendar.HOUR_OF_DAY )+"";
    }

    public String getMintue() {
        Calendar mCalendar= Calendar.getInstance();
        return mCalendar.get(Calendar.MINUTE)+"";
    }

    public void initData() {
        this.currentYear=getYear();
        this.month=getMonth();
        this.day=getDay();
        this.hour=getHour();
        this.mintue=getMintue();
        this.currentDay = 1+"";
        this.currentMonth = 1+"";
    }

    public void calhour(String year, String month, String day)
    {
        if (year.equals(getYear())&&month.equals(getMonth())&&day.equals(getDay())) {
            this.hour=getHour();
        }
        else {
            this.hour = 23+"";
        }
    }


    private void calmintue(String year, String month, String day, String hour)
    {
        if(year.equals(getYear())&&month.equals(getMonth())&&day.equals(getDay())&&hour.equals(getHour())){
            this.mintue=getMintue();
        }else {
            this.mintue = 59+"";
        }
    }

    public void setTime(String year, String month, String day, String hour, String mintue)
    {
        selectYear = year + "年";
        selectMonth = month + "月";
        selectDay = day + "日";
        selectHour = hour + "时";
        selectMintue = mintue + "分";
        issetdata = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        this.currentHour = hour;
        this.currentMintue = mintue;
        if (year.equals(getYear())) {
            this.month = getMonth();
        } else {
            this.month = 12+"";
        }
        calDays(year, month);
        if(year.equals(getYear())&&month.equals(getMonth())&&day.equals(getDay())){
            this.hour=getHour();
        }else {
            this.hour = 23+"";
        }
        if(year.equals(getYear())&&month.equals(getMonth())&&day.equals(getDay())&&hour.equals(getHour())){
            this.mintue=getMintue();
        }else {
            this.mintue = 59+"";
        }

        wvYear.setCurrentItem(setYear(currentYear));
        wvMonth.setCurrentItem(setMonth(currentMonth));
        wvDay.setCurrentItem(setDays(currentDay));
        wvHour.setCurrentItem(Integer.parseInt(currentHour));
        wvMintue.setCurrentItem(Integer.parseInt(currentMintue));
    }
    /**
     * 设置年月日
     * @param day
     */
    public int setDays(String day) {
        int dayIndex = 0;
        calhour(currentYear,currentMonth,currentDay);
        for (int i = 1; i < Integer.parseInt(this.day); i++) {
            if (Integer.parseInt(day) == i) {
                return dayIndex;
            } else {
                dayIndex++;
            }
        }
        return dayIndex;
    }

    /**
     * 设置年份
     *
     * @param year
     */
    public int setYear(String year) {
        int yearIndex = 0;
        if (!year.equals(getYear())) {
            this.month = 12+"";
        } else {
            this.month = getMonth();
        }
        for (int i = Integer.parseInt(getYear()); i > 1950; i--) {
            if (i == Integer.parseInt(year)) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     *
     * @param month
     * @param month
     * @return
     */
    public int setMonth(String month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < Integer.parseInt(this.month); i++) {
            if (Integer.parseInt(month) == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

//    /**
//     * 设置时数
//     *
//     * @param hour
//     * @param hour
//     * @return
//     */
//    public int setHour(String hour) {
//        int monthIndex = 0;
//        calDays(currentYear, month);
//        for (int i = 1; i < Integer.parseInt(this.month); i++) {
//            if (Integer.parseInt(month) == i) {
//                return monthIndex;
//            } else {
//                monthIndex++;
//            }
//        }
//        return monthIndex;
//    }

    /**
     * 计算每月多少天
     *
     * @param month
     * @param year
     */
    public void calDays(String year, String month) {
        boolean leayyear = false;
        if (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        switch (Integer.parseInt(month)) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                this.day = 31+"";
                break;
            case 2:
                if (leayyear) {
                    this.day = 29+"";
                } else {
                    this.day = 28+"";
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                this.day = 30+"";
                break;
        }
        if (year.equals( getYear()) && month .equals( getMonth())) {
            this.day = getDay();
        }
    }
}
