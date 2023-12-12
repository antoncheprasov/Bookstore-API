package tests;

import dto.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateTokenTest extends BaseTest {

    @Test
    public void generateToken() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);
        // здесь отправляем запрос на генерацию токена
        Response generatedToken = postRequest("/Account/v1/GenerateToken", 200, requestBody);
        String userToken = generatedToken.jsonPath().getString("token");

        // Добавляем проверку, что токен не является пустым
        assertNotNull(userToken, "Token is not null");
        updateAuthorizationHeader(userToken);
        //здесь нужно извлечь userId из тела ответа и удаляем созданного
        // пользователя и меняем статус код ответа чтобы тесты не валились
        String userId = createdUser.jsonPath().getString("userID");
        deleteRequest("/Account/v1/User/" + userId, 204);
    }

    @Test
    public void generateTokenNegative() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12!")
                .build();

        Response generatedToken = postRequest("/Account/v1/GenerateToken", 400, requestBody);

        // Проверяем, что токен является null в случае отказа
        assertNull(generatedToken.jsonPath().getString("token"), "Token is null");

        // Проверяем структуру ответа
        assertEquals("Failed", generatedToken.jsonPath().getString("status"));
        assertEquals("User authorization failed.", generatedToken.jsonPath().getString("result"));
    }

}

