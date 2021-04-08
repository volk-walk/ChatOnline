package client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public TextField loginField;
    @FXML
    public TextField passwordField;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox messagePanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 8189;
    private boolean authenticated;
    private String nickname;
    private Stage stage;

    
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);


        if (!authenticated){
            nickname = "";
        }
        setTitle(nickname);
        textArea.clear();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(()->{
            stage = (Stage) textArea.getScene().getWindow();
        });
        setAuthenticated(false);
    }

    private void connect(){
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());


            new Thread(() -> {
                try {

                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")){

                            if (str.equals("/end")) {
                                System.out.println("disconnect");
                                break;
                            }

                            if (str.startsWith("/auth_okay")){
                                nickname = str.split("\\s+")[1];
                                setAuthenticated(true);
                                break;
                            }
                        }else {
                            textArea.appendText(str);
                        }

                    }

                    //цикл работы окна отправки сообщений
                    while (authenticated) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            System.out.println("disconnect");
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    setAuthenticated(false);
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
    //метод отправки сообщений
    @FXML
    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void logIn(ActionEvent actionEvent) {

        if (socket == null || socket.isClosed()){
            connect();
        }

        String msg = String.format("/auth %s %s",
                loginField.getText().trim(), passwordField.getText().trim());//trim игнорирует меножество пробелов, считая их как один
        try {
            out.writeUTF(msg);
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(String nickname){
        Platform.runLater(()->{
            if (nickname.equals("")){
                stage.setTitle("OnlyChats");
            }else {
                stage.setTitle(String.format("OnlyChats <[%s]>",nickname));
            }

        });
    }
}