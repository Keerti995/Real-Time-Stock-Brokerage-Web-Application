package project.wpl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import project.wpl.exception.*;
import project.wpl.model.*;
import project.wpl.repository.*;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Autowired
  private RegistrationRepository registrationRepository;

  @Autowired
  private UserShareRepository userShareRepository;

  @Autowired
  private StockListRepository stockListRepository;

  public void updateUserInformation(@Valid UserRegistry userRegistry, String username) {
    // TODO Auto-generated method stub

    Optional<UserRegistry> findByIdResult = registrationRepository.findById(username);
    //System.out.println("username " + username);

    if (findByIdResult.isPresent()) {
      findByIdResult.get().setEmail(userRegistry.getEmail());
      findByIdResult.get().setAddress(userRegistry.getAddress());
      registrationRepository.save(findByIdResult.get());
    } else {
      throw new ResourceNotFoundException("Username not found");
    }

  }

  public void createBankAccount(@Valid BankAccount bankAccount, String username) {
    // TODO Auto-generated method stub
    //System.out.println("account balance " + bankAccount.getBalance());
    //System.out.println("username isssss bankaccount " + username);
    bankAccount.setUser_name(username);
    bankAccountRepository.save(bankAccount);
  }


  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    UserRegistry user = registrationRepository.findByUsername(userName);
    if (user == null)
      throw new UsernameNotFoundException(userName);

    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
    for (Role role : user.getRoles()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    return new org.springframework.security.core.userdetails.User(user.getUsername(),
        user.getPasswd(), grantedAuthorities);

  }
   @Transactional
    public void transferMoney(TransferInfo transferInfo) throws  InsufficientFundsException{

           Optional<BankAccount> findByIdResult = bankAccountRepository.findById(transferInfo.getToAccounNumber());
           Optional<BankAccount> findFromAccountById = bankAccountRepository.findById(transferInfo.getFromAccountNumber());
           double fromAcccountBalance = findFromAccountById.get().getBalance();

          // System.out.println("transerInfo "+transferInfo.getFromAccountNumber());
           //System.out.println("transfer info to " + transferInfo.getToAccounNumber());
           //System.out.println("balance "+ transferInfo.getAmountToTransfer());
           //System.out.println(accountBalance"");
           if(fromAcccountBalance<transferInfo.getAmountToTransfer()) throw new InsufficientFundsException("Insufficent funds");
           double balance = findByIdResult.get().getBalance();
           double updatedBalance = balance + transferInfo.getAmountToTransfer();
           findByIdResult.get().setBalance(updatedBalance);
           bankAccountRepository.save(findByIdResult.get());


           double updatedFromAccountBalance = fromAcccountBalance - transferInfo.getAmountToTransfer();
           findFromAccountById.get().setBalance(updatedFromAccountBalance);
           bankAccountRepository.save(findFromAccountById.get());
    }

    @Transactional
    public void stockBuy(BuyStock buyStock) throws InsufficientFundsException, InvalidAccountNumberException {
        Optional<BankAccount> findByAccountNumber = bankAccountRepository.findById(buyStock.getAccountNumber());
        //System.out.println("Account Number is "+ buyStock.getAccountNumber());
     //   System.out.println("Username is "+ findByAccountNumber.get().getUser_name());
        //System.out.println(("Second Username is "+buyStock.getUsername()));

       // if(findByAccountNumber.get().getUser_name()!=buyStock.getUsername())
         //     throw new InvalidAccountNumberException("Account Number does not exist");

        double updatedBalance=accountBalanceUpdate(buyStock,findByAccountNumber,"buy");
        if(updatedBalance<0)
            throw new InsufficientFundsException("Insufficent funds");
        findByAccountNumber.get().setBalance(updatedBalance);
        bankAccountRepository.save(findByAccountNumber.get());
        List<UserShare> findUserShareBySymbol = userShareRepository.findByUsername(buyStock.getUsername());
       UserShare userShare=new UserShare();
       boolean flag=false;
       if(findUserShareBySymbol.size()!=0) {
           for (int i = 0; i < findUserShareBySymbol.size(); i++) {

               //System.out.println("username is "+findUserShareBySymbol.get(i).getUsername());
              // System.out.println("company is "+findUserShareBySymbol.get(i).getCompany());

               String symbol = findUserShareBySymbol.get(i).getSymbol();


              // System.out.println("symbol is "+symbol);
               if (buyStock.getSymbol().equals(symbol)) {
                   Long u = findUserShareBySymbol.get(i).getId();
                   int quantity = findUserShareBySymbol.get(i).getQuantity();
                   int updatedQuantity = quantity + buyStock.getNumberOfUnits();
                //   System.out.println("updated quantity is "+updatedQuantity);
                   findUserShareBySymbol.get(i).setQuantity(updatedQuantity);
                   userShareRepository.save(findUserShareBySymbol.get(i));
                 //  System.out.println("flag true");
                   flag=true;
                   break;
               }

           }
       }
       if(flag==false){

           userShare.setSymbol(buyStock.getSymbol());
           userShare.setUsername(buyStock.getUsername());
           userShare.setQuantity(buyStock.getNumberOfUnits());
           userShare.setCompany(buyStock.getCompany_name());
           // findUserShareBySymbol.add(userShare);

        //   System.out.println("before saving flag false");
           userShareRepository.save(userShare);

       }
    }

    public void stockSell(BuyStock buyStock) throws InsufficientStocksException, NoSuchStockException {
        Optional<BankAccount> findByAccountNumber = bankAccountRepository.findById(buyStock.getAccountNumber());
         //      findByAccountNumber.get().getAccountnumber();
       // System.out.println("account number "+buyStock.getAccountNumber());
       // System.out.println("");
        double updatedBalance=accountBalanceUpdate(buyStock,findByAccountNumber,"sell");
        findByAccountNumber.get().setBalance(updatedBalance);
        bankAccountRepository.save(findByAccountNumber.get());
        List<UserShare> findUserShareBySymbol = userShareRepository.findByUsername(buyStock.getUsername());
        boolean flag= false;
        String symbol = findUserShareBySymbol.get(0).getSymbol();
      for(int i=0;i<findUserShareBySymbol.size();i++)
      {
         // System.out.println("in sell flag before");
          flag=true;
             symbol = findUserShareBySymbol.get(i).getSymbol();
          //  System.out.println("symbol is "+symbol);
            if(buyStock.getSymbol().equals(symbol) ){

          int quantity = findUserShareBySymbol.get(i).getQuantity();
          if (buyStock.getNumberOfUnits() > quantity)
              throw new InsufficientStocksException("Insufficent stocks");
          else if(buyStock.getNumberOfUnits() == quantity)
          {

              userShareRepository.delete(findUserShareBySymbol.get(i));
              break;
          }
          int updatedQuantity = quantity - buyStock.getNumberOfUnits();
        //  System.out.println("updated quantity "+updatedQuantity);
          findUserShareBySymbol.get(i).setQuantity(updatedQuantity);
          userShareRepository.save(findUserShareBySymbol.get(i));

          break;
       }
      }
        if(flag == false)
        {
            throw new NoSuchStockException("no such stock");

        }

    }




    private double accountBalanceUpdate(BuyStock buyStock, Optional<BankAccount> findByAccountNumber, String func) {
        int accountNumber= buyStock.getAccountNumber();
        //Optional<CurrentPrice> findByCurrentPrice = currentPriceRepository.findById(buyStock.getSymbol());
       double stockPrice=currentValue(buyStock.getSymbol());
        double totalPrice = buyStock.getNumberOfUnits()*stockPrice;
        double currentAccountBalance=findByAccountNumber.get().getBalance();
        double updateBalance=0.0;
        if(func.equals("buy"))
           updateBalance=currentAccountBalance-totalPrice;
        else if(func.equals("sell"))
            updateBalance=currentAccountBalance+totalPrice;
      //  System.out.println("updated Balance"+updateBalance);

        return updateBalance;
    }

    public List<JsonNode> getProfileInfo(String username) throws IOException {
        List<UserShare> byUsername = userShareRepository.findByUsername(username);
        List<UserDetailOutput> symbolList =new ArrayList<UserDetailOutput>();
        double stockPrice=0.0;
        double totalStockPrice=0.0;
        UserDetailOutput userDetailOutput = new UserDetailOutput();
        List<JsonNode> jsonList=new ArrayList<JsonNode>();
       // Gson gson = new Gson();
        for(int i=0;i<byUsername.size();i++)
        {
              String symbol = byUsername.get(i).getSymbol();
              //change this code to call from another sever using rest-template

              stockPrice=currentValue(symbol);
               System.out.println("stockPrice "+stockPrice);
              totalStockPrice =stockPrice*byUsername.get(i).getQuantity();
              userDetailOutput.setNetWorth(totalStockPrice);
              userDetailOutput.setQuantity(byUsername.get(i).getQuantity());
              userDetailOutput.setCompany_name(byUsername.get(i).getCompany());
              userDetailOutput.setSymbol(symbol);
              symbolList.add(userDetailOutput);
            ObjectMapper objectMapper = new ObjectMapper();
            String userDetailOutputAsJsonString = objectMapper.writeValueAsString(userDetailOutput);

            String userProfileJsonString = objectMapper.writeValueAsString(userDetailOutput);
            JsonNode userProfileJsonNode = objectMapper.readTree(userProfileJsonString);
            jsonList.add(userProfileJsonNode);
              //System.out.println("total Stock price " +userDetailOutput.getNetWorth());
        }

        //sonElement json = gson.toJsonTree(userDetailOutput);
       // System.out.println("json is "+json.toString());
        //return json.toString();

        return jsonList;

    }

    public List<JsonNode> listStocks() throws StocksNotFoundException, IOException {

        Iterable<StocksList> allStocks = stockListRepository.findAll();
        StocksList stocksList=new StocksList();
        List<StocksList> list=new ArrayList<StocksList>();
        ObjectMapper objectMapper = new ObjectMapper();
     // System.out.println("in list Stocks ");
        //Gson gson = new Gson();
         List<JsonNode> jsonList=new ArrayList<JsonNode>();
         for(StocksList stocks: allStocks){
             stocksList.setRegion(stocks.getRegion());
             stocksList.setCompany_name(stocks.getCompany_name());
             stocksList.setStock_type(stocks.getStock_type());
             stocksList.setSymbol(stocks.getSymbol());


             String stocksListJsonString = objectMapper. writeValueAsString(stocksList);
             JsonNode jsonNode = objectMapper.readTree(stocksListJsonString);
             jsonList.add(jsonNode);

             //    JsonElement json = gson.toJsonTree(stocksList);
           //  JsonObject convertedObject = new Gson().fromJson(json.toString(), JsonObject.class);
          //   System.out.println("converted object "+ convertedObject);
              // jsonList.add(convertedObject);
             //jsonList.add(json.toString());


         }
         if(jsonList==null) {
             throw new StocksNotFoundException("No Stocks Found");
         }

        return jsonList;
    }


    public List<JsonNode> listMyStocks(String username) throws IOException {

        List<UserShare> findByUsernameList = userShareRepository.findByUsername(username);
        UserShare userShare=new UserShare();
        List<UserShare> list=new ArrayList<UserShare>();
        ObjectMapper objectMapper = new ObjectMapper();
       // System.out.println("in list Stocks ");
        //Gson gson = new Gson();
        List<JsonNode> jsonList=new ArrayList<JsonNode>();

        for(UserShare userShares:findByUsernameList)
        {

              userShare.setQuantity(userShares.getQuantity());
              userShare.setCompany(userShares.getCompany());
              userShare.setSymbol(userShares.getSymbol());
              list.add(userShare);

            String stocksListJsonString = objectMapper.writeValueAsString(userShare);
            JsonNode jsonNode = objectMapper.readTree(stocksListJsonString);
            jsonList.add(jsonNode);

        }
        if(jsonList==null) {
            throw new StocksNotFoundException("No Stocks Found");
        }


       return jsonList;


    }


    public double currentValue(String symbol){
        String output = "";
        double stockValue;

            final String uri = "http://localhost:9093/currentValue/"+symbol;

            RestTemplate restTemplate = new RestTemplate();
             stockValue = restTemplate.getForObject(uri, Double.class);


        //System.out.println("stock value "+stockValue);
        return stockValue;
    }


    public JsonNode getUserInfo(String username) throws IOException {
        UserRegistry userRegistry = registrationRepository.findByUsername(username);
          UserRegistry userRegistry1 =new UserRegistry();
          userRegistry1.setAddress(userRegistry.getAddress());
          userRegistry1.setEmail(userRegistry.getEmail());
          userRegistry1.setUsername(userRegistry.getUsername());
        ObjectMapper objectMapper = new ObjectMapper();

        String userRegistryJsonString = objectMapper.writeValueAsString(userRegistry1);
        JsonNode jsonNode = objectMapper.readTree(userRegistryJsonString);

        return  jsonNode;
    }
}
