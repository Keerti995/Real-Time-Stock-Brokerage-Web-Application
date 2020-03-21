package project.wpl.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.wpl.model.SingleStock;
import project.wpl.repository.StockCacheRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StocksService {



    @Autowired
    private StockCacheRepository stockCacheRepository;




    public String getStockBySymbol(String symbol)  {
        System.out.println("####"+symbol);
        SingleStock singleStock = stockCacheRepository.getOne(symbol);

        if(singleStock.getJsonResult() == null)
            return "";
        else
          return singleStock.getJsonResult();
    }

    public void updateStock(String result, String symbol) {
        SingleStock singleStock = new SingleStock();
        singleStock.setSymbol(symbol);
        singleStock.setJsonResult(result);
        stockCacheRepository.save(singleStock);
    }

}
