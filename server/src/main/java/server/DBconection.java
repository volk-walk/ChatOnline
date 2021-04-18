package server;

import java.sql.*;

public class DBconection implements AuthService {
    private static Connection connection;
    private static Statement stmt;



    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    private static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            connect();
            ResultSet rs = stmt.executeQuery("SELECT login,password,nickName FROM students");

            while (rs.next()) {
                if (rs.getString("login").equals(login) && rs.getString("password").equals(password)) {
                    return rs.getString("nickName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return null;
    }

    @Override
    public boolean changeNick(String nickname ,String login) {
        try {
            connect();
            ResultSet rs = stmt.executeQuery("SELECT login,nickName FROM students");
            String str = String.format("UPDATE students SET nickName = '%s' WHERE login = '%s'",nickname,login);
            while (rs.next()){
                if(rs.getString("nickName").equals(nickname)){
                    return false;
                }
                stmt.executeUpdate(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            connect();
            ResultSet rs = stmt.executeQuery("SELECT login,password,nickName FROM students");
            String str = String.format("INSERT INTO students (login,password,nickName) VALUES ('%s','%s','%s')",login,password,nickname);
            while (rs.next()) {
                if (rs.getString("login").equals(login) || rs.getString("nickName").equals(nickname)) {
                    return false;
                }
                stmt.executeUpdate(str);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            disconnect();
        }
        return true;
    }
}
