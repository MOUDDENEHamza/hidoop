import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<String> list;
    
    public Server () {
        this.list = new ArrayList<>();
    }

    public void start(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String host = in.readLine();
                if (!this.list.contains(host)) {
                    this.list.add(host);
                }
                System.out.println(this.list);
            }
        } catch (SocketTimeoutException e ) {
            serverSocket.close();
            if (clientSocket != null) {
                clientSocket.close();
            }
        }
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
    public static void main(String[] args) throws IOException {
        System.out.println("******************* Server *******************");
        Server server= new Server();
        System.out.println("Port = 8000");
        server.start(8000);
    }
}