package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;


    public ClientHandler (Server server, Socket socket){
        try {
            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {

                    while (true) {

                        String clientMessage = in.readUTF();
                        //при отправке "/end" закрываем соединение
                        if (clientMessage.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }

                        if (clientMessage.startsWith("/auth")){

                            String [] token = clientMessage.split("\\s+");
                            String newNick = server
                                    .getAuthService()
                                    .getNicknameByLoginAndPassword(token[1],token[2] );
                            if (newNick != null){
                                nickname = newNick;
                                sendMessage("/auth_okay " + nickname);
                                server.subscribe(this);
                                System.out.println("Клиент аутентифицировался. Никнейм "+ nickname +
                                        " Адрес: " + socket.getRemoteSocketAddress());
                                break;
                            }else{
                                sendMessage("Неверный логин или пароль" +"\n");
                            }
                        }
                    }


                    while (true) {


                        String clientMessage = in.readUTF();


                        if (clientMessage.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }

                        server.broadcastMessage(this, clientMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    server.unsubscribe(this);
                    System.out.println("Client " +socket.getRemoteSocketAddress() + " disconnect");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage (String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}