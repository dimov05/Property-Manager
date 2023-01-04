package bg.propertymanager.validator;

import bg.propertymanager.model.dto.user.UserRegisterDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserRegisterDTO user = (UserRegisterDTO) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
