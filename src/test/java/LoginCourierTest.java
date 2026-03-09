import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    private static String testLogin = "Katya";
    private static String testPassword = "1234";
    private static String testFirstName = "March";
    private final String testLoginFail = "Kata";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        //Создаем курьера
        NewCourier courier = new NewCourier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @AfterAll
    public static void tearDown() {
        //Узнаем id курьера
        LoginCourier courier = new LoginCourier(testLogin, testPassword);
        var response = given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");

        if (response.statusCode() == 200) {
            String id = response.then().extract().path("id").toString();

            //Удаляем курьера
            given()
                    .pathParam("id", id)
                    .when()
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(200);
        }
    }

    @DisplayName("Проверка авторизации курьера")
    @Test
    public void loginCourierTest() {
        loginCourier();
        loginErrorCourier();
    }

    @DisplayName("Проверка ввода обязательных полей")
    @ParameterizedTest
    @CsvSource({
            "Katya, ''",
            " , 1234"
    })
    public void loginCourierError(String testLogin, String testPassword) {
        loginCourierOneBox(testLogin, testPassword);
    }


    @Step("Авторизация курьера - ожидание 200")
    public void loginCourier() {
        LoginCourier courier = new LoginCourier(testLogin, testPassword);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().ifError()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Проверка всех обязательные полей - ожидание 400")
    void loginCourierOneBox(String testLogin, String testPassword) {
        LoginCourier courier = new LoginCourier(testLogin, testPassword);
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().ifError()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Заполнение с неверными данными - ожидается 404")
    void loginErrorCourier() {
        LoginCourier courier = new LoginCourier(testLoginFail, testPassword);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().ifError()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

}
