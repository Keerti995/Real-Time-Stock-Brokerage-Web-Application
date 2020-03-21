package project.wpl.service;

import java.util.*;
import javax.validation.Valid;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.wpl.constants.Constants;
import project.wpl.exception.InputValidationException;
import project.wpl.exception.ResourceNotFoundException;
import project.wpl.model.BankAccount;
import project.wpl.model.UserRegistry;
import project.wpl.model.UserShare;
import project.wpl.repository.BankAccountRepository;
import project.wpl.repository.RegistrationRepository;
import project.wpl.repository.RoleRepository;
import project.wpl.repository.UserShareRepository;

@Service
public class UserRegistryServiceImpl {


  @Autowired
  private RegistrationRepository registrationRepository;

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserShareRepository userShareRepository;

  public void createNewUser(@Valid UserRegistry userRegistry) {
    validate(userRegistry);
    //System.out.println("in service");
    userRegistry.setPasswd(bCryptPasswordEncoder.encode(userRegistry.getPasswd()));
    userRegistry.setRoles(new HashSet<>(roleRepository.findAll()));
    //System.out.println("Saving user");
    registrationRepository.save(userRegistry);
  }

  public void validate(@Valid UserRegistry userRegistry) throws InputValidationException {
    if (!userRegistry.getPasswordConfirm().equals(userRegistry.getPasswd())) {
      throw new InputValidationException("This password doesn't match with the above passsword");
    }
    if (!Constants.set.contains(userRegistry.getSecurity_qn())) {
      throw new InputValidationException(
          "Security question doesn't match with the available security quesitons");
    }
  }



  public JSONObject validateSecurityQuestion(@Valid UserRegistry userRegistry)
      throws InputValidationException {

    // HashMap<String, String> map = new HashMap<String, String>();
    JSONObject entity = new JSONObject();
    Optional<UserRegistry> findByIdResult =
        registrationRepository.findById(userRegistry.getUsername());

    if (!findByIdResult.get().getSecurity_qn().equalsIgnoreCase(userRegistry.getSecurity_qn())) {
      entity.put("status", "invalidquestion");
    } else if (!findByIdResult.get().getSecurity_qn_ans()
        .equalsIgnoreCase(userRegistry.getSecurity_qn_ans())) {
      entity.put("status", "invalidanswer");
    } else {
      entity.put("status", "success");
    }

    return entity;

  }


  public void passwordReset(@Valid UserRegistry userRegistry) throws InputValidationException {
    // TODO Auto-generated method stub

    if (!userRegistry.getPasswd().equals(userRegistry.getPasswordConfirm())) {
      throw new InputValidationException("This password doesn't match with the above passsword");
    } else {
      Optional<UserRegistry> findByIdResult =
          registrationRepository.findById(userRegistry.getUsername());
      if (findByIdResult.isPresent()) {
        findByIdResult.get().setPasswd(bCryptPasswordEncoder.encode(userRegistry.getPasswd()));
        findByIdResult.get().setPasswordConfirm(bCryptPasswordEncoder.encode(userRegistry.getPasswd()));
        /*findByIdResult.get()
            .setPasswd(Base64.getEncoder().encodeToString(userRegistry.getPasswd().getBytes()));
        findByIdResult.get().setPasswordConfirm(
            Base64.getEncoder().encodeToString(userRegistry.getPasswd().getBytes()));*/
        registrationRepository.save(findByIdResult.get());
      } else {
        throw new ResourceNotFoundException("Username not found");
      }

    }


  }

  public UserRegistry findByUsername(String username) {
    return registrationRepository.findByUsername(username);
  }



}
