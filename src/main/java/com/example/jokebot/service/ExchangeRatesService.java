package com.example.jokebot.service;

import com.example.jokebot.exception.ServiceException;

import java.io.IOException;

public interface ExchangeRatesService {

    String getUSDExchangeRate() throws ServiceException, IOException;
    String getEURExchangeRate() throws ServiceException, IOException;
}
