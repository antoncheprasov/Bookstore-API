package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class GetAllBooksInfo extends BaseTest{
    @Test
    public void getBooksInfo() {

        given().baseUri("https://demoqa.com")
                .when().log().all();
        Response response = getRequest("/BookStore/v1/Books", 200);

        // Проверяем структуру ответа
        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> books = jsonPath.getList("books");

        assertNotNull(books, "The list of books should not be null");
        assertFalse(books.isEmpty(), "The list of books should not be empty");
        // млжно проверить что в ответе есть ровно восемь книг
        assertEquals(8, books.size());
    }

    @Test
    public void askingForBooksInfoWrongURL() {

        given().baseUri("https://demoqa.com")
                .when().log().all();
        Response response = getRequest("/BookStore/v1/AllBooks", 404);

        assertEquals(404, response.getStatusCode());
    }

}
