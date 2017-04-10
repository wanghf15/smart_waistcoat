package com.example.wanghf.smartwaistcoat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.wanghf.smartwaistcoat.inputdata.DataParseService;
import com.example.wanghf.smartwaistcoat.inputdata.WaistcoatData;
import com.example.wanghf.smartwaistcoat.inputdata.WiFiConnectService;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wanghf on 2017/4/10.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    private static LinkedBlockingQueue<Byte> bytes = null;
    private static LinkedBlockingQueue<WaistcoatData> queue = null;
    private WiFiConnectService wiFiConnectService;
    private DataParseService dataParseService;



    @Override
    public void onCreate() {
        final Context context = MainApplication.this;

        if (queue == null) {
            queue = new LinkedBlockingQueue<>();
        }

        if (bytes == null) {
            bytes = new LinkedBlockingQueue<>();
        }

        super.onCreate();
    }

    private ServiceConnection wiFiConnectServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            if (service != null) {
                wiFiConnectService = ((WiFiConnectService.ServiceBinder) service).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            wiFiConnectService = null;
        }
    };

    private ServiceConnection unityServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    private ServiceConnection dataParseServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            if (service != null) {
                dataParseService = ((DataParseService.ServiceBinder) service).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            dataParseService = null;
        }
    };
    public static LinkedBlockingQueue getQueue(){
        return queue;
    }

    public static LinkedBlockingQueue getBytes() {
        return bytes;
    }
}
