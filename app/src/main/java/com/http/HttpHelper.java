package com.http;


import com.dataclasses.Car;
import com.dataclasses.Driver;
import com.dataclasses.Fine;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HttpHelper {
    private static String sessionID = null;

    private static RequestResponse sendGET(String urlString, String parameters) throws IOException {
        URL url = new URL(urlString + "?" + parameters);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (sessionID != null)
            connection.setRequestProperty("Cookie", "JSESSIONID=" + sessionID);

        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedInputStream responseStream = null;
        responseStream = new BufferedInputStream(connection.getInputStream());

        RequestResponse response = new RequestResponse(connection.getResponseCode(), responseStream);

        connection.disconnect();

        return response;
    }

    private static RequestResponse sendPOST(String urlString, String parameters) throws IOException {
        URL url = new URL(urlString);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (sessionID != null)
            connection.setRequestProperty("Cookie", "JSESSIONID=" + sessionID);

        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.connect();

        final DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(parameters);
        out.flush();
        out.close();

        BufferedInputStream responseStream = new BufferedInputStream(connection.getInputStream());

        RequestResponse response = new RequestResponse(connection.getResponseCode(), responseStream);

        connection.disconnect();

        return response;
    }

    public static void changeDriverByLicense(String driverLicense) throws JsonException, IOException {
        // Отправка запроса
        String url = "http://10.0.2.2:8080/drivers";
        String parameters = "dl=" + driverLicense;
        RequestResponse response = sendGET(url, parameters);

        if (response.responseCode == 200) {
            // Десериализация полученного JSON ответа
            BufferedInputStream in = (BufferedInputStream) response.responseStream;
            JsonObject driverJSON = null;
            driverJSON = (JsonObject) Jsoner.deserialize(new InputStreamReader(in));
            in.close();

            Driver.setLicense(driverLicense);
            Driver.setFirstName((String) driverJSON.get("firstName"));
            Driver.setLastName((String) driverJSON.get("lastName"));
        } else {
            Driver.setLicense(null);
            Driver.setFirstName(null);
            Driver.setLastName(null);
        }
    }

    public static ArrayList<Car> getCarsList(String driverLicense) throws JsonException, IOException {
        // Отправка запроса
        String url = "http://10.0.2.2:8080/cars";
        String parameters = "dl=" + driverLicense;
        RequestResponse response = sendGET(url, parameters);
        BufferedInputStream in = (BufferedInputStream) response.responseStream;

        // Десериализация полученного JSON ответа
        JsonArray accountsJSON = null;
        accountsJSON = (JsonArray) Jsoner.deserialize(new InputStreamReader(in));
        in.close();

        // Формирование списка авто из JSON
        ArrayList<Car> carsList = new ArrayList<>();

        accountsJSON.forEach(entry -> {
            JsonObject JSONCar = (JsonObject) entry;

            String model = (String) JSONCar.get("model");
            String plate = (String) JSONCar.get("plate");
            String color = (String) JSONCar.get("color");
            String insurance = (String) JSONCar.get("insurance");
            int fines = ((BigDecimal) (JSONCar.get("finesCount"))).intValue();

            Car car = new Car(
                    model,
                    plate,
                    color,
                    insurance,
                    fines);

            carsList.add(car);
        });

        return carsList;
    }

    public static ArrayList<Fine> getFinesList(String driverLicense) throws JsonException, IOException {
        // Отправка запроса
        String url = "http://10.0.2.2:8080/fines";
        String parameters = "dl=" + driverLicense;
        RequestResponse response = sendGET(url, parameters);
        BufferedInputStream in = (BufferedInputStream) response.responseStream;

        // Десериализация полученного JSON ответа
        JsonArray accountsJSON = null;
        accountsJSON = (JsonArray) Jsoner.deserialize(new InputStreamReader(in));
        in.close();

        // Формирование списка авто из JSON
        ArrayList<Fine> finesList = new ArrayList<>();

        accountsJSON.forEach(entry -> {
            JsonObject JSONCar = (JsonObject) entry;

            String date = (String) JSONCar.get("date");
            String description = (String) JSONCar.get("description");
            int size = ((BigDecimal) (JSONCar.get("size"))).intValue();

            Fine fine = new Fine(
                    date,
                    description,
                    size
                    );

            finesList.add(fine);
        });

        return finesList;
    }

    public static boolean logIn(String login, String password) throws JsonException, IOException {
        boolean status = false;

        String url = "http://10.0.2.2:8080/auth";
        String parameters = "login=" + login + "&password=" + password;
        RequestResponse response = sendPOST(url, parameters);
        BufferedInputStream in = (BufferedInputStream) response.responseStream;

        JsonObject JSONResponse = (JsonObject) Jsoner.deserialize(new InputStreamReader(in));

        sessionID = JSONResponse.get("sessionID").toString();

        if (!sessionID.equals("0"))
            status = true;

        return status;
    }

    public static boolean issueFine(String license, String description, int size) throws IOException, JsonException {
        boolean inserted = false;

        String url = "http://10.0.2.2:8080/fines";
        String parameters =
                        "license=" + license +
                        "&description=" + description +
                        "&size=" + size;

        RequestResponse response = sendPOST(url, parameters);
        BufferedInputStream in = (BufferedInputStream) response.responseStream;

        JsonObject JSONResponse = (JsonObject) Jsoner.deserialize(new InputStreamReader(in));

        inserted = (Boolean) JSONResponse.get("inserted");

        return inserted;
    }
}
