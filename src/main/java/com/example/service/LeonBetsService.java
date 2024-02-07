package com.example.service;

import com.example.client.LeonBetsHttpClient;
import com.example.exception.LeonBetsExceptions;
import com.example.model.EventDto;
import com.example.model.LeagueEventsDto;
import com.example.model.MarketDto;
import com.example.model.RegionDto;
import com.example.model.SportDto;
import com.example.model.SportInternalLeagueDto;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class LeonBetsService {

    private static final Integer COUNT_THREADS_NUMBER = 3;
    public final LeonBetsHttpClient leonBetsHttpClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(COUNT_THREADS_NUMBER);;
    public LeonBetsService() {
        this.leonBetsHttpClient = new LeonBetsHttpClient();
    }

    public void proceed() throws InterruptedException, ExecutionException {
        List<SportDto> sportDtos = leonBetsHttpClient.getSports();


        List<Callable<String>> tasks = sportDtos.stream()
                .map(this::getTopLeagues)
                .flatMap(Collection::stream)
                .map(this::createTask)
                .collect(Collectors.toList());

        List<Future<String>> futures = executorService.invokeAll(tasks);

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
    }

    private List<String> getTopLeagues(SportDto sportDto) {
        return sportDto.getRegions().stream()
                .map(RegionDto::getLeagues)
                .flatMap(Collection::stream)
                .filter(SportInternalLeagueDto::getTop)
                .map(SportInternalLeagueDto::getId)
                .collect(Collectors.toList());
    }

    private Callable<String> createTask(String leagueId) {
        return () -> {
            LeagueEventsDto leagueEvents = leonBetsHttpClient.getLeagueEvents(leagueId);
            Optional<EventDto> freshEventOptional = leagueEvents.getEvents().stream()
                    .filter(event -> event.getMarkets() != null)
                    .min(Comparator.comparingLong(EventDto::getKickoff));

            if (freshEventOptional.isPresent()) {
                EventDto event = leonBetsHttpClient.getEvent(freshEventOptional.get().getId());

                return mapToString(event);
            } else {
                return "";
            }
        };
    }

    private String mapToString(EventDto event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.getLeague().getSport().getName());
        stringBuilder.append(",");
        stringBuilder.append(event.getLeague().getName());

        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("\t");
        stringBuilder.append(event.getName());
        stringBuilder.append(",");
        stringBuilder.append(Instant.ofEpochMilli(event.getKickoff()));
        stringBuilder.append(",");
        stringBuilder.append(event.getId());

        event.getMarkets().forEach(market -> {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append("\t\t");
            stringBuilder.append(market.getName());
            market.getRunners().forEach(runner -> {
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("\t\t\t");
                stringBuilder.append(runner.getName());
                stringBuilder.append(", ");
                stringBuilder.append(runner.getPrice());
                stringBuilder.append(", ");
                stringBuilder.append(runner.getId());
            });
        });

        return stringBuilder.toString();
    }

}
