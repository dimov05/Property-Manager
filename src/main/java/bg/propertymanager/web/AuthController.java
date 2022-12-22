package bg.propertymanager.web;

import bg.propertymanager.model.dto.UserRegisterDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.model.view.UserProfileView;
import bg.propertymanager.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService ) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        /*if(!model.containsAttribute("userLoginDTO")){
            model.addAttribute("userLoginDTO", new UserLoginDTO());
        }*/
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

    @GetMapping("/profile/{name}")
    public ModelAndView viewProfile(@PathVariable("name") String name, @AuthenticationPrincipal Principal principal) {
        UserEntity userProfileView = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("view-profile");
        mav.addObject("user", userProfileView);
        return mav;
    }

    private boolean isPasswordLikeConfirmPassword(UserRegisterDTO userRegisterDTO) {
        return userRegisterDTO.getPassword().equals(userRegisterDTO.getMatchingPassword());
    }

}
