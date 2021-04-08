package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {

    private  class User {
        String login;
        String password;
        String nickname;

        //конструктор юзеров
        public User (String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;

        }
    }

    //создаем List юзеров (вложенного класса)
    private List<User> users;

    //добавляем в этом методе Юзеров в наш List
    public SimpleAuthService() {
        users = new ArrayList<>();
        users.add(new User("111", "111", "111"));
        users.add(new User("222", "222", "222"));
        users.add(new User("333", "333", "333"));

    }


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        for (User u:users) {
            if(u.login.equals(login) && u.password.equals(password)){
                return u.nickname;
            }
        }
        return null;
    }
}