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

        authService = new DBconection();
        clients = new CopyOnWriteArrayList<>();
//        authService = new SimpleAuthService();
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
    public void PrivateMessage(ClientHandler sender,String recipient, String msg) {
        String message = String.format("%s: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            if(recipient.equals(c.getNickname())){
                c.sendMessage(message);
                sender.sendMessage(message);
            }
        }

    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }
    public boolean LoginIsAuticated(String login){
        for (ClientHandler c:clients) {
            if (login.equals(c.getLogin())){
                return true;
            }
        }
        return false;
    }
    public void broadcastClientList(){
        StringBuilder sb = new StringBuilder("/clientList");
        for (ClientHandler c:clients) {
            sb.append(" ").append(c.getNickname());
            }
        String message = sb.toString();
        for (ClientHandler c:clients) {
           c.sendMessage(message);
        }
    }
}


