package com.example.demo.threads;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class LooperThread extends Thread {
    
    public enum Action {
        ADD,
        CANCEL_ALL,
    }
    
    private Handler
        uiThreadHandler,
        downloadHandler;
    
    public LooperThread(Handler uiThreadHandler) {
        if(uiThreadHandler.getLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("Must give the UI Thread Handler");
        }
        this.uiThreadHandler = uiThreadHandler;
    }
    
    public void run() {
        Looper.prepare();

        downloadHandler = new Handler() {
            public void handleMessage(Message msg) {
                
                Action act = Action.values()[msg.what];
                
                // process incoming messages here
                switch (act) {
                case ADD:
                    int size = getSize();
                    reportMessageToUI(MainActivity.MSG_SET_SIZE, msg.arg1, size);
                    
                    int progress = 0;
                    while(progress < size) {
                        progress++;
                        if(0 == progress % 10240) {
                            reportMessageToUI(MainActivity.MSG_UPDATE_PROGRESS, msg.arg1, progress);
                        }
                    }
                    reportMessageToUI(MainActivity.MSG_FINISHED, msg.arg1, 0);
                    break;
                case CANCEL_ALL:
                    this.getLooper().quit();
                default:
                    break;
                }
            }
        };

        Looper.loop();
    }
    
    private int getSize() {
        int size = Double.valueOf(Math.random() * Integer.MAX_VALUE / 10).intValue();
        return size;
    }
    
    public void addJob(int index) {
        doAction(Action.ADD, index);
    }
    
    public void cancelAll() {
    	downloadHandler.getLooper().quit();
    }
    
    private boolean doAction(Action action, int arg1) {
        return doAction(action, arg1, 0);
    }
    
    private boolean doAction(Action action, int arg1, int arg2) {
        Message msg = obtainMessage(action, arg1, arg2);
        return downloadHandler.sendMessage(msg);
    }
    
    private Message obtainMessage(Action action, int arg1, int arg2) {
    	return downloadHandler.obtainMessage(action.ordinal(), arg1, arg2);
    }
    
    private boolean reportMessageToUI(int what, int arg1, int arg2) {
        Message msg = uiThreadHandler.obtainMessage(what, arg1, arg2);
        return uiThreadHandler.sendMessage(msg);
    }
}