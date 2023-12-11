import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerDatabase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/school?useSSL=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";


    public static void main(String[] args) throws IOException {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器启动，等待客户端连接...");
            //String inputLine;
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    // String inputLine;
//                    while ((inputLine = in.readLine()) != null) {
//                        System.out.println("收到学生学号: " + inputLine);
//                        String studentInfo = getStudentInfo(inputLine);

//                        if (studentInfo != null) {
//                            out.write(studentInfo);
//                        } else {
//                            out.write("未找到学生信息");
//                        }
//                        out.newLine();
//                        out.flush(); // 需要手动刷新缓冲区
                    //1
//                        if (studentInfo != null) {
//                            out.write(String.format("%-5s%-5s", studentInfo.split(",")[0], studentInfo.split(",")[1]));
//                        } else {
//                            out.write(String.format("%-5s%-5s", "未找到学生信息", ""));
//                        }
//                        out.flush();
                    //2
                    //读取输入输出流
//                    DataInputStream dataIn = new DataInputStream(socket.getInputStream());
//                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
//
//                    String inputLine;
//                    while ((inputLine = dataIn.readUTF()) != null) {
//                        System.out.println("收到学生学号: " + inputLine);
//                        String studentInfo = getStudentInfo(inputLine);
//                        //将信息转为字节数组
//                        byte[] responseBytes;
//                        if (studentInfo != null) {
//                            responseBytes = studentInfo.getBytes();
//                        } else {
//                            responseBytes = "未找到学生信息".getBytes();
//                        }
//                        //输出响应数据长度和实际的响应数据
//                        dataOut.writeInt(responseBytes.length);
//                        dataOut.write(responseBytes);
//                        dataOut.flush();
//                    }
                    //3
                    StringBuilder inputBuilder = new StringBuilder();
                    int c;
                    while ((c = in.read()) != -1) {
                        if (c == 0) {
                            String inputLine = inputBuilder.toString();
                            System.out.println("收到学生学号: " + inputLine);
                            String studentInfo = getStudentInfo(inputLine);
                            if (studentInfo != null) {
                                out.write(studentInfo + "\0");//分隔符
                            } else {
                                out.write("未找到学生信息\0");//分隔符
                            }
                            out.flush();
                            inputBuilder.setLength(0);
                        } else {
                            inputBuilder.append((char) c);
                        }
                    }
                }
            }
        }
    }


    private static String getStudentInfo(String studentID) {
        String studentInfo = null;

        //建立数据库连接
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             //预处理sql查询语句
             PreparedStatement statement = connection.prepareStatement("SELECT name, gender FROM students WHERE sid = ?")) {
            //将sql中的占位符？替换为学生学号
            statement.setString(1, studentID);
            //开始查询
            ResultSet resultSet = statement.executeQuery();
            //拼接查询到的姓名和性别
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String gender = resultSet.getString("gender");
                studentInfo = name + "," + gender;
            }
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
        }

        return studentInfo;
    }
}
