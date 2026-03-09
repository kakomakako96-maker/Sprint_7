import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.example.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.then;

public class CourierTest {

    private final String testLogin = "Katya";
    private final String testPassword = "1234";
    private final String testFirstName = "March";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @AfterEach
    void tearDown() {
        //Узнаем id курьера
        String json = given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"" + testLogin + "\", \"password\": \"" + testPassword + "\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        //Оставляем только номер
        String id = json.replaceAll("[^\\d]", "");

        //Удаляем курьера
        given()
                .pathParams("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(200);
    }

    @Description("Создаем курьера")
    @Test
    void createCourier() {
        Courier courier = new Courier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    @Test
    void createTwoCourier() {
        Courier courierOne = new Courier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courierOne)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Courier courierTwo = new Courier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courierTwo)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Description("чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @Test
    void createCourierOneBox() {

        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"" + testLogin + "\"}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

}
