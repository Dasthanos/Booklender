package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.server.Cookie;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson46Server extends Lesson45Server{
    public Lesson46Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/lesson46", this::lesson46Handler);
    }

    private void lesson46Handler(HttpExchange exchange) {
        // скоро тут будут печеньки :)

        Cookie sessionCookie = Cookie.make("userId", "123");
        exchange.getResponseHeaders()
                .add("Set-Cookie", sessionCookie.toString());
        // ну а что бы сервер не зависал, отдадим страничку
        Map<String, Object> data = new HashMap<>();
        int times = 42;
        data.put("times", times);

        Cookie c1 = Cookie.make("user%Id", "456");
        setCookie(exchange, c1);
        Cookie c2 = Cookie.make("user-mail", "example@mail");
        setCookie(exchange, c2);
        Cookie c3 = Cookie.make("restricted()<>@,;:\\\"/[]?={}", "()<>@,;:\\\"/[]?={}");
        setCookie(exchange, c3);
        String cookieString = getCookies(exchange);
        Map<String, String> cookies = Cookie.parse(cookieString);
        data.put("cookies", cookies);

        renderTemplate(exchange, "cookie.html", data);
    }

    protected static String getCookies(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Cookie", List.of(""))
                .get(0);
    }
    protected void setCookie(HttpExchange exchange, Cookie cookie) {
        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }
}
