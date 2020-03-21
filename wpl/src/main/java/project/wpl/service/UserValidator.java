package project.wpl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import project.wpl.model.UserRegistry;
import project.wpl.repository.RegistrationRepository;

@Component
public class UserValidator implements Validator {
   @Autowired
    RegistrationRepository registrationRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserRegistry.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        //System.out.println("Validating");
        UserRegistry user = (UserRegistry) o;
       // System.out.println("after");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue("username","Size.userForm.username");
           // errors.rejectValue("errorcode", "Size.userForm.username");
            //System.out.println("in username");
            return;
        }
        if (registrationRepository.findByUsername(user.getUsername()) != null) {
            errors.rejectValue( "username","Duplicate.userForm.username");
            //System.out.println("in username2");
            return;
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwd", "NotEmpty");
        if (user.getPasswd().length() < 8 || user.getPasswd().length() > 32) {
            errors.rejectValue("passwd","Size.userForm.password");
            //System.out.println("in passwd");
            return;
        }

        if (!user.getPasswordConfirm().equals(user.getPasswd())) {
            errors.reject( "passwordConfirm","Diff.userForm.passwordConfirm");
           // System.out.println("in passwd2");
            return;
        }
    }
}
