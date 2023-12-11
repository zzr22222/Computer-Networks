package TCP;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPTimeServer {
    public static void main(String[] args) throws IOException {
        int serverPort = 8080;
        //创建一个ServerSocket在端口8080监听客户请求
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("服务器正在运行，等待客户端连接...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();//使用accept()方法监听客户端请求
                     //BufferedReader对象用来表示从客户端输入到服务器的流
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     //PrintWriter对象表示服务器端输出到客户端的流
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    System.out.println("客户端已连接");

                    //判断输入字符值
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        //如果客户端输入time，则显示当前时间
                        if (inputLine.equalsIgnoreCase("Time")) {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentTime = new Date();
                            String serverTime = formatter.format(currentTime);
                            out.println("服务器当前时间: " + serverTime);
                            System.out.println(serverTime);
                            //如果客户端输入exit，则输出bye后退出程序
                        } else if (inputLine.equalsIgnoreCase("Exit")) {
                            out.println("Bye");
                            System.out.println("Bye");
                            break;
                        } else {
                            out.println("无效命令");
                            System.out.println("无效命令");
                        }
                    }
                }
            }
        }
    }
}
