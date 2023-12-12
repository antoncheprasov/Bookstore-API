package tests;

import dto.User;
import dto.ValidAddListOfBookRequest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.amazonaws.util.ValidationUtils.assertNotEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteBookByISBN extends BaseTest{


    @Test
    public void deleteBookFromUserByISBN() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);
        assertEquals(201, createdUser.getStatusCode());
        //здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");
        // здесь отправляем запрос на генерацию токена
        Response generatedToken = postRequest("/Account/v1/GenerateToken", 200, requestBody);
        String userToken = generatedToken.jsonPath().getString("token");
        assertEquals(200, generatedToken.getStatusCode());

        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        assertEquals(200, isAuthorized.getStatusCode());
        updateAuthorizationHeader(userToken);

        Response getAllBookInformation = getRequest("/BookStore/v1/Books", 200);

        // Получаем ISBN из ответа и сохраняем их в списке
        List<ValidAddListOfBookRequest.BookItem> isbnList = createBookItemListFromResponse(getAllBookInformation);

        // Создаем запрос на добавление коллекции книг
        ValidAddListOfBookRequest addListOfBookRequest = ValidAddListOfBookRequest.builder()
                .userId(userId)
                .collectionOfIsbns(isbnList)
                .build();

        // Отправляем запрос на добавление коллекции книг
        Response addListOfBooksResponse = postRequest("/BookStore/v1/Books", 201, addListOfBookRequest);

        // Проверка содержимого тела ответа
        JsonPath jsonPath = addListOfBooksResponse.jsonPath();
        List<String> isbnListInResponse = jsonPath.getList("books.isbn");

        // Использую assertNotEmpty, чтобы проверить, что список ISBN в теле ответа не пустой
        assertNotEmpty(isbnListInResponse, "List of ISBNs in the response should not be empty");
        updateAuthorizationHeader(userToken);

        String body = String.format("{\"isbn\": \"9781449331818\", \"userId\": \"%s\"}", userId);
        Response deleteResponse = deleteRequestByISBN("/BookStore/v1/Book", 204, body);

        // Проверка, что запрос на удаление успешен
        assertEquals(204, deleteResponse.getStatusCode(), "Unexpected status code for book deletion");

        // Проверка, что книга была успешно удалена
        Response getUserInfo = getRequest("/Account/v1/User/" + userId, 200);

        // Проверяем наличие книг в ответе
        List<Map<String, Object>> books = getUserInfo.jsonPath().getList("books.isbn");

        // Проверка отсутствия удаленной книги в коллекции пользователя
        assertFalse(books.contains("9781449331818"),
                "Book with this ISBN should not be in user's collection!");

        //здесь удаляем созданного пользователя
        deleteRequest("/Account/v1/User/" + userId, 204);
    }

    @Test
    public void deleteBookFromUserByWrongISBN() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);
        assertEquals(201, createdUser.getStatusCode());
        //здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");
        // здесь отправляем запрос на генерацию токена
        Response generatedToken = postRequest("/Account/v1/GenerateToken", 200, requestBody);
        String userToken = generatedToken.jsonPath().getString("token");
        assertEquals(200, generatedToken.getStatusCode());

        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        assertEquals(200, isAuthorized.getStatusCode());
        updateAuthorizationHeader(userToken);

        Response getAllBookInformation = getRequest("/BookStore/v1/Books", 200);

        // Получаем ISBN из ответа и сохраняем их в списке
        List<ValidAddListOfBookRequest.BookItem> isbnList = createBookItemListFromResponse(getAllBookInformation);

        // Создаем запрос на добавление коллекции книг
        ValidAddListOfBookRequest addListOfBookRequest = ValidAddListOfBookRequest.builder()
                .userId(userId)
                .collectionOfIsbns(isbnList)
                .build();

        // Отправляем запрос на добавление коллекции книг
        Response addListOfBooksResponse = postRequest("/BookStore/v1/Books", 201, addListOfBookRequest);

        // Проверка содержимого тела ответа
        JsonPath jsonPath = addListOfBooksResponse.jsonPath();
        List<String> isbnListInResponse = jsonPath.getList("books.isbn");

        // Использую assertNotEmpty, чтобы проверить, что список ISBN в теле ответа не пустой
        assertNotEmpty(isbnListInResponse, "List of ISBNs in the response should not be empty");
        updateAuthorizationHeader(userToken);

        String body = String.format("{\"isbn\": \"978144933818\", \"userId\": \"%s\"}", userId);
        Response deleteResponse = deleteRequestByISBN("/BookStore/v1/Book", 400, body);

        // Проверка, что запрос на удаление успешен
        assertEquals(400, deleteResponse.getStatusCode(), "Unexpected status code for invalid ISBN book deletion");

        //здесь удаляем созданного пользователя
        deleteRequest("/Account/v1/User/" + userId, 204);
    }
}
