package bg.propertymanager.web;

import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController {
    private final BuildingService buildingService;
    private final UserService userService;

    public HomeController(BuildingService buildingService, UserService userService) {
        this.buildingService = buildingService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView home(Principal principal) {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("buildings", buildingService.findAll());
        if (principal != null) {
            mav.addObject("currentUser", userService.findUserByUsername(principal.getName()));
            mav.addObject("loggedUser", true);
        } else {
            mav.addObject("loggedUser", false);
        }
        return mav;
    }

    @GetMapping("/contact-us")
    public String contactUs() {
        return "coming-soon";
    }

    @GetMapping("/pricing-and-plans")
    public String pricingAndPlan() {
        return "coming-soon";
    }

}
