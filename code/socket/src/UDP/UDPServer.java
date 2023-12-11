package UDP;

import UDP.ClientInfo;
import UDP.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;


public class UDPServer {
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        new UDPServer().run();
    }

    private void run() {
        // 用于存储客户端信息的映射
        Map<String, ClientInfo> clients = new HashMap<>();

        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            byte[] receiveData = new byte[4096 * 4096]; // 增大缓冲区以处理文件传输
            while (true) {
                // 接收客户端发送的数据包
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String receivedText = new String(receivePacket.getData(), 0, receivePacket.getLength());
                Message message = Message.fromString(receivedText);

                // 根据消息类型进行处理
                switch (message.getType()) {
                    case REGISTER:
                        // 注册客户端
                        clients.put(message.getSender(), new ClientInfo(receivePacket.getAddress(), receivePacket.getPort()));
                        break;
                    case CHAT:
                        // 将聊天消息转发给接收者
                        ClientInfo receiverInfo = clients.get(message.getReceiver());
                        if (receiverInfo != null) {
                            byte[] sendData = receivedText.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverInfo.getAddress(), receiverInfo.getPort());
                            serverSocket.send(sendPacket);
                        }
                        break;
                    case UNREGISTER:
                        // 移除客户端
                        clients.remove(message.getSender());
                        break;
                    case FILE:
                        // 将文件消息转发给接收者
                        ClientInfo fileReceiverInfo = clients.get(message.getReceiver());
                        if (fileReceiverInfo != null) {
                            byte[] sendData = receivedText.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, fileReceiverInfo.getAddress(), fileReceiverInfo.getPort());
                            serverSocket.send(sendPacket);
                        }
                        break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
