package main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        try{
            Socket socket = new Socket("localhost", 50000);

//            OutputStream outputStream = socket.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//
//            dataOutputStream.writeInt(Integer.parseInt(args[0]));

//            InputStream inputStream = socket.getInputStream();
//            DataInputStream dataInputStream = new DataInputStream(inputStream);
            ClientListener cl = new ClientListener();
            cl.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(cl.getServerSocket().getLocalPort() + "\n");
            out.flush();
            String s = "";

            while (true) {
                int a = sc.nextInt();
                if (a == 1) {
                    out.write("info\n");
                    out.flush();
                    s = in.readLine();
                    System.out.println(s);
                } if (a == 2) {
                    String[] users = s.split(";");

                    int id = sc.nextInt();
                    String[] data = users[id].split(",");
                    String ss = "TEETEAA\n";
                    System.out.println(data[1]);
                    Socket tempS = new Socket("127.0.0.1", Integer.parseInt(data[1]));
                    BufferedWriter tempOut = new BufferedWriter(new OutputStreamWriter(tempS.getOutputStream()));
                    tempOut.write(ss);
                    tempOut.flush();
                    tempOut.close();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
