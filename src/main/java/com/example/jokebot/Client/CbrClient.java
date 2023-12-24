package com.example.jokebot.Client;

import com.example.jokebot.exception.ServiceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.glassfish.jersey.client.ClientAsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CbrClient {

    @Autowired
    private OkHttpClient client;

    @Value("${cbr.currency.rates.xml.url}")
    private String url;

    public String getCurrencyRatesXML() throws ServiceException, IOException {
        var request = new Request.Builder()
                .url(url)
                .build();
       try(var response = client.newCall(request).execute()){
            var body = response.body();
            return body == null? null:body.string();
       }catch(IOException e){
           throw new ServiceException("Error with getting currency rate",e);
       }

    }
}
