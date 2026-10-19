package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.gson.EmployeeStorage;
import kg.attractor.java.model.Employee;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.server.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Lesson45Server extends Lesson44Server{

    private final EmployeeStorage employeeStorage = new EmployeeStorage();
    private final List<Employee> employees = employeeStorage.load();

    public Lesson45Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/login", this::loginGet);
        registerPost("/login", this::loginPost);
        registerGet("/register", this::registerPage);
        registerPost("/register", this::registerEmployee);
        registerGet("/profile", this::profileGet);
    }

    private void profileGet(HttpExchange exchange) {
        Employee employee = new Employee(
                0,
                "Некий пользователь",
                "unknown@example.com",
                ""
        );

        renderTemplate(
                exchange,
                "profile.html",
                Map.of("employee", employee)
        );
    }

    private void registerEmployee(HttpExchange exchange) {
        String data = getBody(exchange);
        var formData = Utils.parseUrlEncoded(data, "&");
        String email = formData.get("email");
        String fullName = formData.get("fullName");
        String password = formData.get("password");

        boolean alreadyRegistered = employees.stream()
                .anyMatch(employee ->
                        employee.getEmail() != null
                                && employee.getEmail().equalsIgnoreCase(email));

        if (alreadyRegistered) {
            renderTemplate(
                    exchange,
                    "register_result.html",
                    Map.of("message", "Такой email уже зарегистрирован. Попробуйте другой.")
            );
            return;
        }

        Employee employee = new Employee(
                employees.size() + 1,
                fullName,
                email,
                password
        );

        employees.add(employee);
        employeeStorage.save(employees);
        renderTemplate(
                exchange,
                "register_result.html",
                Map.of("message", "Регистрация прошла успешно!")
        );
        System.out.println("Зарегистрирован: " + email);


    }

    private void registerPage(HttpExchange exchange){
        renderTemplate(exchange, "register.html", Map.of());

    }

    private void loginGet(HttpExchange exchange) {
        renderTemplate(exchange, "login.html", Map.of());
    }

    private void loginPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> formData = Utils.parseUrlEncoded(raw, "&");

        String email = formData.get("email");
        String password = formData.get("user-password");

        Employee matchedEmployee = employees.stream()
                .filter(employee ->
                        email != null
                                && password != null
                                && email.equalsIgnoreCase(employee.getEmail())
                                && password.equals(employee.getPassword()))
                .findFirst()
                .orElse(null);

        if (matchedEmployee == null) {
            renderTemplate(
                    exchange,
                    "login.html",
                    Map.of("error", "Пользователь не существует или пароль неверный")
            );
            return;
        }

        renderTemplate(
                exchange,
                "profile.html",
                Map.of("employee", matchedEmployee)
        );
    }

    public static String getContentType(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Content-Type", List.of(""))
                .get(0);
    }

    protected String getBody(HttpExchange exchange) {

        InputStream input = exchange.getRequestBody();

        Charset utf8 = StandardCharsets.UTF_8;

        InputStreamReader isr = new InputStreamReader(input, utf8);

        // сейчас мы предполагаем, что клиент

        // отправляет текстовые данные

        try (BufferedReader reader = new BufferedReader(isr)) {

            return reader.lines().collect(Collectors.joining(""));

        } catch (IOException e) {

            e.printStackTrace();

        }

        return "";

    }
}
