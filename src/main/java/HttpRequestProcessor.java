import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.StringJoiner;

public class HttpRequestProcessor {

    protected static String postUrl = "https://jsonplaceholder.typicode.com/posts/1";
    private final String apiKey = CurrencyApi.getApiKey();
//    protected String defaultApiRequestUrl = buildCurrencyApiUrl("EUR", List.of("USD", "CAD"));

    public JSONObject request(String url) throws IOException {
        HttpURLConnection req = (HttpURLConnection) new URL(url).openConnection();
        req.connect();
        System.out.printf("Response code: %d\n", req.getResponseCode());
        return req.getResponseCode() == HttpURLConnection.HTTP_OK ?
                CurrencyApi.jsonParser(new InputStreamReader(req.getInputStream())) : null;
    }

    public JSONObject getCurrencyData(){
        String query = String.format("apikey=%s", CurrencyApi.getApiKey());
        try {
            URI uri = new URI("https", "api.currencyapi.com", "/v3/currencies", query, null);
            return (JSONObject) request(uri.toString()).get("data");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildCurrencyApiUrl(String baseCurrency, List<String> currencies) {
        StringJoiner joiner = new StringJoiner(",");
        currencies.forEach(c -> joiner.add(c.trim()));
        String query = String.format("apikey=%s&base_currency=%s&currencies=%s", apiKey, baseCurrency, joiner);
        try {
            URI uri = new URI("https", "api.currencyapi.com", "/v3/latest", query, null);
            System.out.println(uri);
            return uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void testGetJsonObject(){
        double val = FileHandler.readJsonFile("test.json")
                .getAsJsonObject("data")
                .getAsJsonObject("USD")
                .get("value").getAsDouble();
        System.out.println(val);
    }


    private static class CurrencyApi {

        private static String file = "src\\main\\resources\\currencyapi_apikey.json";

//        For opening online resources
        public static InputStream urlStream(String url) throws IOException{
            return new URL(url).openStream();
        }

        public static String getApiKey(){
            try {
                Reader r = new FileReader(file);
                return jsonParser(r).get("key").toString();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static JSONObject jsonParser(Reader r) {
            try{
                return (JSONObject) new JSONParser().parse(r);
            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


