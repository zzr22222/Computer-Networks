package TCP;

import java.io.*;
import java.net.*;
import java.nio.*;

public class MacOSReceiver {
    public static void main(String[] args) {
        try {
            // 创建套接字连接
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            // 接收数据
            byte[] receivedData = new byte[4];
            inputStream.readFully(receivedData);

            // 大端字节序（网络字节序）转回本机字节序
            ByteBuffer byteBuffer = ByteBuffer.wrap(receivedData);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            int receivedInt = byteBuffer.getInt();

            // 输出接收到的整数
            System.out.println("Received integer: 0x" + Integer.toHexString(receivedInt).toUpperCase());

            // 关闭连接
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
