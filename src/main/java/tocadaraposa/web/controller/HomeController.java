package tocadaraposa.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings("unused")
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(){
        return "index";
    }

}