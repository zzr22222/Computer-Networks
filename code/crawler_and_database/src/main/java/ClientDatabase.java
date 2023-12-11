import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientDatabase {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("请输入一个学生学号：");

//            while (scanner.hasNext()) {
//                String studentID = scanner.nextLine();
//                out.write(studentID);
//                out.newLine();
//                out.flush();
//                String response = in.readLine();
//                System.out.println("收到服务器响应: " + response);
//
//                System.out.println("请输入一个学生学号：");
//            }
            //解决粘包方式1.固定长度的数据包方法：
//            while (scanner.hasNext()) {
//                String studentID = scanner.nextLine();
//                out.write(studentID);
//                out.newLine();
//                out.flush();
//                char[] responseChars = new char[10];
//                in.read(responseChars, 0, 10);
//                String response = new String(responseChars).trim();
//                System.out.println("收到服务器响应: " + response);
//                System.out.println("请输入一个学生学号：");
//            }
            //解决粘包方式2.数据包头方法
//            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
//            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
//
//            while (scanner.hasNext()) {
//                String studentID = scanner.nextLine();
//                dataOut.writeUTF(studentID);
//                dataOut.flush();
//                //数据包长度int--4字节
//                int responseLength = dataIn.readInt();
//                byte[] responseBytes = new byte[responseLength];
//                dataIn.readFully(responseBytes);
//                String response = new String(responseBytes);
//                System.out.println("收到服务器响应: " + response);
//                System.out.println("请输入一个学生学号：");
//            }
            //解决粘包方式3.特殊分隔符方法
            while (scanner.hasNext()) {
                String studentID = scanner.nextLine();
                out.write(studentID + "\0"); //接收到的数据包添加一个分隔符
                out.flush();
                StringBuilder responseBuilder = new StringBuilder();
                int c;
                while ((c = in.read()) != 0) {
                    responseBuilder.append((char) c);
                }
                String response = responseBuilder.toString();
                System.out.println("收到服务器响应: " + response);
                System.out.println("请输入一个学生学号：");
            }
        }
    }
}

