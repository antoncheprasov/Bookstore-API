package tests;

import dto.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Authorization extends BaseTest{

    @Test
    public void isUserAuthorized() {
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
        assertNotNull(userToken, "Token is not null");
        updateAuthorizationHeader(userToken);
        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        // Проверяем, что в теле ответа приходит значение "true"
        assertTrue(isAuthorized.getBody().asString().equalsIgnoreCase("true"), "User is authorized");

        //здесь удаляем созданного пользователя и меняем статус код ответа чтобы тесты не валились
        deleteRequest("/Account/v1/User/" + userId, 204);
    }

    @Test
    public void isUserAuthorizedWithoutToken() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);
        //здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");

        Response isAuthorized = postRequest("/Account/v1/Authorized", 200, requestBody);
        // Проверяем, что в теле ответа приходит значение "false"
        assertTrue(isAuthorized.getBody().asString().equalsIgnoreCase("false"), "Something went wrong!");
    }
}
