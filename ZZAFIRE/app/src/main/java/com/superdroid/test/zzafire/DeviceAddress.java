package com.superdroid.test.zzafire;

import com.orm.SugarRecord;

public class DeviceAddress extends SugarRecord {
    public String deviceaddr;

    public DeviceAddress()
    {}

    public DeviceAddress(String deviceaddr)
    {
        this.deviceaddr = deviceaddr;
    }
}