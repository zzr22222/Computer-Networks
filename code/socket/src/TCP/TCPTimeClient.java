package TCP;

import java.io.*;
import java.net.*;

public class TCPTimeClient {
    public static void main(String[] args) throws IOException {
        String serverName = "localhost";
        int serverPort = 8080;

        try (Socket clientSocket = new Socket(serverName, serverPort);
             //建立客户端的输入输出流
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             //读取终端指令
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("已连接到服务器");

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
                //客户端接收到服务器回传的指令
                String serverResponse = in.readLine();
                System.out.println("服务器响应: " + serverResponse);

                //如果客户端发送exit指令，则服务器将显示bye，同时客户端主动断开与socket的连接
                if (serverResponse.equalsIgnoreCase("Bye")) {
                    System.out.println("关闭连接...");
                    clientSocket.close();
                    break;
                }
            }
        }
    }
}

