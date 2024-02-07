package com.example;

import com.example.service.LeonBetsService;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        LeonBetsService leonBetsService = new LeonBetsService();
        leonBetsService.proceed();
    }
}
