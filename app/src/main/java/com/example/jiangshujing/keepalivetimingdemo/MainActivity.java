package com.example.jiangshujing.keepalivetimingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.example.jiangshujing.keepalivetimingdemo.keepalive.receiver.ScreenReceiverUtil;
import com.example.jiangshujing.keepalivetimingdemo.keepalive.service.DaemonService;
import com.example.jiangshujing.keepalivetimingdemo.keepalive.utils.Contants;
import com.example.jiangshujing.keepalivetimingdemo.keepalive.utils.JobSchedulerManager;
import com.example.jiangshujing.keepalivetimingdemo.keepalive.utils.ScreenManager;
import com.example.jiangshujing.keepalivetimingdemo.timing.TimeTaskService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Contants.DEBUG)
            Log.d(TAG, "--->onCreate");
        // 1. 注册锁屏广播监听器
//        mScreenListener = new ScreenReceiverUtil(this);
//        mScreenManager = ScreenManager.getScreenManagerInstance(this);
//        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();

        findViewById(R.id.open_time_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mOneIntent = new Intent(MainActivity.this, TimeTaskService.class);
                startService(mOneIntent);
            }
        });
    }


    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };


    private void startDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(MainActivity.this, DaemonService.class);
        stopService(intent);
    }

}
