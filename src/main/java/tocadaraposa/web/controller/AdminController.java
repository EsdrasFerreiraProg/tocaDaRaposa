package tocadaraposa.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@SuppressWarnings("unused")
public class AdminController {

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping({"/login-error"})
    public String loginError(ModelMap map){
        map.addAttribute("alerta", "erro");
        map.addAttribute("titulo", "Credenciais Inválidas!");
        map.addAttribute("texto", "Login ou senha incorretos, tente novamente.");
        return "admin/login";
    }

}
