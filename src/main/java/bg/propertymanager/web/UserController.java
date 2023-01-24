package bg.propertymanager.web;

import bg.propertymanager.model.dto.user.PasswordChangeDTO;
import bg.propertymanager.model.dto.user.UserEditDTO;
import bg.propertymanager.model.entity.UserEntity;
import bg.propertymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@SuppressWarnings("ConstantConditions")
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        if (!model.containsAttribute("userEditDTO")) {
            model.addAttribute("userEditDTO", new UserEditDTO());
        }
        UserEntity userProfileView = userService.findUserByUsername(name);
        ModelAndView mav = new ModelAndView("edit-profile");
        mav.addObject("user", userProfileView);
        return mav;
    }

    @PreAuthorize("#name == principal.username")
    @PostMapping("/profile/edit/{name}")
    public String editProfileConfirm(@Valid UserEditDTO userEditDTO,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @PathVariable("name") String name,
                                     Principal principal) {
        userEditDTO.setId(userService.
                findUserByUsername(principal.getName())
                .getId());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userEditDTO", userEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userEditDTO", bindingResult);
            return "redirect:/users/profile/edit/" + principal.getName();
        }
        userService.updateProfile(userEditDTO);
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
        if (!userService.checkIfOldPasswordIsCorrect(passwordChangeDTO, principal)) {
            bindingResult.rejectValue("oldPassword", null, "Old password is not correct");
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
}
