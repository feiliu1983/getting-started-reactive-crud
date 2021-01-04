package org.acme.reactive.crud;

//import static io.restassured.RestAssured.given;
//import static javafx.beans.binding.Bindings.when;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

//import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
class FruitResourceTest {
    @InjectMock
    Fruit fruit;

    @Test
    public void testListAllFruits() {
        //List all, should have all 4 fruits the database has initially:
        given()
            .when().get("/fruits")
            .then()
            .statusCode(200)
            .body(
                containsString("Kiwi"),
                containsString("Durian"),
                containsString("Pomelo"),
                containsString("Lychee"));

        Multi<Fruit> f = Multi.createFrom().items(new Fruit(111L, "name"));

        Multi<Fruit> countryEntity = Multi.createFrom().items(new
            Fruit(100L, "testname"), new Fruit(111L, "testname"));


//        when(fruit.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
//            .thenReturn(countryEntity);


        when(fruit.findAll(Mockito.any()))
            .thenReturn(countryEntity);

        List<Fruit> lessonList = given()
            .when().get("/lessons")
            .then()
            .statusCode(200)
            .extract().body().jsonPath().getList(".", Fruit.class);

        //Delete the Kiwi:
        given()
            .when().delete("/fruits/1")
            .then()
            .statusCode(204);

        //List all, Kiwi should be missing now:
        given()
            .when().get("/fruits")
            .then()
            .statusCode(200)
            .body(
                not(containsString("Kiwi")),
                containsString("Durian"),
                containsString("Pomelo"),
                containsString("Lychee"));
    }

}
