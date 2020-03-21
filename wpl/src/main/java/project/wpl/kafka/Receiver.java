package project.wpl.kafka;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import project.wpl.exception.InsufficientStocksException;
import project.wpl.exception.InvalidAccountNumberException;
import project.wpl.exception.NoSuchStockException;
import project.wpl.model.BuyStock;
import project.wpl.service.UserDetailServiceImpl;

import java.util.concurrent.CountDownLatch;

@Service
public class Receiver {


    @Autowired
    UserDetailServiceImpl userDetailService;
Logger logger = LoggerFactory.getLogger(Receiver.class);

    @KafkaListener(topics = "buywpl", groupId = "project")
    public void receiveBuy(String payload) throws InvalidAccountNumberException, InterruptedException {
        Thread.sleep(10000);
        Gson g = new Gson();
        BuyStock p = g.fromJson(payload, BuyStock.class);
        userDetailService.stockBuy(p);
       System.out.println("in receiver "+payload);
        logger.info("Kafka consumer "+payload);
    }

    @KafkaListener(topics = "sellwpl", groupId = "project")
    public void receiveSell(String payload) throws InvalidAccountNumberException, NoSuchStockException, InterruptedException, InsufficientStocksException {
        Thread.sleep(10000);
        Gson g = new Gson();
        BuyStock p = g.fromJson(payload, BuyStock.class);
        userDetailService.stockSell(p);
        System.out.println("in receiver "+payload);
        logger.info("Kafka consumer "+payload);
    }


}
