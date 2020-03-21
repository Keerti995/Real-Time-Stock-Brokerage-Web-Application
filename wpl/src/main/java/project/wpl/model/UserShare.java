package project.wpl.model;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name="usershare",schema="public")
public class UserShare {

    @Column(name="username")
    private String username;



    @Column(name="symbol")
    private String symbol;
    @Column(name="company")
    private String company;
    @Column(name="quantity")
    private int quantity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
