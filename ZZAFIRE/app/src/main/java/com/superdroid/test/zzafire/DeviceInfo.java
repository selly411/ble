package com.superdroid.test.zzafire;

import com.orm.SugarRecord;

public class DeviceInfo extends SugarRecord {
    public String address;
    public String hrdata;
    public String year_month;
    public String time;

    public DeviceInfo()
    {}

    public DeviceInfo(String address, String hrdata, String year_month, String time)
    {

        this.address = address;
        this.hrdata = hrdata;
        this.year_month = year_month;
        this.time = time;
    }
}