package com.epam.nazar.grinko.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public static <V, T> void safetyAdd(Map<V, List<T>> map, V key, T value){
        if(key != null) {
            List<T> values;
            if (map.containsKey(key)) values = new ArrayList<>(map.get(key));
            else values = new ArrayList<>();

            values.add(value);
                map.put(key, values);
        }
    }

}
