package org.example;

public interface AuthenticationProvider {
    String getUsernameByLoginAndPassword(String login, String password);
    Roles getRole(String username);
    boolean register(String login, String password, String username);
}