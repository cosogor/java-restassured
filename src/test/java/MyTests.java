import static io.restassured.RestAssured.*;
import static  org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
/*
Scenario:
        Scenario: Remaining cards correctly updated after drawing
        1. Assuming a new deck starts with 52 cards:
        2. Draw cards from the deck 5 times each time draw between 1 and 5 cards at random
        3. Return the first drawn card back to the deck
        4. Verify that the correct number of cards are remaining in the deck
*/


public class MyTests {
//    String someRandomString = String.format("%1$TH%1$TM%1$TS", new Date());


    // @Test (enabled=false)
    @Test (priority=3)
    //Smoke test Api check in BDD\Gherkin style
    void test001(){

        given().
        when().get("http://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1").
        then().
            statusCode(200).
            body("remaining", equalTo(52)).
            body("success", equalTo (true)).
            body("deck_id", not (equalTo(""))).
            body("shuffled", equalTo(true)).
            body("remaining", equalTo(52));
    }

    @Test (priority=2)//Api check in old school style
    void test002(){
        String deck_id = "";
        String first_card = "";
        boolean success = false;
        int cards_remaining = 0;        // cards in actual deck state
        int cards_remaining_old = 0;    // cards in old deck state

        // Create new Cards Deck =1 Cards = 52 by default
        Response response = get("http://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1");

        System.out.println("Header: " + response.getHeader("content-type"));
        System.out.println("Body: " + response.getBody().asString());

        System.out.print("Status Code: " +  response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200);

        System.out.println("\t Time taken: " + response.getTime());

        success = response.jsonPath().getBoolean("success");
        Assert.assertEquals(response.jsonPath().getBoolean("success"),true);

        deck_id = response.jsonPath().getString("deck_id");
        Assert.assertNotEquals(deck_id,"");
        System.out.println("Created deck_id: " + deck_id);

        cards_remaining = response.jsonPath().getInt("remaining");
        System.out.println("Cards remaining in deck: " + cards_remaining);
        Assert.assertEquals(cards_remaining,52);

        cards_remaining_old = cards_remaining;

        // Draw a Cards random [1;5] x5 times and check Deck remaining cards:
        for (int i = 1; i<=5; i++) {
            int random_cards_count = (1 + (int) (Math.random() * ((5 - 1) + 1)));

            String url_draw_random_cards = "http://deckofcardsapi.com/api/deck/" + deck_id + "/draw/?count=" + random_cards_count;
            System.out.println("Draw a Cards: " + url_draw_random_cards);
            response = get(url_draw_random_cards);

            System.out.print("Status Code: " +  response.getStatusCode());
            Assert.assertEquals(response.getStatusCode(), 200);
            System.out.println("\t Time taken: " + response.getTime());

            System.out.println("Body: " + response.getBody().asString());

            success = response.jsonPath().getBoolean("success");
            Assert.assertEquals(response.jsonPath().getBoolean("success"),true);

            cards_remaining = response.jsonPath().getInt("remaining");
            System.out.println("Cards remaining check: " + cards_remaining_old + "-" + random_cards_count + "=" + cards_remaining);
            System.out.println("Cards remaining in deck after Draw: " + cards_remaining);

            // check remaining cards number after Draw
            Assert.assertEquals (cards_remaining_old-random_cards_count,cards_remaining);

            cards_remaining_old = cards_remaining;

            // Read the first drawn card back to the deck
            if (i==1) {
                first_card = response.jsonPath().getString("cards[0].code");
                Assert.assertNotEquals(first_card,"");
                System.out.println("Deck first card Draw: " + first_card);
            }
        }
        // Return the first drawn card back to the deck
        String url_return_first_card = "http://deckofcardsapi.com/api/deck/" + deck_id + "/return/?cards=" + first_card;
        System.out.println("Return a Cards: " + first_card);
        response = get(url_return_first_card);
        System.out.print("Status Code: " +  response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("\t Time taken: " + response.getTime());

        System.out.println("Body: " + response.getBody().asString());

        success = response.jsonPath().getBoolean("success");
        Assert.assertEquals(response.jsonPath().getBoolean("success"),true);

        cards_remaining = response.jsonPath().getInt("remaining");
        System.out.println("Cards remaining check after return: " + cards_remaining_old + "+" + 1 + "=" + cards_remaining);
        System.out.println("Cards remaining to deck after Return cards: " + cards_remaining);

        // check remaining cards number after Return card
        Assert.assertEquals(cards_remaining_old + 1, cards_remaining);
    }
    @Test (priority=1)
        //REST API POST request test
    void test003(){
        String requestBody = "{\n" +
                "    \"name\": \"morpheus\",\n" +
                "    \"job\": \"leader\"\n" +
                "}";
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "morpheus");
        requestParams.put("job", "leader");
        String json = requestParams.toString();
        request.body(requestParams.toString());

        given().
                when()
                .header("Content-type", "application/json")
                .body(json)
                .post("https://reqres.in/api/users").
                then().
                statusCode(201).
                body("name", equalTo("morpheus")).
                body("job", equalTo ("leader")).
                body("id", not (equalTo("246")));
    }
}
