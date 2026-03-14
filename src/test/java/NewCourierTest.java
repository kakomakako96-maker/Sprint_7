import io.qameta.allure.Description;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewCourierTest {

    private static String testLogin;
    private static String testPassword;
    private static String testFirstName;

    @BeforeEach
    public void setUp() {
        testLogin = String.valueOf(UUID.randomUUID());
        testPassword = String.valueOf(UUID.randomUUID());
        testFirstName = "FirstName " + testLogin;
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterEach
    void tearDown() {
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

    @DisplayName("Создание курьеров")
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    @Test
    void createDoubleCourierTesting() {
        String actualOne = createCourier()
                .then()
                .log().ifError()
                .extract()
                .body()
                .asString();
        assertEquals("{\"ok\":true}", actualOne, "Ожидался ответ \"{\\\"ok\\\":true}\", но получен " + actualOne );

        int actualTwo = createDubleCourier()
                .then()
                .log().ifError()
                .extract()
                .statusCode();
        assertEquals(409, actualTwo, "Ожидался статус 409, но получен " + actualTwo);
    }

    private static Stream<Arguments> courierCreate() {
        testLogin = String.valueOf(UUID.randomUUID());
        testPassword = String.valueOf(UUID.randomUUID());
        testFirstName = "FirstName " + testLogin;
        return Stream.of(
                Arguments.of(testLogin, "", testFirstName),
                Arguments.of("", testPassword, testFirstName)
        );
    }

    @DisplayName("Проверка заполнения полей")
    @Description("Проверка, чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @ParameterizedTest
    @MethodSource("courierCreate")
    public void createBoxCourier(String login, String password, String firstName) {
        int actual = createCourierOneBox(login, password, firstName)
                .then()
                .log().ifError()
                .extract()
                .statusCode();
        assertEquals(400, actual, "Ожидался статус 400, но получен " + actual);
    }

    @Step("Создание курьера - ожидание 201")
    public Response createCourier() {
        NewCourier courier = new NewCourier(testLogin, testPassword, testFirstName);

        return given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Создание дубликата курьера - ожидание 409")
    public Response createDubleCourier() {
        NewCourier courierTwo = new NewCourier(testLogin, testPassword, testFirstName);
        return given()
                .contentType(ContentType.JSON)
                .body(courierTwo)
                .when()
                .post("/api/v1/courier");

    }

    @Step("Создаем курьера с одним заполненным полем - ожидание 400")
    public Response createCourierOneBox(String login, String password, String firstName) {
        NewCourier courier = new NewCourier(login, password, firstName);
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");

    }
}