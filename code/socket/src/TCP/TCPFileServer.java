package TCP;

import java.io.*;
import java.net.*;

public class TCPFileServer {
    public static void main(String[] args) throws IOException {
        int serverPort = 8080;
        File fileToSend = new File("example.txt"); // 要发送的文件

        // 创建服务器套接字并等待客户端连接
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("服务器正在运行，等待客户端连接...");

            while (true) {
                // 客户端连接后，处理输入和输出流
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("客户端已连接");

                    String inputLine;
                    // 读取客户端发送file指令
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.equalsIgnoreCase("File")) {
                            // 发送文件名给客户端
                            System.out.println("服务器接受到指令：" + inputLine);
                            System.out.println("服务器传输文件名：" + fileToSend.getName());
                            out.println(fileToSend.getName());

                            String clientResponse = in.readLine();
                            // 当客户端发送ready准备接收文件时，发送文件
                            if (clientResponse.equalsIgnoreCase("Ready")) {
                                //打印
                                System.out.println("服务器接受到指令：Ready");
                                System.out.println("服务器开始传输文件 " + fileToSend.getName());
                                //读取txt文件内容
                                try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend))) {
                                    byte[] buffer = new byte[8192];
                                    int bytesRead;
                                    OutputStream clientOut = clientSocket.getOutputStream();

                                    while ((bytesRead = fileIn.read(buffer)) > 0) {
                                        clientOut.write(buffer, 0, bytesRead);
                                    }
                                    //打印已发送文件的指令
                                    System.out.println("已发送文件：example.txt");

                                    clientOut.flush();
                                    break;
                                } catch (FileNotFoundException e) {
                                    System.out.println("文件未找到: " + fileToSend.getAbsolutePath());
                                }
                            }
                            //如果收到的是"UPLOAD"指令，则接收客户端上传的文件
                        } else if (inputLine.equalsIgnoreCase("UPLOAD")) {
                            String fileName = in.readLine();
                            System.out.println("接收到的文件名: " + fileName);
                            File fileToSave = new File("fromClient_" + fileName);
                            //将接收到的文件内容写入txt
                            try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileToSave))) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                InputStream clientIn = clientSocket.getInputStream();

                                while ((bytesRead = clientIn.read(buffer)) > 0) {
                                    fileOut.write(buffer, 0, bytesRead);
                                }
                                fileOut.flush();
                                System.out.println("文件已保存: " + fileToSave.getAbsolutePath());
                                break;
                            } catch (FileNotFoundException e) {
                                System.out.println("无法创建文件: " + fileToSave.getAbsolutePath());
                            }
                        } else {
                            System.out.println("无效命令");
                        }
                    }
                }
            }
        }
    }
}
