package com.example.jokebot.service.impl;

import com.example.jokebot.Client.CbrClient;
import com.example.jokebot.exception.ServiceException;
import com.example.jokebot.service.ExchangeRatesService;
import org.glassfish.grizzly.nio.transport.DefaultStreamReader;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private static final String USD_XPATH="/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH="/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;

    @Override
    public String getUSDExchangeRate() throws ServiceException, IOException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException, IOException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try{
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/",source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression,document);
        }catch(XPathExpressionException e){
            throw new ServiceException("Impossible to parse XML",e);
        }
    }
}