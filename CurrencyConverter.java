import javax.swing.*;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class CurrencyConverter extends JFrame {
    private JPanel panel1;
    private static final String API_KEY = "791cafe0885c82ebd7fb7ac9";
    private static final String API_URL =  "https://v6.exchangerate-api.com/v6/" + API_KEY;


    public static void main(String[] args) {
        //new CurrencyConverter();

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to the currency converter!");

        try {
            Set<String> availableCurrencies = getAvailableCurrencies();

            while (true) {
                System.out.println("Available Currencies: " + availableCurrencies);

                System.out.println("What is the currency to convert from:");
                String original_currency = input.nextLine().toUpperCase();

                System.out.println("What is the amount you want to convert: ");
                double amount = input.nextDouble();
                input.nextLine();

                if (amount < 1) {
                    System.out.println("Cannot convert this amount. Please put in a different amount.");
                } else {
                    System.out.println("What is the currency you want to convert to?");
                    String conversion_currency = input.nextLine().toUpperCase();

                    if (availableCurrencies.contains(original_currency) && availableCurrencies.contains(conversion_currency)) {
                        double conversion_rate = getConversionRate(original_currency, conversion_currency);
                        double converted_amount = amount * conversion_rate;
                        double rounded_amount = Math.round(converted_amount * 100) / 100.0;

                        System.out.println(amount + " " + original_currency + " is equal to " + rounded_amount + " " + conversion_currency);
                    } else {
                        System.out.println("Invalid currency entered.");
                    }
                }

                System.out.println("Do you want to convert another currency? (yes/no)");
                String continueConversion = input.next().toLowerCase();
                input.nextLine();
                if (!continueConversion.equals("yes")) {
                    break;
                }
            }

            System.out.println("Thank you for using the currency converter!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public CurrencyConverter() {
        setTitle("Currency Converter App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static Set<String> getAvailableCurrencies() throws Exception {
        String urlStr = API_URL + "/latest/USD";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

        Gson gson = new Gson();
        Map<String, Double> ratesMap = gson.fromJson(conversionRates, new TypeToken<Map<String, Double>>(){}.getType());

        return ratesMap.keySet();
    }


    private static double getConversionRate(String fromCurrency, String toCurrency) throws Exception {
        String urlStr = API_URL + "/pair/" + fromCurrency + "/" + toCurrency;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rate");
        return conversionRates.get("conversion_rate").getAsDouble();
    }

}

