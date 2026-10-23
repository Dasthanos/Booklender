package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.model.*;
import kg.attractor.java.server.Cookie;
import kg.attractor.java.server.Utils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson46Server extends Lesson45Server{
    private final List<Loan> loans = new ArrayList<>();
    public Lesson46Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/lesson46", this::lesson46Handler);
        registerPost("/borrow", this::borrowBook);
        registerGet("/books", this::booksHandler);
        registerPost("/return", this::returnBook);
    }

    private void returnBook(HttpExchange exchange) {
        Employee employee = getCurrentEmployee(exchange);

        if (employee == null) {
            renderTemplate(exchange, "login.html",
                    Map.of("error", "Сначала войдите в систему"));
            return;
        }

        Map<String, String> formData =
                Utils.parseUrlEncoded(getBody(exchange), "&");

        int bookId;
        try {
            bookId = Integer.parseInt(formData.get("bookId"));
        } catch (NumberFormatException e) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "Некорректный номер книги"));
            return;
        }

        Loan activeLoan = loans.stream()
                .filter(loan ->
                        loan.getBookId() == bookId
                                && loan.getEmployeeId() == employee.getId()
                                && loan.isActive())
                .findFirst()
                .orElse(null);

        if (activeLoan == null) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "У вас нет этой книги для возврата"));
            return;
        }

        activeLoan.markReturned(LocalDate.now());

        renderTemplate(exchange, "register_result.html",
                Map.of("message", "Книга возвращена"));
    }

    private void booksHandler(HttpExchange exchange) {
        renderTemplate(exchange, "books.html",createBookListDataModel());
    }

    private BookListDataModel createBookListDataModel() {
        List<BookListItem> items = new ArrayList<>();

        for(Book book : getBooks()){
            Loan activeLoan = loans.stream()
                    .filter(loan ->
                            loan.getBookId() == book.getId() && loan.isActive())
                    .findFirst()
                    .orElse(null);

            Employee currentHolder = activeLoan == null
                    ? null
                    : getEmployeeById(activeLoan.getEmployeeId());

            items.add(new BookListItem(book, currentHolder));
        }
        return new BookListDataModel(items);
    }

    private void borrowBook(HttpExchange exchange) {
        Employee employee = getCurrentEmployee(exchange);
        if (employee==null){
            renderTemplate(exchange, "login.html",
                    Map.of("error", "Сначала войдите в систему")
            );
            return;
        }
        Map<String, String> formData =
                Utils.parseUrlEncoded(getBody(exchange), "&");

        int bookId;
        try {
            bookId = Integer.parseInt(formData.get("bookId"));
        } catch (NumberFormatException e) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "Некорректный номер книги"));
            return;
        }

        boolean bookExists = getBooks().stream()
                .anyMatch(book -> book.getId() == bookId);

        if (!bookExists) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "Книга не найдена"));
            return;
        }
        long activeLoans = loans.stream()
                .filter(loan ->
                        loan.getEmployeeId() == employee.getId()
                                && loan.isActive())
                .count();

        if (activeLoans >= 2) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "Нельзя взять больше двух книг одновременно"));
            return;
        }

        boolean bookAlreadyBorrowed = loans.stream()
                .anyMatch(loan ->
                        loan.getBookId() == bookId
                                && loan.isActive());

        if (bookAlreadyBorrowed) {
            renderTemplate(exchange, "register_result.html",
                    Map.of("message", "Эта книга уже выдана"));
            return;
        }
        Loan loan = new Loan(bookId, employee.getId(), LocalDate.now());
        loans.add(loan);

        renderTemplate(exchange, "register_result.html",
                Map.of("message", "Книга успешно выдана"));
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
