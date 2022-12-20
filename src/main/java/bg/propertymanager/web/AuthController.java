package bg.propertymanager.web;

import bg.propertymanager.model.dto.UserRegisterDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.repository.UserRepository;
import bg.propertymanager.service.UserService;
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
    private final UserRepository userRepository;

    public AuthController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "auth-login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegisterDTO")) {
            model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        }
        return "auth-register";
    }

    @PostMapping("/register")
    public String registerConfirm(@Valid @ModelAttribute("user") UserRegisterDTO userRegisterDTO,
                                  BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                  Model model) {
        UserEntity existingUser = userService.findUserByUsername(userRegisterDTO.getUsername());
        if (existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty()) {
            bindingResult.rejectValue("username", null, "There is already an account registered with the same username");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegisterDTO", userRegisterDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDTO", bindingResult);
            return "redirect:register";
        }
        userService.register(userRegisterDTO);
        return "redirect:login";
    }

    private boolean isPasswordLikeConfirmPassword(UserRegisterDTO userRegisterDTO) {
        return userRegisterDTO.getPassword().equals(userRegisterDTO.getMatchingPassword());
    }

}
