package tests;
import dto.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class DeleteUserTest extends BaseTest{

    @Test
    public void successfulDeleteUser() {
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
        // Проверка авторизации
        updateAuthorizationHeader(userToken);

        //здесь удаляем созданного пользователя
        deleteRequest("/Account/v1/User/" + userId, 200);
    }

    @Test
    public void deleteUserWithoutAuthorization() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest("/Account/v1/User", 201, requestBody);

        //здесь нужно извлечь userId из тела ответа
        String userId = createdUser.jsonPath().getString("userID");

        //здесь удаляем созданного пользователя
        deleteRequest("/Account/v1/User/" + userId, 204);
    }
}


