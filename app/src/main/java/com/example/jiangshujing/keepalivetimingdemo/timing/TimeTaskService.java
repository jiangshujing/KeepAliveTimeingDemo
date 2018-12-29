package com.example.jiangshujing.keepalivetimingdemo.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.*;

public class TimeTaskService extends Service {

    private static final int INTERVAL_TIME = 10 * 1000 * 60;
    AlarmManager alarmManager;
    PendingIntent pIntent;
    Calendar mCalendar;

    public TimeTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //这里模拟后台操作
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "启动一个10分钟的定时通知", Toast.LENGTH_LONG).show();
            }
        });

        startTask(1000);

        return super.onStartCommand(intent, flags, startId);
    }


    public void startTask(int time) {
        if (time > 0) {
            mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            mCalendar.add(Calendar.MILLISECOND, time);//10秒钟定时

            //通过AlarmManager定时启动广播
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent timeTaskIntent = new Intent(this, AlarmReceiver.class);
            Random random = new Random();
            int code = random.nextInt(20) % (20 - 1 + 1) + 1;

            pIntent = PendingIntent.getBroadcast(this, code, timeTaskIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            int apiLevel = getApiLevel();//获取当前 api版本
            if (apiLevel < Build.VERSION_CODES.KITKAT) {
                Log.d("api<19", "setExactAlarmCompat ");
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), INTERVAL_TIME, pIntent);

            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.d("19<api<23", "setExactAlarmCompat ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("api>23", "setExactAlarmCompat ");
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pIntent);
            }
        }
    }

    public static int getApiLevel() {
        try {
            Field f = Build.VERSION.class.getField("SDK_INT");
            f.setAccessible(true);
            return f.getInt(null);
        } catch (Throwable e) {
            return 3;
        }
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(pIntent);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "停止后台循环任务，停止执行时间为："
                        + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
            }
        });
        Log.e("messages", "停止后台循环任务，停止执行时间为：" + System.currentTimeMillis());
    }


}
