package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Regcontroller {
    private Controller controller;

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nicknameField;
    @FXML
    private TextArea textArea;

    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();

        controller.registration(login,password,nickname);

    }

    public void showResult(String result){
        if(result.equals("/reg_ok")){
            textArea.appendText("Регистрация прошла успешно.\n");
        }else{
            textArea.appendText("Регистрация не удалась.\n");
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
