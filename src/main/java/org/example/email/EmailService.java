package org.example.email;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.example.user.User;

@Slf4j
@ApplicationScoped
public class EmailService {

    private static final String URL = "http://localhost:8080/user-storage/rest/users/activate?token=%s";

    public void sendConfirmationEmail(User user) {
        log.info(String.format(URL, user.getToken())); //TODO: implement actual email client
    }
}
