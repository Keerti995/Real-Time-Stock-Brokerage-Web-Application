package project.wpl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;


public class Stocks implements Serializable {


    private String symbol;


    List<SingleStock> stocksList;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<SingleStock> getStocksList() {
        return stocksList;
    }

    public void setStocksList(List<SingleStock> stocksList) {
        this.stocksList = stocksList;
    }






}
