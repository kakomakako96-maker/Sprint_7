import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    private static String testLogin;
    private static String testPassword;
    private static String testFirstName;
    private static String testLoginFail;

    @BeforeEach
    public void setUp() {
        testLogin = String.valueOf(UUID.randomUUID());
        testPassword = String.valueOf(UUID.randomUUID());
        testFirstName = "FirstName " + testLogin;
        testLoginFail = String.valueOf(UUID.randomUUID());
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
        System.out.println("Sozdan");
    }

    @AfterEach
    public void tearDown() {
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
        loginCourier()
                .then()
                .log().ifError()
                .statusCode(200)
                .body("id", notNullValue());

        loginErrorCourier()
                .then()
                .log().ifError()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    private static Stream<Arguments> courierError() {
        testLogin = String.valueOf(UUID.randomUUID());
        testPassword = String.valueOf(UUID.randomUUID());
        return Stream.of(
                Arguments.of(testLogin, ""),
                Arguments.of("", testPassword)
        );
    }

    @DisplayName("Проверка ввода обязательных полей")
    @ParameterizedTest
    @MethodSource("courierError")
    public void loginCourierError(String testLogin, String testPassword) {
        loginCourierOneBox(testLogin, testPassword)
                .then()
                .log().ifError()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }


    @Step("Авторизация курьера - ожидание 200")
    public Response loginCourier() {
        LoginCourier courier = new LoginCourier(testLogin, testPassword);
        return given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверка всех обязательные полей - ожидание 400")
    public Response loginCourierOneBox(String testLogin, String testPassword) {
        LoginCourier courier = new LoginCourier(testLogin, testPassword);
        return given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Заполнение с неверными данными - ожидается 404")
    Response loginErrorCourier() {
        LoginCourier courier = new LoginCourier(testLoginFail, testPassword);
       return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

}
