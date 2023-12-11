package UDP;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;


public class UDPClient {
    private static final int CLIENT_PORT = 0;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final String CLIENT_A_NAME = "A";
    private static final String CLIENT_B_NAME = "B";
    private static final String CLIENT_C_NAME = "C";


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UDPClient clientA = new UDPClient(CLIENT_A_NAME);
            clientA.createAndShowGUI();
            clientA.startListening();

            UDPClient clientB = new UDPClient(CLIENT_B_NAME);
            clientB.createAndShowGUI();
            clientB.startListening();

            UDPClient clientC = new UDPClient(CLIENT_C_NAME);
            clientC.createAndShowGUI();
            clientC.startListening();

        });
    }

    private String clientName;
    private DatagramSocket clientSocket;
    private JTextArea chatHistoryTextArea;
    private JTextField chatInputTextField;

    public UDPClient(String clientName) {
        this.clientName = clientName;
        try {
            this.clientSocket = new DatagramSocket(CLIENT_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI() {
        /** 创建并设置窗口*/
        JFrame frame = new JFrame("Chat Client - " + clientName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        /** 创建聊天历史文本区域*/
        chatHistoryTextArea = new JTextArea();
        chatHistoryTextArea.setEditable(false);
        // 修改字体以支持macOS上的表情
        chatHistoryTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatHistoryTextArea.setLineWrap(true);
        chatHistoryTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatHistoryTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        /**创建输入面板 */
        JPanel inputPanel = new JPanel(new BorderLayout());
        chatInputTextField = new JTextField();
        chatHistoryTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(chatInputTextField, BorderLayout.CENTER);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        /** 创建按钮面板 */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 创建并添加表情选择下拉框
        JComboBox<String> emojiComboBox = new JComboBox<>(new String[]{"😊", "😃", "😄", "😁", "😆", "😅", "😂", "🤣"});
        emojiComboBox.setFont(new Font("Apple Color Emoji", Font.PLAIN, 14));
        emojiComboBox.addActionListener(e -> {
            String selectedEmoji = (String) emojiComboBox.getSelectedItem();
            chatInputTextField.setText(chatInputTextField.getText() + selectedEmoji);
            chatInputTextField.requestFocus();
        });
        buttonPanel.add(emojiComboBox);

        // 创建并添加文件选择按钮
        JButton fileButton = new JButton("Choose File");
        fileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        fileButton.addActionListener(new FileButtonActionListener());
        buttonPanel.add(fileButton);

        // 创建并添加发送按钮
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton.addActionListener(new SendButtonActionListener());
        buttonPanel.add(sendButton);

        // 创建并添加清除按钮
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(e -> chatInputTextField.setText(""));
        buttonPanel.add(clearButton);

        // 创建并添加退出按钮
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(e -> {
            try {
                unregisterFromServer();
                clientSocket.close();
                System.exit(0);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        buttonPanel.add(exitButton);


        inputPanel.add(buttonPanel, BorderLayout.EAST);
        contentPane.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // 向服务器注册客户端
        try {
            registerToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     //开始监听接收消息
    private void startListening() {
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024 * 64];
                while (true) {
                    // 接收从服务器发来的数据包
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String receivedText = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Message message = Message.fromString(receivedText);

                    // 在聊天历史中显示收到的消息或文件
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentTime = new Date();
                    String serverTime = formatter.format(currentTime);
//// 开始监听接收消息
//    private void startListening() {
//        new Thread(() -> {
//            try {
//                int BUFFER_SIZE = 2048;
//                int CHUNK_SIZE = 1024;
//                byte[] receiveData = new byte[BUFFER_SIZE];
//                while (true) {
//                    // 接收从服务器发来的数据包
//                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                    clientSocket.receive(receivePacket);
//                    String receivedText = new String(receivePacket.getData(), 0, receivePacket.getLength());
//                    Message message = Message.fromString(receivedText);
//
//                    // 在聊天历史中显示收到的消息或文件
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date currentTime = new Date();
//                    String serverTime = formatter.format(currentTime);
//-----------------

                    if (message.getType() == Message.Type.CHAT) {
                        chatHistoryTextArea.append("(" + serverTime + ")" + message.getSender() + ": " + message.getContent() + "\n");
                    } else if (message.getType() == Message.Type.FILE) {
                        String[] parts = message.getContent().split("::", 2);
                        String fileName = parts[0];
                        String fileContentBase64 = parts[1];
                        byte[] fileContent = Base64.getDecoder().decode(fileContentBase64);
//                    } else if (message.getType() == Message.Type.FILE) {
//                        String[] parts = message.getContent().split("::", 10);
//                        String fileName = parts[0];
//                        String fileContentBase64 = parts[1];
//
//                        List<String> fileContentChunks = new ArrayList<>();
//                        int start = 0;
//                        while (start < fileContentBase64.length()) {
//                            int end = Math.min(start + CHUNK_SIZE, fileContentBase64.length());
//                            fileContentChunks.add(fileContentBase64.substring(start, end));
//                            start = end;
//                        }
//
//                        byte[] fileContent = Base64.getDecoder().decode(String.join("", fileContentChunks));
//---------


                        // 将文件保存到客户端的文件系统中
                        String saveDirectory = "received_files"; // 用于保存接收到的文件的目录
                        File directory = new File(saveDirectory);
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        File receivedFile = new File(directory, fileName);
                        Files.write(receivedFile.toPath(), fileContent);

                        chatHistoryTextArea.append("(" + serverTime + ")" + message.getSender() + " sent a file: " + fileName + "\n");
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    // 向服务器注册客户端
    private void registerToServer() throws IOException {
        Message registerMessage = new Message(Message.Type.REGISTER, clientName, null, null);
        byte[] sendData = registerMessage.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
        clientSocket.send(sendPacket);
    }
    // 向服务器注销客户端
    private void unregisterFromServer() throws IOException {
        Message unregisterMessage = new Message(Message.Type.UNREGISTER, clientName, null, null);
        byte[] sendData = unregisterMessage.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
        clientSocket.send(sendPacket);
    }

    // 发送按钮的事件处理器
    private class SendButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String chatInputText = chatInputTextField.getText().trim();
            if (!chatInputText.isEmpty()) {
                try {
                    // 构建并发送聊天消息
                    String[] receivers = {CLIENT_A_NAME, CLIENT_B_NAME, CLIENT_C_NAME};
                    for (String receiver : receivers) {
                        Message chatMessage = new Message(Message.Type.CHAT, clientName, receiver, chatInputText);
                        byte[] sendData = chatMessage.toString().getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
                        clientSocket.send(sendPacket);
                    }
                    // 清空输入框并将焦点返回到输入框
                    chatInputTextField.setText("");
                    chatInputTextField.requestFocus();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    // 文件按钮的事件处理器
    private class FileButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //创建一个JFileChooser对象，用于弹出文件选择对话框
            JFileChooser fileChooser = new JFileChooser();
            //创建一个FileNameExtensionFilter对象，用于筛选允许的文件类型（包括文本、图像和音频文件）
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text, Image, and Audio files", "txt", "jpg", "jpeg", "png", "gif", "mp3", "wav");
            //应用过滤器
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            //如果用户选择了一个文件并单击了“打开”按钮，返回值将等于JFileChooser.APPROVE_OPTION
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                //获取用户选择的文件
                File file = fileChooser.getSelectedFile();
                try {
                    //发送文件
                    sendFile(file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
     //发送文件
    private void sendFile(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String fileContent = Base64.getEncoder().encodeToString(fileBytes);
        String[] receivers = {CLIENT_A_NAME, CLIENT_B_NAME, CLIENT_C_NAME};
        //转换发送的数据为字节数组，通过clientSocket发送给服务器
        for (String receiver : receivers) {
            Message fileMessage = new Message(Message.Type.FILE, clientName, receiver, file.getName() + "::" + fileContent);
            byte[] sendData = fileMessage.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
            clientSocket.send(sendPacket);
        }
        //显示在聊天历史区
        chatHistoryTextArea.append(clientName + " sent a file: " + file.getName() + "\n");
    }
//     private void sendFile(File file) throws IOException {
//         byte[] fileBytes = Files.readAllBytes(file.toPath());
//        String[] receivers = {CLIENT_A_NAME, CLIENT_B_NAME, CLIENT_C_NAME};
//         int chunkSize = 1024; // 1024分片大小
//         int totalChunks = (int) Math.ceil((double) fileBytes.length / chunkSize);
//
//         for (int i = 0; i < totalChunks; i++) {
//             int startIndex = i * chunkSize;
//             int endIndex = Math.min(startIndex + chunkSize, fileBytes.length);
//             byte[] fileFragment = Arrays.copyOfRange(fileBytes, startIndex, endIndex);
//             String fileFragmentBase64 = Base64.getEncoder().encodeToString(fileFragment);
//
//             for (String receiver : receivers) {
//                 Message fileMessage = new Message(Message.Type.FILE, clientName, receiver, file.getName() + "::" + fileFragmentBase64, i, totalChunks);
//                 byte[] sendData = fileMessage.toString().getBytes();
//                 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
//                 clientSocket.send(sendPacket);
//             }
//         }
//     }
}
