package project.wpl.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
//import project.wpl.kafka.Sender;
import project.wpl.model.SingleStock;
import project.wpl.model.Stocks;
//import project.wpl.repository.StocksCacheRepository;
import project.wpl.model.UserRegistry;
import project.wpl.service.StocksService;

import javax.validation.Valid;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@RestController
public class UserStockDataController {

//     @Autowired
//     StocksCacheRepository stocksCacheRepository;
    @Autowired
    StocksService stocksService;

//    @Autowired
//    Sender kafkaProducer;

    Logger logger = LoggerFactory.getLogger(UserStockDataController.class);

    @GetMapping(value = "/currentPrice/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity currentValue(@PathVariable("symbol") String symbol){

        final String uri = "http://localhost:9093/currentValue/"+symbol;

        RestTemplate restTemplate = new RestTemplate();
        double result = restTemplate.getForObject(uri, Double.class);
        return new ResponseEntity(result,HttpStatus.ACCEPTED);
    }

    @Cacheable(value = "pastweek", key = "#symbol")
    @GetMapping(value = "/pastWeek/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getPastWeekData(@PathVariable("symbol") String symbol){

            System.out.println("In if loop");
              logger.info("Caching the data ");

           // System.out.println("In if loop");
            final String uri = "http://localhost:9093/pastWeek/" + symbol;
            RestTemplate restTemplate = new RestTemplate();
           String result = restTemplate.getForObject(uri, String.class);

         //   stocksService.updateStock(result,symbol);
            return new ResponseEntity(result,HttpStatus.ACCEPTED);

    }

    @GetMapping(value = "/currentWeek/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentWeekData(@PathVariable("symbol") String symbol){
       System.out.println("In controler "+symbol);
        final String uri = "http://localhost:9093/currentWeek/"+symbol;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    @GetMapping(value = "/currentDay/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentDay(@PathVariable("symbol") String symbol){

        final String uri = "http://localhost:9093/currentDay/"+symbol;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    @GetMapping(value = "/monthToDate/{symbol}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getMonthToDate(@PathVariable("symbol") String symbol,@PathVariable("month") String month) {
          ObjectMapper objectMapper = new ObjectMapper();
          final String uri = "http://localhost:9093/monthToDate/" + symbol+"/"+month;
          RestTemplate restTemplate = new RestTemplate();
          String result = restTemplate.getForObject(uri, String.class);
        //  System.out.println("outpu "+ result);

        return new ResponseEntity(result, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/yearToDate/{symbol}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getYearToDate(@PathVariable("symbol") String symbol, @PathVariable("year") String year){

        final String uri = "http://localhost:9093/yearToDate/"+symbol+"/"+year;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return new ResponseEntity(result, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/fiveYear/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getFiveYear(@PathVariable("symbol") String symbol){

        final String uri = "http://localhost:9093/fiveYear/"+symbol;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }


//    @PostMapping(value = "/kafkaTest", consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public String kafkaTest(@Valid @RequestBody UserRegistry userRegistry) throws Exception {
//        JsonElement json = new Gson().toJsonTree(userRegistry);
//        System.out.println("In controller "+ json.toString());
//        kafkaProducer.send(json.toString());
//        return "Pushed to kafka";
//    }






}
