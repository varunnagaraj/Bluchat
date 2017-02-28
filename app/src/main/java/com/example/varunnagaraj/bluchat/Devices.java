package com.example.varunnagaraj.bluchat;

/**
 * Created by Varun Nagaraj on 27-02-2017.
 */

public class Devices {
    private int _id;
    private String _deviceName;
    private String _deviceAddress;
    private String _deviceRSSI;

    public Devices(){

    }
    public Devices(String _deviceName, String _deviceAddress, String _deviceRSSI) {
        this._deviceName = _deviceName;
        this._deviceAddress = _deviceAddress;
        this._deviceRSSI = _deviceRSSI;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_deviceName() {
        return _deviceName;
    }

    public void set_deviceName(String _deviceName) {
        this._deviceName = _deviceName;
    }

    public String get_deviceAddress() {
        return _deviceAddress;
    }

    public void set_deviceAddress(String _deviceAddress) {
        this._deviceAddress = _deviceAddress;
    }

    public String get_deviceRSSI() {
        return _deviceRSSI;
    }

    public void set_deviceRSSI(String _deviceRSSI) {
        this._deviceRSSI = _deviceRSSI;
    }
}
