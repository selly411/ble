package com.superdroid.test.zzafire;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class selecttime_data extends AppCompatActivity {

    Button btn_date;
    RadioButton btn_date1;
    RadioButton btn_date2;

    TextView text_date1;
    TextView text_date2;
    Calendar c = Calendar.getInstance();

    int myear;
    int mmonth;
    int mmonth2;
    int mday;
    int mday2;


    ListView listView;//리스트뷰 객체
    BleList bleList = null;//리스트 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecttime_data);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_date1 = (RadioButton) findViewById(R.id.btn_date1);
        btn_date2 = (RadioButton) findViewById(R.id.btn_date2);
        text_date1 = (TextView) findViewById(R.id.text_date1);
        text_date2 = (TextView) findViewById(R.id.text_date2);

        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);

        bleList = new BleList();
        listView = (ListView) findViewById(R.id.listView_selecttime);
        listView.setAdapter(bleList);

        test_addDevice();
    }

    public void test_addDevice(){

        if(bleList.devices.isEmpty()) {

            String device_string = "기기명1";
            bleList.addDevice(device_string);

            String device_string2 = "기기명2";
            bleList.addDevice(device_string2);

            bleList.notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String device_string3 = "기기명3";
                    bleList.addDevice(device_string3);
                    bleList.notifyDataSetChanged();
                }
            }, 500);// 0.5초 정도 딜레이를 준 후 시작
        }
        else
        {
            bleList.notifyDataSetChanged();
        }

        List<DeviceInfo> infos = DeviceInfo.listAll(DeviceInfo.class);

        for(int i=0;i<infos.size();i++)
        {
            Log.d("HR",infos.get(i).hrdata);
        }

    }

    private class BleList extends BaseAdapter {
        private ArrayList<String> devices;
        private LayoutInflater inflater;


        public BleList() {
            super();
            devices = new ArrayList<String>();
            inflater = ((Activity) selecttime_data.this).getLayoutInflater();
        }

        public void addDevice(String device) {
            devices.add(device);
        }

        public void clear() {
            devices.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.time_listview, null);
                viewHolder.devicebox= (CheckBox) convertView.findViewById(R.id.cb_name);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.devicebox.setTag(position);
            viewHolder.devicebox.setText(bleList.devices.get(position));
            viewHolder.devicebox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Toast.makeText(selecttime_data.this,viewHolder.devicebox.getText().toString()+"시작",Toast.LENGTH_SHORT).show();
                    }

                }
            });




            return convertView;
        }
    }


    static class ViewHolder {
        CheckBox devicebox;
    }


    DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myear = year;
            mmonth = monthOfYear + 1;
            mday = dayOfMonth;
            mday2 = dayOfMonth + 1;
            text_date1.setText(myear + "/" + mmonth + "/" + mday);
            text_date2.setText(myear + "/" + mmonth + "/" + mday2);
        }
    };



    public void disableDayField(DatePickerDialog datePickerDialog) {
        try {
            Field[] f = datePickerDialog.getClass().getDeclaredFields();
            datePickerDialog.getDatePicker().setSpinnersShown(true);

            for (Field dateField : f) {
                if (dateField.getName().equals("mDayPicker") ||
                        dateField.getName().equals("mDaySpinner") ||
                        dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField.get(datePickerDialog);
                    Field datePickerFields[] = dateField.getType().getDeclaredFields();


                    // Lollipop 이후 버전
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                        if (daySpinnerId != 0) {
                            View daySpinner = datePicker.findViewById(daySpinnerId);
                            if(daySpinner != null) {
                                daySpinner.setVisibility(View.GONE);
                            }
                        }
                    }

                    // Lollipop 이전 버전
                    else {
                        for (Field datePickerField : datePickerFields) {
                            if (datePickerField.getName().contains("mDay")) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = new Object();
                                dayPicker = datePickerField.get(datePicker);

                                try {
                                    ((View) dayPicker).setVisibility(View.GONE);
                                } catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myear = year;
            mmonth = monthOfYear + 1;
            mmonth2 = mmonth +1;
            text_date1.setText(myear + "/" + mmonth);
            text_date2.setText(myear + "/" + mmonth2);
        }
    };


    public void mOnClick_date1(View v){
        Dialog datepicker = new DatePickerDialog(selecttime_data.this,dateSetListener1,myear,mmonth,mday);
        DatePickerDialog datePickerDialog = new DatePickerDialog(selecttime_data.this,android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener2, myear, mmonth, mday);
        if(btn_date1.isChecked()==true) {
            disableDayField(datePickerDialog);
            datePickerDialog.show();
        }

        if(btn_date2.isChecked()==true) {
            datepicker.show();
        }
    }


}

