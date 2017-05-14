package com.example.wanghf.smartwaistcoat.inputdata;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.wanghf.smartwaistcoat.MainApplication;
import com.example.wanghf.smartwaistcoat.utils.BroadcastUtil;
import com.example.wanghf.smartwaistcoat.utils.ByteUtil;
import com.example.wanghf.smartwaistcoat.utils.FileUtil;

import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghf on 2017/4/10.
 */

public class DataParseService extends Service {
    private static final String TAG = "PlanAParseService";

    private ByteFifo byteFifo = null;
    private TranslateThread translateThread = null;
    private ByteUtil byteUtil = new ByteUtil();
    private FileUtil fileUtil = new FileUtil();
    private String dir = "AAA";
    private LinkedBlockingQueue<WaistcoatData> queue;
    private LinkedBlockingQueue<Byte> bytesQueue;
    private Context context;

    public void onCreate() {
        context = this;
        if (byteFifo == null) {
            byteFifo = ByteFifo.getInstance();
        }
        if (queue == null) {
            queue = MainApplication.getQueue();
        }
        if (bytesQueue == null) {
            bytesQueue = MainApplication.getBytes();
        }
        if (translateThread == null) {
            translateThread = new TranslateThread();
        }

        if(!translateThread.isAlive()) {
            translateThread.start();
            Log.d(TAG, "PARSE THREAD START");
        }
        super.onCreate();
    }
    private class TranslateThread extends Thread {
        private final int noneHead = 0;
        private final int head1 = 1;
        private final int head2 = 2;
        private final int head3 = 3;
        private final int head4 = 4;
        private final int head5 = 5;
        private final int head6 = 6;
        private int dataCount = 0;
        private int state = noneHead;
        private byte[] dataItem = new byte[64];

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        int sourceID = sharedPreferences.getInt("source_id", 1);

        public void run() {
            int i = 0;
            while (!interrupted()) {

//                i++;
//                BroadcastUtil.updateECG(context, i);
//                try {
//                    Thread.sleep(5);
//                } catch (Exception e) {
//                    break;
//                }
//                if (true) {
//                    continue;
//                }
                switch (state) {
                    case noneHead:
                        try {
                            byte head = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                            if (head > 0) {
                                state = head;
                                dataCount = 0;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "bytesQueue is empty");
                        }
                        break;
                    case head1:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 7) {
                                try {
                                   dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse1(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    case head2:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 15) {
                                try {
                                    dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse2(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    case head3:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 15) {
                                try {
                                    dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse3(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    case head4:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 15) {
                                try {
                                    dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse4(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    case head5:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 15) {
                                try {
                                    dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse5(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    case head6:
                        while (!bytesQueue.isEmpty()) {
                            if (dataCount < 15) {
                                try {
                                    dataItem[dataCount++] = bytesQueue.poll(2500, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    Log.i(TAG, "bytesQueue is empty");
                                }
                            } else {
                                dataParse6(Arrays.copyOfRange(dataItem, 0, dataCount));
                                state = noneHead;
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private void dataParse1(byte[] buffer) {
            if (buffer.length < 7) {
                return;
            }
            WaistcoatData waistcoatData = new WaistcoatData();
            waistcoatData.setDianliang(byteUtil.getInt1(buffer[1]));
            int xueyang = buffer[5] & 127;
            int xinlv = buffer[4] & 127;
            if ((buffer[3] & 0x1) == 1) {
                // 心率
                waistcoatData.setXinlv(xinlv + 128);
            }
            else {
                waistcoatData.setXinlv(xinlv);
            }
            waistcoatData.setXueyang(xueyang);
            waistcoatData.setWendu(((buffer[6] & 127) + 320) / 10);

            //更新数据表
            BroadcastUtil.updateDataTable(context, waistcoatData.clone());
        }

        private void dataParse2(byte[] buffer) {
            if (buffer.length < 15) {
                return;
            }
            int[] head = new int[12];

            for (int i = 0; i < 6; i++) {
                head[i] = (buffer[0] >> i) & 0x1;
            }
            for (int i = 6; i < 12; i++) {
                head[i] = (buffer[1] >> (i - 6)) & 0x1;
            }

            int data1 = ((buffer[2] & 0x7f) + head[0] * 128) * 256 * 256 +
                    ((buffer[3] & 0x7f) + 128 * head[1]) * 256 + (buffer[4] & 0x7f) + 128 * head[2];
            int data2 = ((buffer[5] & 0x7f) + head[3] * 128) * 256 * 256 +
                    ((buffer[6] & 0x7f) + 128 * head[4]) * 256 + (buffer[7] & 0x7f) + 128 * head[5];
            int data3 = ((buffer[8] & 0x7f) + head[6] * 128) * 256 * 256 +
                    ((buffer[9] & 0x7f) + 128 * head[7]) * 256 + (buffer[10] & 0x7f) + 128 * head[8];
            int data4 = ((buffer[11] & 0x7f) + head[9] * 128) * 256 * 256 +
                    ((buffer[12] & 0x7f) + 128 * head[10]) * 256 + (buffer[13] & 0x7f) + 128 * head[11];

            int tp = 32768 * 256;
            data1 = data1 > tp ? (data1 - tp * 2) : data1;
            data2 = data2 > tp ? (data2 - tp * 2) : data2;
            data3 = data3 > tp ? (data3 - tp * 2) : data3;
            data4 = data4 > tp ? (data4 - tp * 2) : data4;

            int limit = 1500;

            if (Math.abs(data1) > limit || Math.abs(data2) > limit || Math.abs(data3) > limit
                    || Math.abs(data4) > limit) {
                return;
            }

            BroadcastUtil.updateECG(context, data1);
            BroadcastUtil.updateECG(context, data2);
            BroadcastUtil.updateECG(context, data3);
            BroadcastUtil.updateECG(context, data4);

//            fileUtil.write2SDFromInputString(dir, "data.txt", data1 + "\n" + data2 + "\n" + data3 + "\n" + data4 + "\n");
        }

        private void dataParse3(byte[] buffer) {
            if (buffer.length < 15) {
                return;
            }
            int[] head = new int[12];

            for (int i = 0; i < 6; i++) {
                head[i] = (buffer[0] >> i) & 0x1;
            }
            for (int i = 6; i < 12; i++) {
                head[i] = (buffer[1] >> (i - 6)) & 0x1;
            }

            int data1 = ((buffer[2] & 0x7f) + head[5] * 128) * 256 * 256 +
                    ((buffer[3] & 0x7f) + 128 * head[4]) * 256 + (buffer[4] & 0x7f) + 128 * head[3];
            int data2 = ((buffer[5] & 0x7f) + head[2] * 128) * 256 * 256 +
                    ((buffer[6] & 0x7f) + 128 * head[1]) * 256 + (buffer[7] & 0x7f) + 128 * head[0];
            int data3 = ((buffer[11] & 0x7f) + head[8] * 128) * 256 * 256 +
                    ((buffer[9] & 0x7f) + 128 * head[10]) * 256 + (buffer[10] & 0x7f) + 128 * head[9];
            int data4 = ((buffer[11] & 0x7f) + head[8] * 128) * 256 * 256 +
                    ((buffer[12] & 0x7f) + 128 * head[7]) * 256 + (buffer[13] & 0x7f) + 128 * head[6];

            BroadcastUtil.updateImpedance(context, data1);
            BroadcastUtil.updateImpedance(context, data2);
            BroadcastUtil.updateImpedance(context, data3);
            BroadcastUtil.updateImpedance(context, data4);
        }

        private void dataParse4(byte[] buffer) {
            if (buffer.length < 15) {
                return;
            }
            int[] head = new int[12];

            for (int i = 0; i < 6; i++) {
                head[i] = (buffer[0] >> i) & 0x1;
            }
            for (int i = 6; i < 12; i++) {
                head[i] = (buffer[1] >> (i - 6)) & 0x1;
            }

            int data1 = ((buffer[2] & 0x7f) + head[5] * 128) * 256 + (buffer[3] & 0x7f) + 128 * head[4];
            int data2 = ((buffer[4] & 0x7f) + head[3] * 128) * 256 + (buffer[5] & 0x7f) + 128 * head[2];
            int data3 = ((buffer[6] & 0x7f) + head[1] * 128) * 256 + (buffer[7] & 0x7f) + 128 * head[0];
            int data4 = ((buffer[8] & 0x7f) + head[11] * 128) * 256 + (buffer[9] & 0x7f) + 128 * head[10];
            int data5 = ((buffer[10] & 0x7f) + head[9] * 128) * 256 + (buffer[11] & 0x7f) + 128 * head[8];
            int data6 = ((buffer[12] & 0x7f) + head[7] * 128) * 256 + (buffer[13] & 0x7f) + 128 * head[6];

            BroadcastUtil.updateStrike(context, data1);
            BroadcastUtil.updateStrike(context, data2);
            BroadcastUtil.updateStrike(context, data3);
            BroadcastUtil.updateStrike(context, data4);
            BroadcastUtil.updateStrike(context, data5);
            BroadcastUtil.updateStrike(context, data6);
        }

        private void dataParse5(byte[] buffer) {
            if (buffer.length < 15) {
                return;
            }
            int[] head = new int[12];

            for (int i = 0; i < 6; i++) {
                head[i] = (buffer[0] >> i) & 0x1;
            }
            for (int i = 6; i < 12; i++) {
                head[i] = (buffer[1] >> (i - 6)) & 0x1;
            }

            int data1 = ((buffer[2] & 0x7f) + head[5] * 128) * 256 + (buffer[3] & 0x7f) + 128 * head[4];
            int data2 = ((buffer[8] & 0x7f) + head[11] * 128) * 256 + (buffer[9] & 0x7f) + 128 * head[10];


            BroadcastUtil.updateStrike(context, data1);
            BroadcastUtil.updateStrike(context, data2);
        }

        private void dataParse6(byte[] buffer) {
            if (buffer.length < 15) {
                return;
            }
            int[] head = new int[12];

            for (int i = 0; i < 6; i++) {
                head[i] = (buffer[0] >> i) & 0x1;
            }
            for (int i = 6; i < 12; i++) {
                head[i] = (buffer[1] >> (i - 6)) & 0x1;
            }

            int data1 = ((buffer[2] & 0x7f) + head[5] * 256) * 256 * 256 * 256 +
                    ((buffer[3] & 0x7f) + 256 * head[4]) * 256 * 256 +
                    ((buffer[4] & 0x7f) + 256 * head[3]) * 256 + (buffer[5] & 127) + 256 * head[2];
            int data2 = ((buffer[6] & 0x7f) + head[1] * 256) * 256 * 256 * 256 +
                    ((buffer[7] & 0x7f) + 256 * head[0]) * 256 * 256 +
                    ((buffer[8] & 0x7f) + 256 * head[11]) * 256 + (buffer[9] & 0x7f) + 256 * head[10];
            int data3 = ((buffer[10] & 0x7f) + head[9] * 256) * 256 * 256 * 256 +
                    ((buffer[11] & 0x7f) + 256 * head[8]) * 256 * 256 +
                    ((buffer[12] & 0x7f) + 256 * head[7]) * 256 + (buffer[13] & 0x7f) + 256 * head[6];

            BroadcastUtil.updateImpedance(context, data1);
            BroadcastUtil.updateImpedance(context, data2);
            BroadcastUtil.updateImpedance(context, data3);
        }

    }
    public class ServiceBinder extends Binder {
        public DataParseService getService() {
            return DataParseService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public void onDestroy() {
        if (translateThread != null) {
            translateThread.interrupt();
        }
        if (!bytesQueue.isEmpty()) {
            bytesQueue.clear();
        }
        if (!queue.isEmpty())
            queue.clear();
    }
}
