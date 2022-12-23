package bg.propertymanager.web;

import bg.propertymanager.model.dto.EditProfileDTO;
import bg.propertymanager.model.dto.PasswordChangeDTO;
import bg.propertymanager.model.dto.UserRegisterDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("#name == principal.username")
    @GetMapping("/profile/{name}")
    public ModelAndView viewProfile(@PathVariable("name") String name) {
        UserEntity userProfileView = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("view-profile");
        mav.addObject("user", userProfileView);
        return mav;
    }

    @PreAuthorize("#name == principal.username")
    @GetMapping("/profile/edit/{name}")
    public ModelAndView editProfile(@PathVariable("name") String name, Model model) {
        if (!model.containsAttribute("editProfileDTO")) {
            model.addAttribute("editProfileDTO", new EditProfileDTO());
        }
        UserEntity userProfileView = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("edit-profile");
        mav.addObject("user", userProfileView);
        return mav;
    }

    @PreAuthorize("#name == principal.username")
    @PostMapping("/profile/edit/{name}")
    public String editProfileConfirm(@Valid EditProfileDTO editProfileDTO,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @PathVariable("name") String name,
                                     Principal principal) {
        editProfileDTO.setId(userService.
                findUserByUsername(principal.getName())
                .getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("editProfileDTO", editProfileDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editProfileDTO", bindingResult);
            return "redirect:/users/profile/edit/" + principal.getName();
        }
        userService.updateProfile(editProfileDTO);
        return "redirect:/users/profile/" + principal.getName();
    }


    @PreAuthorize("#name == principal.username")
    @GetMapping("/profile/change-password/{name}")
    public ModelAndView changePassword(@PathVariable("name") String name, Model model) {
        if (!model.containsAttribute("passwordChangeDTO")) {
            model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
        }
        UserEntity userProfileView = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("change-password");
        mav.addObject("user", userProfileView);
        return mav;
    }

    @PreAuthorize("#name == principal.username")
    @PostMapping("/profile/change-password/{name}")
    public String changePasswordConfirm(@Valid PasswordChangeDTO passwordChangeDTO,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        @PathVariable("name") String name,
                                        Principal principal) {
        passwordChangeDTO.setId(userService.
                findUserByUsername(principal.getName())
                .getId());
        if(!userService.checkIfOldPasswordIsCorrect(passwordChangeDTO,principal)){
            bindingResult.rejectValue("oldPassword",null,"Old password is not correct");
        }
        if (passwordsNotMatching(passwordChangeDTO)) {
            bindingResult.rejectValue("matchingNewPassword", null, "Passwords must match");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("passwordChangeDTO", passwordChangeDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChangeDTO", bindingResult);
            return "redirect:/users/profile/change-password/" + principal.getName();
        }
        userService.updatePassword(passwordChangeDTO);
        return "redirect:/users/profile/" + principal.getName();
    }


    private static boolean passwordsNotMatching(PasswordChangeDTO passwordChangeDTO) {
        return !passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getMatchingNewPassword());
    }

    private static boolean alreadyRegisteredUsername(UserEntity existingUser) {
        return existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty();
    }

    private static boolean passwordsNotMatching(UserRegisterDTO userRegisterDTO) {
        return !userRegisterDTO.getPassword().equals(userRegisterDTO.getMatchingPassword());
    }
}
