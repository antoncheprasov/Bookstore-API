package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetBookByISBN extends BaseTest {

    private static final String GitPocketGuide = "9781449325862";
    private static final String LearningJavaScriptDesignPatterns = "9781449331818";
    private static final String DesigningEvolvableWebAPIsWithASPNET = "9781449337711";
    private static final String SpeakingJavaScript = "9781449365035";
    private static final String YouDontKnowJS = "9781491904244";
    private static final String ProgrammingJavaScriptApplications = "9781491950296";
    private static final String EloquentJavaScriptSecondEdition = "9781593275846";
    private static final String UnderstandingECMAScript6 = "9781593277574";
    private static final String falseBookISBN = "1234567890";


    @Test
    public void getBookInfoByISBN() {
        given().baseUri("https://demoqa.com")
                .when().log().all();

        // кладем в запрос переменную GitPocketGuide
        Response response = getRequestWithQueryParam("/BookStore/v1/Book",
                200, "ISBN", GitPocketGuide);

        // Проверяем структуру ответа
        JsonPath jsonPath = response.jsonPath();

        // Проверяем, что в ответе есть поле "title" со значением "Git Pocket Guide"
        assertEquals("Git Pocket Guide", jsonPath.getString("title"));
    }



    // тест на проверку ответа при запросе несуществующего ISBN, статус код ответа д.б. 400 Bad Request
    @Test
    public void getBookInfoByFalseISBN() {
        given().baseUri("https://demoqa.com")
                .when().log().all();

        Response response = getRequestWithQueryParam("/BookStore/v1/Book",
                400, "ISBN", falseBookISBN);

        // Проверяем структуру ответа
        JsonPath jsonPath = response.jsonPath();

        // Проверяем, что в ответе есть поле "message" со описанием ошибки
        assertEquals("ISBN supplied is not available in Books Collection!", jsonPath.getString("message"));

    }
}
