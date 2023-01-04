package bg.propertymanager.web;

import bg.propertymanager.model.dto.user.UserRegisterDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth-login";
    }

    @PostMapping("/login-error")
    public String onFailedLogin(@ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("bad_credentials", true);

        return "redirect:/users/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegisterDTO")) {
            model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        }
        return "auth-register";
    }

    @PostMapping("/register")
    public String registerConfirm(@Valid UserRegisterDTO userRegisterDTO,
                                  BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        UserEntity existingUser = userService.findUserByUsername(userRegisterDTO.getUsername());
        if (alreadyRegisteredUsername(existingUser)) {
            bindingResult.rejectValue("username", null, "There is already an account registered with the same username");
            redirectAttributes.addFlashAttribute("sameUser", true);
        }
        if (passwordsNotMatching(userRegisterDTO)) {
            bindingResult.rejectValue("matchingPassword", null, "Passwords must match");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegisterDTO", userRegisterDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDTO", bindingResult);
            return "redirect:register";
        }
        userService.register(userRegisterDTO);
        return "redirect:login";
    }

    private static boolean alreadyRegisteredUsername(UserEntity existingUser) {
        return existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty();
    }

    private static boolean passwordsNotMatching(UserRegisterDTO userRegisterDTO) {
        return !userRegisterDTO.getPassword().equals(userRegisterDTO.getMatchingPassword());
    }
}
