package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private String db = "chat-server\\chat.db";
    Connection connection;
    private final List<User> users;

    public InMemoryAuthenticationProvider() {
        this.users = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + db);
        } catch (SQLException ex) {
            throw new RuntimeException(db + " not found" + ex.getMessage());
        }
        withDB(users);
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login)) {
                return user.getUsername();
            }
        }
        return null;
    }

    public void withDB(List<User> user_load) {
        String sql = "SELECT NAME, PASS, NICK FROM USERS";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(rs.getString("NAME"), rs.getString("PASS"), rs.getString("NICK"));
                if (user.getUsername().toLowerCase().equals("admin")) {
                    user.setRole(Roles.ADMIN);
                }
                user_load.add(user);
                System.out.println("load " + user.getUsername() + " role=" + user.getRole());
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Roles getRole(String username) {
        return null;
    }

    @Override
    public synchronized boolean register(String login, String password, String username) {
        for (User user : users) {
            if (Objects.equals(user.getUsername(), username) && Objects.equals(user.getLogin(), login)) {
                return false;
            }
        }
        users.add(new User(login, password, username));
        return true;
    }
}
