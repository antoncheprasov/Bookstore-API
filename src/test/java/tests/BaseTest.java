package tests;

import com.github.javafaker.Faker;
import dto.User;
import dto.ValidAddListOfBookRequest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {

    final static String BASE_URI = "https://demoqa.com";

    Faker faker = new Faker();

    String fakeUserName = faker.name().username();

    private static String token;

    protected List<String> savedISBNList;

    @BeforeEach
    public void setUp() {
        savedISBNList = new ArrayList<>();
    }

    static RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", "Bearer" + token)
            .build();

    static RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType(ContentType.JSON)
            .build();

    protected List<ValidAddListOfBookRequest.BookItem> createBookItemListFromResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();
        List<ValidAddListOfBookRequest.BookItem> bookItemList = jsonPath.getList("books", ValidAddListOfBookRequest.BookItem.class);

        return bookItemList;
    }


    public static Response getRequest(String endPoint, Integer responseCode) {
        Response response = RestAssured.given()
                .spec(specification)
                .when()
                .log().all()
                .get(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public void updateAuthorizationHeader(String token) {
        specification = new RequestSpecBuilder()
                .setUrlEncodingEnabled(false)
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    public Response getRequestWithQueryParam(String endPoint, Integer responseCode, String paramName, Object paramValue) {
        Response response = RestAssured.given()
                .spec(specificationWithoutToken)
                .when()
                .queryParam(paramName, paramValue)
                .log().all()
                .get(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public Response postRequest(String endPoint, Integer responseCode, Object body) {
        Response response = RestAssured.given()
                .spec(specification)
                .body(body)
                .when()
                .log().all()
                .post(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public Response putRequest(String endPoint, Integer responseCode, Object body) {
        Response response = RestAssured.given()
                .spec(specification)
                .body(body)
                .when()
                .log().all()
                .put(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public Response deleteRequest(String endPoint, Integer responseCode) {
        Response response = RestAssured.given()
                .spec(specification)
                .when()
                .log().all()
                .delete(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public Response deleteRequestForBooks(String endPoint, Integer responseCode, String paramName, String paramValue) {
        Response response = RestAssured.given()
                .spec(specification)
                .when()
                .queryParam(paramName, paramValue)  // Добавляем query-параметр
                .log().all()
                .delete(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

    public Response deleteRequestByISBN(String endPoint, int responseCode, String body) {
        Response response = RestAssured.given()
                .spec(specification)
                .when()
                .body(body)
                .log().all()
                .delete(endPoint)
                .then().log().all()
                .extract().response();
        response.then().assertThat().statusCode(responseCode);
        return response;
    }

}

