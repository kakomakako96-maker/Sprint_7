import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

public class NewCourierTest {

    private String testLogin = "Katya";
    private String testPassword = "1234";
    private String testFirstName = "March";

    @BeforeEach
    public void setUp() {
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
    void createCourierTesting() {
        createCourier();
        createDubleCourier();
    }

    @DisplayName("Проверка заполнения полей")
    @Description("Проверка, чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @ParameterizedTest
    @CsvSource({
            "Katya, , March",
            " , 1234, March"
    })
    public void createBoxCourier(String testLogin, String testPassword, String testFirstName) {
        createCourierOneBox(testLogin, testPassword, testFirstName);
    }

    @Step("Создание курьера - ожидание 201")
    public void createCourier() {
        NewCourier courier = new NewCourier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().ifError()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Создание дубликата курьера - ожидание 409")
    public void createDubleCourier() {
        NewCourier courierTwo = new NewCourier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courierTwo)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().ifError()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Создаем курьера с одним заполненным полем - ожидание 400")
    public void createCourierOneBox(String testLogin, String testPassword, String testFirstName) {
        NewCourier courier = new NewCourier(testLogin, testPassword, testFirstName);
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .log().ifError()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }
}