package project.wpl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "bankaccount", schema = "public")
public class BankAccount {



  @Column(name = "accountnumber")
  private int accountnumber;

  @Column(name = "routingnumber")
  private String routingnumber;

  @Column(name = "user_name")
  private String user_name;

  @Column(name = "balance")
  private Double balance;

  @Id
  public int getAccountnumber() {
    return accountnumber;
  }

  public void setAccountnumber(int accountnumber) {
    this.accountnumber = accountnumber;
  }

  public String getRoutingnumber() {
    return routingnumber;
  }

  public void setRoutingnumber(String routingnumber) {
    this.routingnumber = routingnumber;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }


}
