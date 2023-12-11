package TCP;

import java.io.*;
import java.net.*;

public class TCPFileClient {
    public static void main(String[] args) throws IOException {
        String serverName = "localhost";
        int serverPort = 8080;

        // 连接到服务器并处理输入和输出流
        try (Socket clientSocket = new Socket(serverName, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("已连接到服务器");

            String userInput;
            // 从控制台读取用户输入
            while ((userInput = consoleInput.readLine()) != null) {
                // 发送用户输入到服务器
                out.println(userInput);

                if (userInput.equalsIgnoreCase("File")) {
                    // 接收服务器发送的文件名
                    String fileName = in.readLine();
                    System.out.println("接收到的文件名: " + fileName);
                    // 自动发送 "Ready" 指令，告诉服务器客户端准备好接收文件
                    out.println("Ready");

                    // 接收文件，改名并将其保存在本地
                    File receivedFile = new File("received_" + fileName);
                    try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(receivedFile))) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        InputStream clientIn = clientSocket.getInputStream();

                        while ((bytesRead = clientIn.read(buffer)) > 0) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                        fileOut.flush();
                        System.out.println("文件已接收: " + receivedFile.getAbsolutePath());
                        break;
                    } catch (FileNotFoundException e) {
                        System.out.println("无法创建文件: " + receivedFile.getAbsolutePath());
                    }
                } //向服务器发送"UPLOAD"指令表示上传文件
                else  if (userInput.equalsIgnoreCase("UPLOAD")) {
                    System.out.print("请输入要上传的文件名: ");
                    String fileName = consoleInput.readLine();
                    System.out.println("上传的文件名: " + fileName);
                    out.println(fileName);
                    File fileToSend = new File(fileName);
                    if (fileToSend.exists()) {
                        try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend))) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            OutputStream serverOut = clientSocket.getOutputStream();
                            while ((bytesRead = fileIn.read(buffer)) > 0) {
                                serverOut.write(buffer, 0, bytesRead);
                            }
                            serverOut.flush();
                            System.out.println("文件已上传: " + fileToSend.getAbsolutePath());
                            break;
                        } catch (FileNotFoundException e) {
                            System.out.println("文件未找到: " + fileToSend.getAbsolutePath());
                        }
                    } else {
                        System.out.println("文件不存在: " + fileToSend.getAbsolutePath());
                    }
                }
                else {
                    // 接收并打印服务器的响应
                    String serverResponse = in.readLine();
                    out.println("服务器响应: " + serverResponse);
                }
            }
        }
    }
}
