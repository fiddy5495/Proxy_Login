package com.BlackBox.Wifi_Login.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.BlackBox.Wifi_Login.Activities.Main_Activity;
import com.BlackBox.Wifi_Login.R;

public class BackgroundService extends Service {

    private ConChangeReceiver br;

    final public String TAG = BackgroundService.class.getSimpleName() + " YOYO";
    private static final int Notification_ID = 5151;
    private Context context;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand  " + Thread.currentThread().getName());
        context = getApplicationContext();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        br = new ConChangeReceiver();

        Intent notificationIntent = new Intent(this, Main_Activity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = "WifiLogin_Service_01";
                CharSequence name = "Wifi_Login_Service";
                String description = "Service to automate Wifi Login";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
                notificationChannel.setDescription(description);
                notificationManager.createNotificationChannel(notificationChannel);

                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                notificationChannel.setSound(null,null);

                builder = new Notification.Builder(context, id)
                        .setContentTitle("Login Service")
                        .setContentText(description)
                        .setSmallIcon(R.drawable.ic_icon)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setCategory(Notification.CATEGORY_SERVICE);

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //noinspection deprecation
                    builder = new Notification.Builder(context)
                            .setContentTitle("Login Service")
                            .setSmallIcon(R.drawable.ic_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon))
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setVisibility(Notification.VISIBILITY_SECRET)
                            .setPriority(Notification.PRIORITY_MIN)
                            .setCategory(Notification.CATEGORY_SERVICE);
                } else {
                    //noinspection deprecation
                    builder = new Notification.Builder(context)
                            .setContentTitle("Login Service")
                            .setSmallIcon(R.drawable.ic_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon))
                            .setContentIntent(pendingIntent)
                            .setPriority(Notification.PRIORITY_MIN)
                            .setOngoing(true);
                }
            }

            startForeground(Notification_ID, builder.build());

            registerReceiver(br, intentFilter);

            return START_STICKY;
        }
        else{

            stopSelf();
            return START_REDELIVER_INTENT;

        }

//        Todo:
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            new JobInfo.Builder(125,new ComponentName(context,Login_Service.class))
//                    .setPersisted(true)
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        try{
            if(br!= null) {
                unregisterReceiver(br);
            }
        } catch (Exception e){
            // already unregistered
            Log.e(TAG, "onDestroy: br", e);
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory: ");
        Toast.makeText(context, "Low Memory", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDestroy");
        try{
            if(br!= null) {
                unregisterReceiver(br);
            }
        } catch (Exception e){
            // already unregistered
            Log.e(TAG, "onDestroy: br", e);
        }
        stopForeground(true);
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
