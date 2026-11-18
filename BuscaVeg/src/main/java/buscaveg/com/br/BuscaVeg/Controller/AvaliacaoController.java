package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import buscaveg.com.br.BuscaVeg.Entity.PratoCardapio;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.AvaliacaoService;

@Controller
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public String avaliarPrato(
            @RequestParam(required = false) Long pratoId,
            Principal principal,
            Authentication authentication,
            Model model) {
        boolean ehUsuario = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USUARIO"));
        model.addAttribute("ehUsuario", ehUsuario);
        if (pratoId != null) {
            PratoCardapio prato = avaliacaoService.buscarPrato(pratoId);
            model.addAttribute("prato", prato);
            model.addAttribute("notaAtual", ehUsuario
                    ? avaliacaoService.buscarNotaAtual(pratoId, principal.getName())
                    : null);
            model.addAttribute("resumo",
                    avaliacaoService.resumirPratos(java.util.List.of(prato)).get(pratoId));
        }
        return "avaliacoes";
    }

    @PostMapping
    public String salvarAvaliacao(
            @RequestParam Long pratoId,
            @RequestParam Integer nota,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Long empresarialId = avaliacaoService.avaliar(
                    pratoId, principal.getName(), nota);
            redirectAttributes.addFlashAttribute("sucesso", "Sua avaliação foi salva.");
            return "redirect:/cardapio/" + empresarialId;
        } catch (RegraDeNegocioException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/avaliacoes?pratoId=" + pratoId;
        }
    }
}
