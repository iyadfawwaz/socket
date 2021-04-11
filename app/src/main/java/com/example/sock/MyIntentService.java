package com.example.sock;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

        static String UDP_BROADCAST = "UDPBroadcast";

        //Boolean shouldListenForUDPBroadcast = false;
        DatagramSocket socket;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception {
            byte[] recvBuf = new byte[15000];
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket(port, broadcastIP);
                socket.setBroadcast(true);
            }
            //socket.setSoTimeout(1000);
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            Log.e("UDP", "Waiting for UDP broadcast");
            socket.receive(packet);

            String senderIP = packet.getAddress().getHostAddress();
            String message = new String(packet.getData()).trim();

            Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);

            broadcastIntent(senderIP, message);
            socket.close();
        }

        private void broadcastIntent(String senderIP, String message) {
            Intent intent = new Intent(UDPListenerService.UDP_BROADCAST);
            intent.putExtra("sender", senderIP);
            intent.putExtra("message", message);
            sendBroadcast(intent);
        }

        Thread UDPBroadcastThread;

        void startListenForUDPBroadcast() {
            UDPBroadcastThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        InetAddress broadcastIP = InetAddress.getByName("10.0.0.82"); //172.16.238.42 //192.168.1.255
                        Integer port = 514;
                        while (shouldRestartSocketListen) {
                            listenAndWaitAndThrowIntent(broadcastIP, port);
                        }
                        //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                    } catch (Exception e) {
                        Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                    }
                }
            });
            UDPBroadcastThread.start();
        }

        private Boolean shouldRestartSocketListen=true;

        void stopListen() {
            shouldRestartSocketListen = false;
            socket.close();
        }

        @Override
        public void onCreate() {

        };

        @Override
        public void onDestroy() {
            stopListen();
        }


        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            shouldRestartSocketListen = true;
            startListenForUDPBroadcast();
            Log.i("UDP", "Service started");
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

}