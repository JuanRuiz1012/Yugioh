package com.duel.api;

import com.duel.model.Card;
import org.json.JSONObject;

import java.net.http.*;
import java.net.URI;

public class YgoApiClient {
    private static final String API_URL = "https://db.ygoprodeck.com/api/v7/randomcard.php";

    public static Card getRandomMonsterCard() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        // Validar tipo Monster
        if (!json.getString("type").toLowerCase().contains("monster")) {
            return getRandomMonsterCard(); // vuelve a intentar
        }

        String name = json.getString("name");
        int atk = json.optInt("atk", 0);
        int def = json.optInt("def", 0);
        String imageUrl = json.getJSONArray("card_images").getJSONObject(0).getString("image_url");

        return new Card(name, atk, def, imageUrl);
    }
}
