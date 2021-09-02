package com.superdroid.test.zzafire;

        import android.app.Activity;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;

        import java.util.ArrayList;

public class rename extends AppCompatActivity {

    ListView listView;//리스트뷰 객체
    BleList bleList = null;//리스트 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);

        //리스트뷰 설정
        bleList = new BleList();
        listView = (ListView) findViewById(R.id.listView_rename);
        listView.setAdapter(bleList);

        test_addDevice();
    }

    public void test_addDevice(){

        if(bleList.addresss.isEmpty()) {

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
    }

    private class BleList extends BaseAdapter {
        private ArrayList<String> devices;
        private ArrayList<String> addresss;
        private ArrayList<String> connects;
        private LayoutInflater inflater;


        public BleList() {
            super();
            devices = new ArrayList<String>();
            addresss = new ArrayList<String>();
            inflater = ((Activity) rename.this).getLayoutInflater();
        }

        public void addDevice(String device) {
            devices.add(device);
            addresss.add(device);
        }

        public void clear() {
            devices.clear();
            addresss.clear();
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
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.rename_listview, null);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.text_name);
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.text_address);
                viewHolder.deviceRename = (EditText) convertView.findViewById(R.id.text_rename);
                viewHolder.deviceButton = (Button)convertView.findViewById(R.id.btn_connect);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            viewHolder.deviceName.setText(devices.get(position));
            viewHolder.deviceAddress.setText(addresss.get(position));

            buttonInfo buttonInfo = new buttonInfo();
            buttonInfo.position=position;
            buttonInfo.viewHolder=viewHolder;
            viewHolder.deviceButton.setTag(buttonInfo);
            viewHolder.deviceButton.setOnClickListener(btnClickListener);

            return convertView;
        }
    }
    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonInfo buttonInfo =(buttonInfo) v.getTag();
            switch (v.getId()) {

                // 버튼 클릭
                case R.id.btn_connect:
                {
                    String change_connects_state = buttonInfo.viewHolder.deviceRename.getText().toString();
                    bleList.devices.set(buttonInfo.position,change_connects_state);
                    bleList.notifyDataSetChanged();

                }
                break;
            }
        }
    };


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        EditText deviceRename;
        Button deviceButton;

    }

    private class buttonInfo{
        ViewHolder viewHolder;
        int position;
    }

}


