package bg.propertymanager.web;

import bg.propertymanager.service.BuildingService;
import bg.propertymanager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String index(){
        return "index";
    }
    @GetMapping("/home")
    public String home(Model model, Principal principal){
        model.addAttribute("buildings",buildingService.findAll());
        model.addAttribute("currentUser",userService.findUserByUsername(principal.getName()));
        return "home";
    }
    @GetMapping("/contact-us")
    public String contactUs(){
        return "coming-soon";
    }
    @GetMapping("/pricing-and-plans")
    public String pricingAndPlan(){
        return "coming-soon";
    }

}
