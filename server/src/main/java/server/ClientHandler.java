package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private String login;


    public ClientHandler (Server server, Socket socket){
        try {
            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {

                    socket.setSoTimeout(120000);

                    while (true) {

                        String clientMessage = in.readUTF();
                        //при отправке "/end" закрываем соединение
                        if (clientMessage.equals("/end")) {
                            out.writeUTF("/end");
                            throw new RuntimeException("Клиент рещил выйти");
                        }
                            // Аутентификация
                        if (clientMessage.startsWith("/auth")) {

                            String[] token = clientMessage.split("\\s+",3);
                            if(token.length<3){
                                continue;
                            }

                            String newNick = server
                                    .getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            if (newNick != null) {
                                login = token[1];
                                if (!server.LoginIsAuticated(login)) {
                                    nickname = newNick;
                                    sendMessage("/auth_okay " + nickname);
                                    socket.setSoTimeout(0);
                                    server.subscribe(this);
                                    System.out.println("Клиент аутентифицировался. Никнейм " + nickname +
                                            " Адрес: " + socket.getRemoteSocketAddress());
                                    break;
                                } else {
                                    sendMessage("С таким логином уже зашли" + "\n");
                                }

                            } else {
                                sendMessage("Неверный логин или пароль" + "\n");
                            }

                        }


                        //Регистрация
                        if (clientMessage.startsWith("/reg")) {
                            String[] token = clientMessage.split("\\s+", 4);
                            if(token.length<4){
                                continue;
                            }
                            boolean b = server.getAuthService()
                                    .registration(token[1],token[2],token[3]);
                            if (b){
                                sendMessage("/reg_ok");
                            }else{
                                sendMessage("/reg_no");
                            }
                        }
                    }


                    while (true) {


                        String clientMessage = in.readUTF();


                        if (clientMessage.equals("/end")) {
                            out.writeUTF("/end");
                            break;
                        }
                        if (clientMessage.startsWith("/w")) {
                            String[] token = clientMessage.split("\\s+", 3);
                            server.PrivateMessage(this, token[1], token[2]);
                        } else {
                            server.broadcastMessage(this, clientMessage);
                        }


                    }
                }catch (SocketTimeoutException e){
                    try {
                        out.writeUTF("/end");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }catch (RuntimeException e){
                    System.out.println(e.getMessage());
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {

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

    public String getLogin() {
        return login;
    }
}