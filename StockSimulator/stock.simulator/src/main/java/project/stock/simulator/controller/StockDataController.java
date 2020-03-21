package project.stock.simulator.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import project.stock.simulator.model.SingleStock;
import project.stock.simulator.model.Stocks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
public class StockDataController {

    @GetMapping(value = "/pastWeek/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPastWeekData(@PathVariable("symbol")  String symbol)  {
        System.out.println("Url hit");
        int count = 1;
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();
        Gson gson = new Gson();
        LocalDate now = new LocalDate();
        while(count<=7){
            if(now.getDayOfWeek() != 6 && now.getDayOfWeek() != 7) {
            String date = now.toString();
            double low = (Math.random()*((500.0-10.0)+1))+10;
            low = Math.round(low * 100.0) / 100.0;
            double high = (Math.random()*((500-low)+1))+low;
            high = Math.round(high * 100.0) / 100.0;
            double open = (Math.random()*((high-low)+1))+low;
            open = Math.round(open * 100.0) / 100.0;
            double close = (Math.random()*((high-low)+1))+low;
            close = Math.round(close * 100.0) / 100.0;
            Random r = new Random();
            int volume = r.nextInt((100000 - 0) + 1) + 0;


            SingleStock singleStock = new SingleStock();
            singleStock.setDate(date);
            singleStock.setLow(low);
            singleStock.setHigh(high);
            singleStock.setClose(close);
            singleStock.setOpen(open);
            //  singleStock.setSymbol(symbol);
            singleStock.setVolume(volume);
            list.add(singleStock);
            count++;
            }
            now = now.minusDays(1);


        }

        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);
       /* FileWriter fileWriter = new FileWriter("output.json");
        gson.toJson(stocks, fileWriter);
        fileWriter.flush();
        fileWriter.close();*/
        //System.out.println(json.toString());
        return json.toString();

    }
    @GetMapping(value = "/currentWeek/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentWeekData(@PathVariable("symbol")  String symbol)  {
        Gson gson = new Gson();
        LocalDate now = new LocalDate();
        LocalDate prev = now.withDayOfWeek(DateTimeConstants.MONDAY);
        now = now.plusDays(1);
        System.out.println(now);
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();

        while (!prev.equals(now)) {
            //System.out.println(now);
            double low = (Math.random()*((500.0-10.0)+1))+10;
            low = Math.round(low * 100.0) / 100.0;
            double high = (Math.random()*((500-low)+1))+low;
            high = Math.round(high * 100.0) / 100.0;
            double open = (Math.random()*((high-low)+1))+low;
            open = Math.round(open * 100.0) / 100.0;
            double close = (Math.random()*((high-low)+1))+low;
            close = Math.round(close * 100.0) / 100.0;
            Random r = new Random();
            int volume = r.nextInt((100000 - 0) + 1) + 0;
            String date = String.valueOf(prev);


            SingleStock singleStock = new SingleStock();
            singleStock.setDate(date);
            singleStock.setLow(low);
            singleStock.setHigh(high);
            singleStock.setClose(close);
            singleStock.setOpen(open);
            //  singleStock.setSymbol(symbol);
            singleStock.setVolume(volume);
            list.add(singleStock);

            prev = prev.plusDays(1);
        }
        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);
       /* FileWriter fileWriter = new FileWriter("output.json");
        gson.toJson(stocks, fileWriter);
        fileWriter.flush();
        fileWriter.close();*/

        return json.toString();
    }

    @GetMapping(value = "/currentDay/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentDay(@PathVariable("symbol")  String symbol)  {
        Gson gson = new Gson();
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();

        LocalDate today = new LocalDate();
        if(today.getDayOfWeek() == 6)
            today = today.minusDays(1);
        else if(today.getDayOfWeek() == 7)
            today = today.minusDays(2);

        double low = (Math.random()*((500.0-10.0)+1))+10;
        low = Math.round(low * 100.0) / 100.0;
        double high = (Math.random()*((500-low)+1))+low;
        high = Math.round(high * 100.0) / 100.0;
        double open = (Math.random()*((high-low)+1))+low;
        open = Math.round(open * 100.0) / 100.0;
        double close = (Math.random()*((high-low)+1))+low;
        close = Math.round(close * 100.0) / 100.0;
        Random r = new Random();
        int volume = r.nextInt((100000 - 0) + 1) + 0;
        String date = String.valueOf(today);

        SingleStock singleStock = new SingleStock();
        singleStock.setDate(date);
        singleStock.setLow(low);
        singleStock.setHigh(high);
        singleStock.setClose(close);
        singleStock.setOpen(open);
        //  singleStock.setSymbol(symbol);
        singleStock.setVolume(volume);
        list.add(singleStock);

        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);
       /* FileWriter fileWriter = new FileWriter("output.json");
        gson.toJson(stocks, fileWriter);
        fileWriter.flush();
        fileWriter.close();*/

        return json.toString();
    }

    @GetMapping(value = "/monthToDate/{symbol}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getMonthToDate(@PathVariable("symbol")  String symbol,@PathVariable("month") String month) {

        month=month.trim();
        Map<String,Integer> map = new HashMap<>();
        map.put("Jan",1);
        map.put("February",2);
        map.put("March",3);
        map.put("April",4);
        map.put("May",5);
        map.put("June",6);
        map.put("July",7);
        map.put("August",8);
        map.put("September",9);
        map.put("October",10);
        map.put("November",11);
        map.put("December",12);

        Gson gson = new Gson();
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();

        LocalDate today = new LocalDate();
        LocalDate firstDay;
        if(new LocalDate().getMonthOfYear() < map.get(month)) {
          firstDay = today.dayOfMonth().withMinimumValue();
        }
         else{
              firstDay = new LocalDate("2019-" + map.get(month) + "-01");
            }

        while(!firstDay.equals(today)) {

            if(firstDay.getDayOfWeek() != 6 && firstDay.getDayOfWeek() != 7) {
                double low = (Math.random() * ((500.0 - 10.0) + 1)) + 10;
                low = Math.round(low * 100.0) / 100.0;
                double high = (Math.random() * ((500 - low) + 1)) + low;
                high = Math.round(high * 100.0) / 100.0;
                double open = (Math.random() * ((high - low) + 1)) + low;
                open = Math.round(open * 100.0) / 100.0;
                double close = (Math.random() * ((high - low) + 1)) + low;
                close = Math.round(close * 100.0) / 100.0;
                Random r = new Random();
                int volume = r.nextInt((100000 - 0) + 1) + 0;
                String date = String.valueOf(firstDay);

                SingleStock singleStock = new SingleStock();
                singleStock.setDate(date);
                singleStock.setLow(low);
                singleStock.setHigh(high);
                singleStock.setClose(close);
                singleStock.setOpen(open);
                //  singleStock.setSymbol(symbol);
                singleStock.setVolume(volume);
                list.add(singleStock);
            }
            firstDay = firstDay.plusDays(1);
        }
        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);
        return json.toString();
    }

    @GetMapping(value = "/yearToDate/{symbol}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getYearToDate(@PathVariable("symbol")  String symbol, @PathVariable("year")  String year) {
        year = year.trim();
        LocalDate fromYear = new LocalDate(year+"-01-01");
        Gson gson = new Gson();
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();

        LocalDate today = new LocalDate();

        while(!fromYear.equals(today)) {

            if(fromYear.getDayOfWeek() != 6 && fromYear.getDayOfWeek() != 7) {
                double low = (Math.random() * ((500.0 - 10.0) + 1)) + 10;
                low = Math.round(low * 100.0) / 100.0;
                double high = (Math.random() * ((500 - low) + 1)) + low;
                high = Math.round(high * 100.0) / 100.0;
                double open = (Math.random() * ((high - low) + 1)) + low;
                open = Math.round(open * 100.0) / 100.0;
                double close = (Math.random() * ((high - low) + 1)) + low;
                close = Math.round(close * 100.0) / 100.0;
                Random r = new Random();
                int volume = r.nextInt((100000 - 0) + 1) + 0;
                String date = String.valueOf(fromYear);

                SingleStock singleStock = new SingleStock();
                singleStock.setDate(date);
                singleStock.setLow(low);
                singleStock.setHigh(high);
                singleStock.setClose(close);
                singleStock.setOpen(open);
                //  singleStock.setSymbol(symbol);
                singleStock.setVolume(volume);
                list.add(singleStock);
            }
            fromYear = fromYear.plusDays(1);
        }
        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);
        return json.toString();

    }

    @GetMapping(value = "/fiveYear/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getFiveYear(@PathVariable("symbol") String symbol) {
        Gson gson = new Gson();
        Stocks stocks = new Stocks();
        List<SingleStock> list = new ArrayList<SingleStock>();

        LocalDate today = new LocalDate();
        LocalDate fiveYear = new LocalDate("2015-01-01");

        while(!fiveYear.equals(today)) {

            if(fiveYear.getDayOfWeek() != 6 && fiveYear.getDayOfWeek() != 7) {
                double low = (Math.random() * ((500.0 - 10.0) + 1)) + 10;
                low = Math.round(low * 100.0) / 100.0;
                double high = (Math.random() * ((500 - low) + 1)) + low;
                high = Math.round(high * 100.0) / 100.0;
                double open = (Math.random() * ((high - low) + 1)) + low;
                open = Math.round(open * 100.0) / 100.0;
                double close = (Math.random() * ((high - low) + 1)) + low;
                close = Math.round(close * 100.0) / 100.0;
                Random r = new Random();
                int volume = r.nextInt((100000 - 0) + 1) + 0;
                String date = String.valueOf(fiveYear);

                SingleStock singleStock = new SingleStock();
                singleStock.setDate(date);
                singleStock.setLow(low);
                singleStock.setHigh(high);
                singleStock.setClose(close);
                singleStock.setOpen(open);
                //  singleStock.setSymbol(symbol);
                singleStock.setVolume(volume);
                list.add(singleStock);
            }
            fiveYear = fiveYear.plusDays(1);
        }
        stocks.setStocksList(list);
        JsonElement json = gson.toJsonTree(stocks);

        return json.toString();


    }
@GetMapping(value = "/currentValue/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public double currentValue(@PathVariable("symbol") String symbol){
        String output = "";
        double stockValue = 0.0;
        //System.out.println("in Second server" );
        try {
            URL url = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+"&apikey=YNNNXAMXZNPWGTUD");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            int code = con.getResponseCode();
            System.out.println(code);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            output = content.toString();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(output).getAsJsonObject();
            LocalDate today = new LocalDate();
            if(today.getDayOfWeek() == 6)
                today = today.minusDays(1);
            else if(today.getDayOfWeek() == 7)
                today = today.minusDays(2);
            int hrs = LocalDateTime.now().getHourOfDay();
            if(hrs<9)
                today = today.minusDays(1);

            stockValue = jsonObject.get("Time Series (Daily)").getAsJsonObject().get(today.toString()).getAsJsonObject().get("4. close").getAsDouble();
            //json = jsonObject.toString();
        }catch (Exception e){

        }
 return stockValue;

    }

  /*  public static void main(String[] arg){

          System.out.println(  new StockDataController().currentValue("AMZN"));

    }*/

}
