package bg.propertymanager.web;

import bg.propertymanager.model.view.AdminViewUserProfile;
import bg.propertymanager.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ModelAndView showUsers() {
        ModelAndView mav = new ModelAndView("manage-users");
        List<AdminViewUserProfile> usersList = userService.findAll();
        mav.addObject("users", usersList);
        return mav;
    }
}
