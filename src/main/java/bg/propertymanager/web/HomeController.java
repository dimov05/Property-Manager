package bg.propertymanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/home")
    public String home(){
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
