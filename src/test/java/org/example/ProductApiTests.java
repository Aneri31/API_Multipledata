package org.example;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ProductApiTests {

    private String baseUri = "https://api.escuelajs.co/api/v1/products/";

    @DataProvider(name = "productDataProvider")
    public Object[][] productDataProvider() {
        return new Object[][] {
                { 93, "Seragam SMP", 150000, "Seragam untuk pelajar SMP", "[\"https://seragamku.com/seragam-smp.png\"]", 61, "Books", "https://api.lorem.space/image/book?w=150&h=220" },
        };
    }
    @Test(dataProvider = "productDataProvider")
    public void testGetProductById(int expectedId, String expectedTitle, int expectedPrice, String expectedDescription, String expectedImages, int expectedCategoryId, String expectedCategoryName, String expectedCategoryImage) {

        Response response = given()
                .baseUri(baseUri)
                .when()
                .get();
        System.out.println("Response Body: " + response.getBody().asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        List<Map<String, Object>> products = response.jsonPath().getList("$");
        products.forEach(product -> System.out.println("Product: " + product));
        boolean productFound = products.stream().anyMatch(product -> {
            int id = (int) product.get("id");
            String title = (String) product.get("title");
            int price = (int) product.get("price");
            String description = (String) product.get("description");
            List<String> images = (List<String>) product.get("images");
            String imagesString = images.isEmpty() ? "" : images.get(0);  // Convert to string
            Map<String, Object> category = (Map<String, Object>) product.get("category");
            int categoryId = (int) category.get("id");
            String categoryName = (String) category.get("name");
            String categoryImage = (String) category.get("image");

            return id == expectedId &&
                    title.equals(expectedTitle) &&
                    price == expectedPrice &&
                    description.equals(expectedDescription) &&
                    imagesString.equals(expectedImages) &&
                    categoryId == expectedCategoryId &&
                    categoryName.equals(expectedCategoryName) &&
                    categoryImage.equals(expectedCategoryImage);
        });

        Assert.assertTrue(productFound, "Product with ID " + expectedId + " not found.");
    }
}
