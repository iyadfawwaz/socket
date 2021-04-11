package com.example.sock;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpServerService extends Service {

    public static int PORT=4445;
    private ServerSocket serverSocket=null;
    private boolean working = true;
    private Runnable runnable =new  Runnable() {

        Socket socket = null;

        @Override
        public void run() {

        try {
            serverSocket = new ServerSocket(PORT);
            while (working) {
                if (serverSocket != null) {
                    socket = serverSocket.accept();
                    System.out.println("New client: $socket");
                    DataInputStream dataInputStream =new  DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream =new  DataOutputStream(socket.getOutputStream());
                    System.out.println(""+dataInputStream.read());

                    // Use threads for each client to communicate with them simultaneously
                    Thread t  =new  TcpClientHandler(dataInputStream, dataOutputStream);
                    t.start();
                } else {
                   System.out.println("Couldn't create ServerSocket!");
                }
            }
        } catch (
        IOException exception) {
            exception.printStackTrace();
            try {
                socket.close();
            } catch ( IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    }
    ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        working=false;
    }

    private void startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "packageName";
            String channelName = "Tcp Server Background Service";
            NotificationChannel chan =new  NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility( Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Tcp Server is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        } else {
            startForeground(1,new Notification() );
        }
    }
}
