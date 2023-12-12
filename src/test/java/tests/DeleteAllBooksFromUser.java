package tests;

import dto.User;
import dto.ValidAddListOfBookRequest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.amazonaws.util.ValidationUtils.assertNotEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteAllBooksFromUser extends BaseTest {

    @Test
    public void deleteBooksFromCollection() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);

        //здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");

        // здесь отправляем запрос на генерацию токена
        Response generatedToken = postRequest("/Account/v1/GenerateToken", 200, requestBody);
        String userToken = generatedToken.jsonPath().getString("token");
        assertEquals(200, generatedToken.getStatusCode());

        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        updateAuthorizationHeader(userToken);

        // Выполняем GET-запрос для получения информации о пользователе
        Response getUserInfo = getRequest("/Account/v1/User/" + userId, 200);

        // Проверяем структуру ответа
        assertEquals(userId, getUserInfo.jsonPath().getString("userId"));
        assertEquals(fakeUserName, getUserInfo.jsonPath().getString("username"));
        updateAuthorizationHeader(userToken);

        // Сюда по хорошему нужно добавить метод добавляющий книги в коллекцию пользователя
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

        // Выполняем снова GET-запрос для получения информации о пользователе
        Response getUserInfoAgain = getRequest("/Account/v1/User/" + userId, 200);

        // Проверяем структуру ответа
        assertEquals(userId, getUserInfoAgain.jsonPath().getString("userId"));
        assertEquals(fakeUserName, getUserInfoAgain.jsonPath().getString("username"));

        // Проверяем наличие книг в ответе
        List<Map<String, Object>> books = getUserInfoAgain.jsonPath().getList("books");

        // И теперь делаем запрос на удаление книг из коллекции пользователя
        Response deleteAllBooks = deleteRequestForBooks("/BookStore/v1/Books", 204, "UserId", userId);

        // Выполняем снова GET-запрос для получения информации о пользователе после того как удалили книги
        Response getUserInfoAfterDeletingBooks = getRequest("/Account/v1/User/" + userId, 200);

        // Проверяем структуру ответа
        assertEquals(userId, getUserInfoAfterDeletingBooks.jsonPath().getString("userId"));
        assertEquals(fakeUserName, getUserInfoAfterDeletingBooks.jsonPath().getString("username"));
        // Проверяем, что коллекция книг теперь пуста
        List<Map<String, Object>> booksAfterDelete = getUserInfoAfterDeletingBooks.jsonPath().getList("books");
        assertTrue(booksAfterDelete.isEmpty(), "User's collection is not empty!");

        //здесь удаляем созданного пользователя и меняем статус код ответа чтобы тесты не валились
        deleteRequest("/Account/v1/User/" + userId, 204);
    }
}
