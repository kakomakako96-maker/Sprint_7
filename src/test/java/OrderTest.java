import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.NewOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.openqa.selenium.support.Colors.GREY;

public class OrderTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }


    private static Stream<Arguments> orderColorData() {
        return Stream.of(
                Arguments.of("Katya", "March", "Ahaha", "777 apt.", "4", "+8 888 888 88 88", "7", "2026-08-08", "Bugaga", new String[0]),
                Arguments.of("Katya", "March", "Ahaha", "777 apt.", "4", "+8 888 888 88 88", "7", "2026-08-08", "Bugaga", new String[]{"BLACK"}),
                Arguments.of("Katya", "March", "Ahaha", "777 apt.", "4", "+8 888 888 88 88", "7", "2026-08-08", "Bugaga", new String[]{"GREY"}),
                Arguments.of("Katya", "March", "Ahaha", "777 apt.", "4", "+8 888 888 88 88", "7", "2026-08-08", "Bugaga", new String[]{"BLACK", "GREY"})
        );
    }

    @DisplayName("Проверка создания заказа")
    @ParameterizedTest
    @MethodSource("orderColorData")
    public void createOrderColorTest(String name, String firstName, String lastName, String address, String metroStation, String phone, String rentTime, String deliveryDate, String comment, String[] color) {
        createOrderColor(name, firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color)
                .then()
                .log().ifError()
                .statusCode(201)
                .body("track", notNullValue());

    }

    @Step("Создание заказа - ожидание 201")
    public Response createOrderColor(String name, String firstName, String lastName, String address, String metroStation, String phone, String rentTime, String deliveryDate, String comment, String[] color) {
        NewOrder orderColor = new NewOrder(name, firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        return given()
                .contentType(ContentType.JSON)
                .log().all()
                .body(orderColor)
                .when()
                .post("/api/v1/orders");
    }


}
