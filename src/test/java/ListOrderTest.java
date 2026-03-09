import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
    public void listOrderTest(){
        listOrder();
    }

    @Step("Проверка списка заказов - ожидание 200")
    public void listOrder() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/orders?limit=10&page=0")
                .then()
                .log().ifError()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
