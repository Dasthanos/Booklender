package kg.attractor.java.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kg.attractor.java.model.Employee;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeStorage {
    private final Path filePath = Path.of("data", "employees.json");
    private final Gson gson = new Gson();

    private final Type employeeListType = new TypeToken<List<Employee>>(){}.getType();

    public List<Employee> load() {
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            List<Employee> employees = gson.fromJson(reader, employeeListType);

            if (employees == null) {
                return new ArrayList<>();
            }

            return employees;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить сотрудников", e);
        }
    }

    public void save(List<Employee> employees) {
        try (var writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            gson.toJson(employees, writer);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить сотрудников", e);
        }
    }
}
