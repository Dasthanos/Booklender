package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.model.*;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();
    private final Employee employee = new Employee(1, "Иван Петров");
    private final Book cleanCode = new Book(1, "Чистый код", "Роберт Мартин");
    private final Book effectiveJava =
            new Book(2, "Эффективная Java", "Джошуа Блох");

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/sample", this::freemarkerSampleHandler);
        registerGet("/books", this::freemarkerBooksHandler);
        registerGet("/book", this::freemarkerBookHandler);
        registerGet("/employee", this::freemarkerEmployeeHandler);
    }

    protected List<Book> getBooks() {
        return List.of(cleanCode, effectiveJava);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void freemarkerBookHandler(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery(); // id=1

        if (query == null || !query.startsWith("id=")) {
            sendBookNotFound(exchange);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(query.substring(3));
        } catch (NumberFormatException e) {
            sendBookNotFound(exchange);
            return;
        }
        BookListItem selectedItem = getBookListDataModel().getBooks().stream()
                .filter(item -> item.getBook().getId() == id)
                .findFirst()
                .orElse(null);

        if (selectedItem == null) {
            sendBookNotFound(exchange);
            return;
        }

        renderTemplate(exchange, "book.html", selectedItem);
    }

    private void sendBookNotFound(HttpExchange exchange) {
        try {
            sendByteData(
                    exchange,
                    ResponseCodes.NOT_FOUND,
                    ContentType.TEXT_PLAIN,
                    "Книга не найдена".getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void freemarkerEmployeeHandler(HttpExchange exchange) {
        renderTemplate(exchange, "employee.html", getEmployeePageDataModel());
    }

    private void freemarkerBooksHandler(HttpExchange exchange){
        renderTemplate(exchange, "books.html", getBookListDataModel());
    }

    private void freemarkerSampleHandler(HttpExchange exchange) {
        renderTemplate(exchange, "sample.html", getSampleDataModel());
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            // Загружаем шаблон из файла по имени.
            // Шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток, который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя шаблон данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private SampleDataModel getSampleDataModel() {
        // возвращаем экземпляр тестовой модели-данных
        // которую freemarker будет использовать для наполнения шаблона
        return new SampleDataModel();
    }

    private EmployeePageDataModel getEmployeePageDataModel() {
        List<Book> currentBooks = List.of(cleanCode);
        List<Book> pastBooks = List.of(effectiveJava);

        return new EmployeePageDataModel(employee, currentBooks, pastBooks);
    }

    private BookListDataModel getBookListDataModel() {
        return new BookListDataModel(List.of(
                new BookListItem(cleanCode, employee),
                new BookListItem(effectiveJava, null)
        ));
    }
}
