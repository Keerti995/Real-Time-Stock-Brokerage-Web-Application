package project.wpl.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Sender {



    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendBuy(String payload) {
       System.out.println(payload);
       //System.out.println("in buy receiver");
        kafkaTemplate.send("buywpl", payload);
    }

    public void sendSell(String payload) {
        System.out.println(payload);
     //   System.out.println("in receiver sell");
        kafkaTemplate.send("sellwpl", payload);
    }

}
