package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
public static ServerSocket server;
public static final int PORT = 8189;
public static Socket socket;
private List<ClientHandler> clients;
private AuthService authService;


    public Server() {

        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();
        try {

            server =  new ServerSocket (PORT);
            System.out.println("Сервер запустился");



            while (true) {


                socket = server.accept();
                System.out.println("Client connected" + socket.getRemoteSocketAddress());


                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(ClientHandler sender, String msg) {
        String message = String.format("%s: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}


