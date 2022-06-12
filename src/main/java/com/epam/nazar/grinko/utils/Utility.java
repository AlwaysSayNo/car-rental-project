package com.epam.nazar.grinko.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.util.Locale;

public class Utility {
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getRole(String role){
        return role.toLowerCase(Locale.ROOT).replace("role_", "");
    }

}
