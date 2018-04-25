package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListener extends Thread {
    ServerSocket serverSocket;
    public ClientListener() {
        try{
            serverSocket = new ServerSocket(0);

        }catch (Exception e) {

        }

    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            while(true) {
                Socket sk = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                System.out.println(in.readLine());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
