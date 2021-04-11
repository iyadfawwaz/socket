package com.example.sock;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class TcpClientHandler extends Thread {

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public TcpClientHandler(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(dataInputStream.available() > 0){
                    Log.i("TAG", "Received: " + dataInputStream.readUTF());
                    dataOutputStream.writeUTF("Hello Client");
                    sleep(2000L);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                try {
                    dataInputStream.close();
                    dataOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    dataInputStream.close();
                    dataOutputStream.close();
                } catch ( IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
