package tests;

import dto.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CreateUserTest extends BaseTest{
    String endpoint = "/Account/v1/User";

    @Test
    public void createUserWithValidCredentials() {
   
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12Qwerty!")
                .build();

        Response createdUser = postRequest(endpoint, 201, requestBody);
        assertEquals(201, createdUser.getStatusCode());
    }

    @Test
    public void createUserWithInvalidPassword() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("12")
                .build();

        Response createdUser = postRequest(endpoint, 404, requestBody);
        assertEquals(404, createdUser.getStatusCode());
    }

    @Test
    public void createUserWithEmptyPassword() {
        User requestBody = User.builder()
                .userName(fakeUserName)
                .password("")
                .build();

        Response createdUser = postRequest(endpoint, 404, requestBody);
        assertEquals(404, createdUser.getStatusCode());
    }

    @Test
    public void createUserWithEmptyCredentials() {
        User requestBody = User.builder()
                .userName("")
                .password("")
                .build();

        Response createdUser = postRequest(endpoint, 404, requestBody);
        assertEquals(404, createdUser.getStatusCode());
    }
}
