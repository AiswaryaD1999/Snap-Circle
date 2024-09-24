package com.example.snapcircle;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicationShutdown {

    private final HttpServletResponse response;

    public ApplicationShutdown(HttpServletResponse response) {
        this.response = response;
    }

    @PreDestroy
    public void cleanUp() {
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        System.out.println("Cookies cleared on application shutdown.");
    }
}
