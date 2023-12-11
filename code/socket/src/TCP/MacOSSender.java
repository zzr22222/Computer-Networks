package TCP;

import java.io.*;
import java.net.*;
import java.nio.*;

public class MacOSSender {
    public static void main(String[] args) {
        try {
            // 创建套接字连接目的端口
            Socket socket = new Socket("172.26.189.122", 8080); //待发送的ip地址和端口号
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // 需要发送的整数
            int dataToSend = 0x12345678;

            // 转换为大端字节序
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            //将下面的方法注释，再次尝试
           // byteBuffer.order(ByteOrder.BIG_ENDIAN);
            byteBuffer.putInt(dataToSend);

            // 发送数据
            outputStream.write(byteBuffer.array());
            outputStream.flush();

            // 关闭连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
