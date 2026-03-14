import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class ListOrderTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }


    @DisplayName("Проверка списка заказов")
    @Test
    public void listOrderTest() {
        listOrder()
                .then()
                .log().ifError()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Проверка списка заказов - ожидание 200")
    public Response listOrder() {
        return given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 10)
                .queryParam("page", 0)
                .when()
                .get("/v1/orders?limit=10&page=0");
    }
}
