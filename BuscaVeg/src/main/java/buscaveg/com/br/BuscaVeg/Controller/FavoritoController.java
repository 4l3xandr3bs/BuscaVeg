package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import buscaveg.com.br.BuscaVeg.Service.FavoritoService;

@Controller
@RequestMapping("/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        model.addAttribute("empresas", favoritoService.listarFavoritos(principal.getName()));
        return "favoritos";
    }

    @PostMapping("/{empresarialId}")
    public String adicionar(@PathVariable Long empresarialId, Principal principal) {
        favoritoService.adicionarFavorito(principal.getName(), empresarialId);
        return "redirect:/empresarialCTR/listarEmpresas";
    }

    @PostMapping("/{empresarialId}/remover")
    public String remover(@PathVariable Long empresarialId, Principal principal) {
        favoritoService.removerFavorito(principal.getName(), empresarialId);
        return "redirect:/favoritos";
    }
}
