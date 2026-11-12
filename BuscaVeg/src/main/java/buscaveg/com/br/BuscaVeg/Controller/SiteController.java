package buscaveg.com.br.BuscaVeg.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {

    @GetMapping({"/", "/site"})
    public String inicio(Authentication authentication, Model model) {
        model.addAttribute("ehUsuario", authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USUARIO")));
        return "site";
    }
}
