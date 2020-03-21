package project.wpl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "current_price", schema = "public")
public class CurrentPrice {

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "company_name")
    private String company_name;

    @Column(name ="price")
    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Id
    public String getSymbol() {
        return symbol;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


}
