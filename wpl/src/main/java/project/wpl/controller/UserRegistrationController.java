package project.wpl.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.wpl.exception.*;
//import project.wpl.kafka.Sender;
import project.wpl.kafka.Sender;
import project.wpl.model.BankAccount;
import project.wpl.model.BuyStock;
import project.wpl.model.TransferInfo;
import project.wpl.model.UserRegistry;
import project.wpl.repository.BankAccountRepository;
import project.wpl.service.UserDetailServiceImpl;
import project.wpl.service.UserRegistryServiceImpl;
import project.wpl.service.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UserRegistrationController {

  @Autowired
  private UserRegistryServiceImpl userRegistryServiceImpl;

  @Autowired
  private UserDetailServiceImpl userDetailService;

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserValidator userValidator;

  Cookie cookie;

 @Autowired
 Sender kafkaProducer;


  Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

  @PostMapping(value = "/userRegistration", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createDataSet(@Valid @RequestBody UserRegistry userRegistry,
                                      @RequestParam Map<String, String> params, BindingResult bindingResult) throws Exception {
    try {
      //System.out.println("Creating user");
      logger.info("Creating new user"+userRegistry.getUsername());
       userValidator.validate(userRegistry,bindingResult);
       if(bindingResult.hasErrors())
       {
         logger.error("Error registering user: Invalid username or password - username should be minimum of 6 characters and password should be minimum of 8 characters ");
         return new ResponseEntity("invalid username or password - username should be minimum of 6 characters and password should be minimum of 8 characters ",HttpStatus.FORBIDDEN);
       }
      userRegistryServiceImpl.createNewUser(userRegistry);
      logger.info("User Registration Successful "+userRegistry.getUsername());
    } catch (InputValidationException e) {
      logger.error(e.getMessage());
      return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
    // registrationRepository.findAll().forEach(x -> System.out.println(x));
    return new ResponseEntity("user registration Success", HttpStatus.ACCEPTED);
  }


  @PutMapping(value = "/updateUserInfo", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateDataSet(@Valid @RequestBody UserRegistry userRegistry,
      @RequestParam Map<String, String> params, HttpSession session) throws Exception {
    // System.out.println("size " + params.size());
    if(cookie == null){
      throw new SessionNotFoundException("User Not logged in");
    }
    String username = (String) session.getAttribute("username");

    try {
      if (username == null) {
        throw new SessionNotFoundException("User is Not logged in");
      }
      userDetailService.updateUserInformation(userRegistry, username);

      // System.out.println("size " + params.size());
    } catch (ResourceNotFoundException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    } catch (SessionNotFoundException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
    // registrationRepository.findAll().forEach(x -> System.out.println(x));
    return new ResponseEntity("update Info Success", HttpStatus.ACCEPTED);
  }



  @PostMapping(value = "/addBankAccount", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity addBankAccount(@Valid @RequestBody BankAccount bankAccount,
      @RequestParam Map<String, String> params, HttpSession session) throws Exception {
    if(cookie == null){
      throw new SessionNotFoundException("User Not logged in");
    }
    String username= cookie.getValue();
//    System.out.println("usernaem is "+username);
//    System.out.println("in add account");
//    System.out.println(username);
    try {

      if (username == null) {
        throw new SessionNotFoundException("User Not logged in");
      }
    } catch (SessionNotFoundException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
    userDetailService.createBankAccount(bankAccount, username);
   // System.out.println("username is addbannkaccount " + username);
    // registrationRepository.findAll().forEach(x -> System.out.println(x));
    return new ResponseEntity("Add Bank Account success", HttpStatus.ACCEPTED);
  }

  @PostMapping(value="/transferMoney", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity moneyTransfer(@Valid @RequestBody TransferInfo transferInfo,
                                      @RequestParam Map<String ,String> params,HttpSession session)
  {
     try {
         userDetailService.transferMoney(transferInfo);
//       System.out.println("transerInfo ---- "+transferInfo.getFromAccountNumber());
//       System.out.println("transfer info to " + transferInfo.getToAccounNumber());
//       System.out.println("balance "+ transferInfo.getAmountToTransfer());
     }
     catch(InsufficientFundsException e)
     {
       return new ResponseEntity(e.getMessage(),HttpStatus.FORBIDDEN);
     }
      return new ResponseEntity("sucesfully amount transferred",HttpStatus.ACCEPTED);
  }


  @PostMapping(value="/buy",consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity buyStock(@Valid @RequestBody BuyStock buyStock,@RequestParam Map<String,String> params,HttpSession session) throws InvalidAccountNumberException, InterruptedException {
    //String username = (String) session.getAttribute("username");
    if(cookie == null){
      throw new SessionNotFoundException("User Not logged in");
    }
    String username = cookie.getValue();
   // System.out.println("username in controller is " + username);
try {
    buyStock.setUsername(username);
    JsonElement json = new Gson().toJsonTree(buyStock);
    kafkaProducer.sendBuy(json.toString());

}
catch (InsufficientStocksException e)
{
    return  new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
}
     //userDetailService.stockBuy(buyStock);
     return new ResponseEntity("stock bought succesfully",HttpStatus.ACCEPTED);
  }




  @PostMapping(value="/sell",consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity sellStock(@Valid @RequestBody BuyStock buyStock,@RequestParam Map<String,String> params,HttpSession session)
  {
    //String username = (String) session.getAttribute("username");

    String username = cookie.getValue();
    buyStock.setUsername(username);
    try {
      //userDetailService.stockSell(buyStock);
        JsonElement json = new Gson().toJsonTree(buyStock);
        kafkaProducer.sendSell(json.toString());


    }
    catch(InsufficientStocksException e){
      return new ResponseEntity(e.getMessage(),HttpStatus.FORBIDDEN);
    }
      return new ResponseEntity("stock sold succesfully",HttpStatus.ACCEPTED);
  }


  @GetMapping(value="/listStocks", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<JsonNode>> getStocks()
  {
    List<JsonNode> jsonString= new ArrayList<>();

    try {
       jsonString = userDetailService.listStocks();

      // System.out.println( " in list stocks ");
//       for(int i=0;i<jsonString.size();i++)
//          System.out.println("json String is "+ jsonString.get(i));
    }
    catch(StocksNotFoundException | IOException e)
    {
         return  new ResponseEntity(e.getMessage(),HttpStatus.FORBIDDEN);
    }
   // System.out.println("list stock");
    return new ResponseEntity<List<JsonNode>>(jsonString,HttpStatus.ACCEPTED);
  }



  @GetMapping(value="/getUserInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JsonNode> getUserInfo() throws IOException {
    JsonNode jsonString;
    String username =cookie.getValue();

       jsonString = userDetailService.getUserInfo(username);

      //System.out.println( " in list stocks ");
//       for(int i=0;i<jsonString.size();i++)
//          System.out.println("json String is "+ jsonString.get(i));

    System.out.println("get user");
    return new ResponseEntity<JsonNode>(jsonString,HttpStatus.ACCEPTED);
  }


  @GetMapping(value="/listMyStocks", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<JsonNode>> getMyStocks()
  {
    List<JsonNode> jsonString= new ArrayList<>();
    String username =cookie.getValue();

    try {
      jsonString = userDetailService.listMyStocks(username);
     // System.out.println( " in list stocks ");
//       for(int i=0;i<jsonString.size();i++)
//          System.out.println("json String is "+ jsonString.get(i));
    }
    catch(StocksNotFoundException | IOException e)
    {
      return  new ResponseEntity(e.getMessage(),HttpStatus.FORBIDDEN);
    }
  //  System.out.println("list stock");
    return new ResponseEntity<List<JsonNode>>(jsonString,HttpStatus.ACCEPTED);
  }






  @GetMapping(value="/getUserProfileInfo")
  public ResponseEntity getUserProfileInfo(@RequestParam Map<String,String> params,HttpSession session) throws IOException {
    //String username = (String) session.getAttribute("username");
    if(cookie==null){
      throw new SessionNotFoundException("User Not logged in");
    }
    String username= cookie.getValue();
   // System.out.println("in user profile info");
    List<JsonNode> jsonNodeList =new ArrayList<JsonNode>();
    try {
      if (username == null) {
        throw new SessionNotFoundException("User Not logged in");
      }
    } catch (SessionNotFoundException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
    jsonNodeList=userDetailService.getProfileInfo(username);
    return new ResponseEntity(jsonNodeList,HttpStatus.ACCEPTED);
  }




  @PostMapping(value = "/forgotPassword", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity forgotPassword(@Valid @RequestBody UserRegistry userRegistry,
      @RequestParam Map<String, String> params) throws Exception {
    //System.out.println("in forgot Password");
    JSONObject entities = userRegistryServiceImpl.validateSecurityQuestion(userRegistry);
    if(entities.get("status").equals("success"))
      return new ResponseEntity<Object>("success", HttpStatus.OK);
    else
      return new ResponseEntity("invalid answer",HttpStatus.FORBIDDEN);
  }

  @PutMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity resetPassword(@Valid @RequestBody UserRegistry userRegistry,
      @RequestParam Map<String, String> params) throws Exception {
    userRegistryServiceImpl.passwordReset(userRegistry);
    return new ResponseEntity("reset password sucess", HttpStatus.OK);
  }




  @PostMapping("/login")
  public ResponseEntity login(@Valid @RequestBody UserRegistry userRegistry, HttpServletRequest request,
                              Principal principal) {
   // System.out.println("logging in");
    // System.out.println("principal user " + principal.getName());
    UserDetails userDetails = userDetailsService.loadUserByUsername(userRegistry.getUsername());

    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, userRegistry.getPasswd(),
            userDetails.getAuthorities());
   // System.out.println("Auth test");
    authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      // create session
      HttpSession session = request.getSession();
      session.setAttribute("username", userRegistry.getUsername());
      cookie = new Cookie("username",userRegistry.getUsername());
      cookie.getValue();
     // System.out.println("cookie value "+cookie.getValue());
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      //System.out.println(String.format("Auto login %s successfully!", userRegistry.getUsername()));
    } else
      return new ResponseEntity("username or password invalid",HttpStatus.FORBIDDEN);
    return new ResponseEntity(cookie.getValue(),HttpStatus.ACCEPTED);

  }



  @GetMapping(value = "/customlogout")
  public ResponseEntity logoutPage(HttpServletRequest request, HttpServletResponse response) {
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    HttpSession session = request.getSession(false);
    SecurityContextHolder.clearContext();
    //System.out.println("in logged out");
    session = request.getSession();
    cookie.setMaxAge(0);
    if (session != null) {
      session.invalidate();
      return new ResponseEntity("logged out",HttpStatus.ACCEPTED);
    }

    return new ResponseEntity("error",HttpStatus.FORBIDDEN);

  }

  @GetMapping({"/", "/welcome"})
  public String welcome() {
    return "welcome";
  }



  /*
   * @GetMapping("/findall") public List<UserRegistry> findAll(){
   * 
   * List<UserRegistry> userRegistries = registrationRepository.findAll(); return userRegistries; }
   */
}
