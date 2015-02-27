package com.example.demo.threads;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Handler uiThreadHandler;
    
    private Button addTask, cancelAll;
    private LinearLayout panel;
    
    private int counter = 0;
    
    public final static int
        MSG_SET_SIZE = 1,
        MSG_UPDATE_PROGRESS = 2,
        MSG_FINISHED = 3;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        // Create the Handler. It will implicitly bind to the Looper
        // that is internally created for this thread (since it is the UI thread)
        uiThreadHandler = new Handler() {
            
            public void handleMessage(Message msg) {
                View row = panel.getChildAt(msg.arg1);
                ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
                TextView status = (TextView) row.findViewById(R.id.status);
                
                switch(msg.what) {
                case MSG_SET_SIZE:
                    progressBar.setMax(msg.arg2);
                    status.setText(R.string.downloading);
                    break;
                case MSG_UPDATE_PROGRESS:
                    progressBar.setProgress(msg.arg2);
                    break;
                case MSG_FINISHED:
                	progressBar.setProgress(progressBar.getMax());
                    status.setText(R.string.finished);
                    break;
                default:
                    break;
                }
            }
            
        };
        
        final LayoutInflater inflater = getLayoutInflater();
        
        addTask = (Button) findViewById(R.id.add_task);
        cancelAll = (Button) findViewById(R.id.cancel_all);
        panel = (LinearLayout) findViewById(R.id.panel);
        
        final LooperThread looperThread = new LooperThread(uiThreadHandler);
        looperThread.start();
        
        addTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View downloadView = inflater.inflate(R.layout.download_control, null);
                TextView index = (TextView) downloadView.findViewById(R.id.index);
                index.setText(String.format("%d", counter));
                panel.addView(downloadView);
                
                looperThread.addJob(counter);
                
                ++counter;
            }
        });
        
        cancelAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				looperThread.cancelAll();
			}
		});
    }
}
