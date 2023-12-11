package TCP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        JFrame frame = new JFrame("File Transfer Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JButton button = new JButton("Choose File");
        frame.getContentPane().add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        sendFile(selectedFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    private static void sendFile(File file) throws IOException {
        String serverAddress = "localhost";
        int port = 12345;
        Socket socket = new Socket(serverAddress, port);

        //文件读取流，打开文件进行读取
        FileInputStream fileInputStream = new FileInputStream(file);
        //将数据写入输出流中
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        //获取文件名和大小
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());
        //读取文件数据到缓冲区buffer，然后写入数据输出流
        byte[] buffer = new byte[4096];
        int read;
        while ((read = fileInputStream.read(buffer)) > 0) {
            dataOutputStream.write(buffer, 0, read);
        }

        fileInputStream.close();
        socket.close();
        System.out.println("File transfer complete.");
    }
}

