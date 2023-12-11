import java.net.InetAddress;

public class LocalHostInfo {
    public static void main(String[] args) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Local Hostname : " + localHost.getHostName());
            System.out.println("Local IP Address : " + localHost.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
