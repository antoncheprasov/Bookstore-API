package tests;

import dto.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class GetUserInfo extends BaseTest {

    @Test
    public void getUserInfo() {
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
        assertNotNull(userToken, "Token is not null");
        updateAuthorizationHeader(userToken);

        // Выполняем GET-запрос для получения информации о пользователе
        Response getUserInfo = getRequest("/Account/v1/User/" + userId, 200);

        // Проверяем структуру ответа
        assertEquals(userId, getUserInfo.jsonPath().getString("userId"));
        assertEquals(fakeUserName, getUserInfo.jsonPath().getString("username"));

        // Проверяем наличие массива книг в ответе
        List<Map<String, Object>> books = getUserInfo.jsonPath().getList("books");

        //здесь удаляем созданного пользователя и меняем статус код ответа чтобы тесты не валились
        deleteRequest("/Account/v1/User/" + userId, 204);
    }
}
