package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class ClientListener extends Service {
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
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {


                try {
                    while (!this.isCancelled()) {
                        Socket sk = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                        System.out.println(in.readLine());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.cancel();
                }
                return null;

            }
        };
    }

}
