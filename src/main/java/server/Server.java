package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    private ServerApp app;

    public Server(int port, ServerApp app) {
        this.port = port;
        this.app = app;
    }

    public void start() throws IOException {
        System.out.println("Start http-server...");
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("http-server running at: http://localhost:" + this.port);

        this.run();
    }

    private void run() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(clientSocket, this.app);
                Thread thread = new Thread(requestHandler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
