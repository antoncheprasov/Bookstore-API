package tests;

import dto.User;
import dto.ValidAddListOfBookRequest;
import dto.ValidBookRequest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.amazonaws.util.ValidationUtils.assertNotEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class AddListOfBooks extends BaseTest {

    @Test
    public void successAddListOfBooks() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);

        // здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");
        // здесь отправляем запрос на генерацию токена
        Response generatedToken = postRequest("/Account/v1/GenerateToken", 200, requestBody);
        String userToken = generatedToken.jsonPath().getString("token");

        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        updateAuthorizationHeader(userToken);

        // Отпарвляем запрос на получение информации о книгах
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

        // Здесь удаляем созданного пользователя и меняем статус код ответа чтобы тесты не валились
        deleteRequest("/Account/v1/User/" + userId, 204);
    }
}
