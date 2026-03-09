import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.NewOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @DisplayName("Проверка создания заказа")
    @ParameterizedTest
    @CsvSource({
            "Katya, March, Ahaha, 777 apt., 4, +8 888 888 88 88, 7, 2026-08-08, Bugaga, ",
            "Katya, March, Ahaha, 777 apt., 4, +8 888 888 88 88, 7, 2026-08-08, Bugaga, BLACK",
            "Katya, March, Ahaha, 777 apt., 4, +8 888 888 88 88, 7, 2026-08-08, Bugaga, BLACK GREY"
    })
    public void createOrderColorTest(String name, String firstName, String lastName, String address, String metroStation, String phone, String rentTime, String deliveryDate, String comment, String color) {
        createOrderColor(name, firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

    }

    @Step("Создание заказа - ожидание 201")
    public void createOrderColor(String name, String firstName, String lastName, String address, String metroStation, String phone, String rentTime, String deliveryDate, String comment, String color) {
        NewOrder orderColor = new NewOrder(name, firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(orderColor)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().ifError()
                .statusCode(201)
                .body("track", notNullValue());
    }


}
