import java.net.*;
import java.io.*;

public class Client {
    private Socket clientSocket;
    private static PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("*************** Client ***************");
        Client client = new Client();
        System.out.println("Host = "+ args[0] + "    Port = 8000");
        while (true) {
            System.out.println("Start connection with server");
            client.startConnection("127.0.0.1", 8000);
            System.out.println("Send heart beat to server");
            out.println(args[0]);
            Thread.sleep(2000);
        }
    }
}