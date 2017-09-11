/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.homecharge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(MessageServlet.class.getName());

  @Override public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    HashMap<String, String> queryParams = parseQueryParams(request);

    String amount = queryParams.get("amount");
    String note = queryParams.get("note");
    String apiKey = queryParams.get("api-key");
    String token = queryParams.get("registration-token");

    URL url = new URL("https://fcm.googleapis.com/fcm/send");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Authorization", "key=" + apiKey);

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

    String json = String.format( //
        "{\"data\":{\"amount\":\"%s\",\"note\":\"%s\"}," //
            + "\"to\":\"%s\"}", //
        amount, //
        note, //
        token);

    LOG.info("Sending:" + json);

    writer.write(json);
    writer.close();

    logFcmResponse(connection);

    response.setStatus(connection.getResponseCode());
    response.setContentType("text/plain");
    response.getWriter().println("Over Extracted?");
  }

  private HashMap<String, String> parseQueryParams(HttpServletRequest request)
      throws UnsupportedEncodingException {
    String queryString = request.getQueryString();

    String[] params = URLDecoder.decode(queryString, "UTF-8").split("&");

    HashMap<String, String> queryParams = new HashMap<>();
    for (String param : params) {
      String[] parsedParam = param.split("=");
      queryParams.put(parsedParam[0], parsedParam[1]);
    }
    return queryParams;
  }

  private void logFcmResponse(HttpURLConnection connection) throws IOException {
    StringBuilder responseString = new StringBuilder();
    String line;
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    while ((line = reader.readLine()) != null) {
      responseString.append(line);
    }
    reader.close();
    LOG.info("Response received:" + responseString);
  }
}
