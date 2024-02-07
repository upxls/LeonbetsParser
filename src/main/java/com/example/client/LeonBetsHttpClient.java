package com.example.client;

import com.example.exception.LeonBetsExceptions;
import com.example.model.EventDto;
import com.example.model.LeagueEventsDto;
import com.example.model.SportDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.util.List;

public class LeonBetsHttpClient {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LeonBetsHttpClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<SportDto> getSports() {
        HttpGet request = new HttpGet("https://leonbets.com/api-2/betline/sports?ctag=ru-UA&flags=urlv2");
        setupHeaders(request);

        try {
            HttpResponse response = httpClient.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(json, new TypeReference<List<SportDto>>() {});
        } catch (IOException e) {
            throw new LeonBetsExceptions(e);
        }
    }

    public LeagueEventsDto getLeagueEvents(String leagueId) {
        HttpGet request = new HttpGet("https://leonbets.com/api-2/betline/events/all?ctag=ru-UA&league_id="
                + leagueId + "&hideClosed=true&flags=reg,urlv2,mm2,rrc,nodup");
        setupHeaders(request);

        try {
            HttpResponse response = httpClient.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(json, new TypeReference<LeagueEventsDto>() {});
        } catch (IOException e) {
            throw new LeonBetsExceptions(e);
        }
    }

    public EventDto getEvent(String eventId) {
        HttpGet request = new HttpGet("https://leonbets.com/api-2/betline/event/all?ctag=ru-UA&eventId="
                + eventId + "&flags=reg,urlv2,mm2,rrc,nodup,smg,outv2");
        setupHeaders(request);

        try {
            HttpResponse response = httpClient.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(json, new TypeReference<EventDto>() {});
        } catch (IOException e) {
            throw new LeonBetsExceptions(e);
        }
    }

    private void setupHeaders(HttpRequestBase request) {
        request.setHeader("authority", "leonbets.com");
        request.setHeader("accept", "*/*");
        request.setHeader("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
    }

}
