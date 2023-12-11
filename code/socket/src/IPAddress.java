import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
public class IPAddress {
    public static void main(String[] args) {
        //遍历三个网站
        String[] websites = {"www.baidu.com", "www.csdn.net", "www.google.com"};
        for (String website : websites) {
            try {
                //获取数组网站的所有IP地址，并打印
                InetAddress[] addresses = InetAddress.getAllByName(website);
                System.out.println("IP addresses for " + website + ": " + Arrays.toString(addresses));

            } catch (UnknownHostException ex) {
                //若找不到ip，则输出错误信息
                System.out.println("Could not find IP addresses for " + website);
            }
        }
    }
}


