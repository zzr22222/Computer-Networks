package TCP;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private static JTextArea textArea;
    private static File saveDirectory;

    public static void main(String[] args) throws IOException {
        createUI();

        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);
        log("Server is listening on port " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            log("A client has connected.");

            FileTransferThread fileTransferThread = new FileTransferThread(socket);
            fileTransferThread.start();
        }
    }

    private static void createUI() {
        JFrame frame = new JFrame("File Transfer Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainPanel);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JLabel saveDirLabel = new JLabel("Save Directory:");
        bottomPanel.add(saveDirLabel);

        JTextField saveDirField = new JTextField(20);
        saveDirField.setEditable(false);
        bottomPanel.add(saveDirField);

        JButton chooseDirButton = new JButton("Choose...");
        bottomPanel.add(chooseDirButton);
        chooseDirButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                saveDirectory = fileChooser.getSelectedFile();
                saveDirField.setText(saveDirectory.getAbsolutePath());
            }
        });

        frame.setVisible(true);
    }

    public static void log(String message) {
        textArea.append(message + "\n");
    }

    public static File getSaveDirectory() {
        return saveDirectory;
    }
}

class FileTransferThread extends Thread {
    private Socket socket;

    public FileTransferThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //文件输入流读取发来的文件
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            //读取文件名和大小
            String fileName = dataInputStream.readUTF();
            long fileSize = dataInputStream.readLong();
            //保存到的指定路径
            File saveDirectory = TCPServer.getSaveDirectory();
            File outputFile = saveDirectory == null ? new File(fileName) : new File(saveDirectory, fileName);
            //将缓冲区的数据写入输出流
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[4096];
            int read;
            long remaining = fileSize;
            //每次读的字节数为缓冲区和剩余字节数中较少的一个
            while ((read = inputStream.read(buffer, 0, Math.min(buffer.length, (int) remaining))) > 0) {
                remaining -= read;
                fileOutputStream.write(buffer, 0, read);
            }

            fileOutputStream.close();
            socket.close();
            TCPServer.log("File transfer complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
