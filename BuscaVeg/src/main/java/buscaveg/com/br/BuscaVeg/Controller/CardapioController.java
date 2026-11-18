package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import buscaveg.com.br.BuscaVeg.DTO.PratoCardapioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Entity.PratoCardapio;
import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import buscaveg.com.br.BuscaVeg.Enum.EnumTagAlimentar;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.AvaliacaoService;
import buscaveg.com.br.BuscaVeg.Service.CardapioService;
import buscaveg.com.br.BuscaVeg.Service.EmpresarialService;
import buscaveg.com.br.BuscaVeg.Service.UsuarioService;

@Controller
@RequestMapping("/cardapio")
public class CardapioController {

    private final CardapioService cardapioService;
    private final AvaliacaoService avaliacaoService;
    private final EmpresarialService empresarialService;
    private final UsuarioService usuarioService;

    public CardapioController(
            CardapioService cardapioService,
            AvaliacaoService avaliacaoService,
            EmpresarialService empresarialService,
            UsuarioService usuarioService) {
        this.cardapioService = cardapioService;
        this.avaliacaoService = avaliacaoService;
        this.empresarialService = empresarialService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{empresarialId:\\d+}")
    public String verCardapio(
            @PathVariable Long empresarialId,
            Authentication authentication,
            Model model) {
        Empresarial empresa = empresarialService.buscarEmpresarialPorId(empresarialId);
        boolean ehDono = empresa.getConta().getEmail().equals(authentication.getName());
        boolean ehUsuario = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USUARIO"));
        boolean ehEmpresa = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_EMPRESA"));
        EnumPerfilAlimentar perfil = ehUsuario
                        ? usuarioService.buscarPorEmailDaConta(authentication.getName()).getPerfilAlimentar()
                        : null;

        model.addAttribute("empresa", empresa);
        model.addAttribute("ehDono", ehDono);
        model.addAttribute("ehUsuario", ehUsuario);
        model.addAttribute("ehEmpresa", ehEmpresa);
        model.addAttribute("perfilAlimentar", perfil);
        model.addAttribute("tagsAlimentares", EnumTagAlimentar.values());
        model.addAttribute("prato", new PratoCardapioDTO());
        List<PratoCardapio> pratos = ehDono
                ? cardapioService.listarTodos(empresarialId, authentication.getName())
                : cardapioService.listarPratosDisponiveis(empresarialId, perfil);
        model.addAttribute("pratos", pratos);
        model.addAttribute("resumosAvaliacoes", avaliacaoService.resumirPratos(pratos));
        return "cardapio";
    }

    @GetMapping("/empresa")
    public String cardapioDaEmpresa(Principal principal) {
        Empresarial empresa = empresarialService.buscarPorEmailDaConta(principal.getName());
        return "redirect:/cardapio/" + empresa.getId();
    }

    @PostMapping("/{empresarialId:\\d+}/pratos")
    public String adicionarPrato(
            @PathVariable Long empresarialId,
            @ModelAttribute("prato") PratoCardapioDTO dto,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            cardapioService.adicionarPrato(empresarialId, principal.getName(), dto);
        } catch (RegraDeNegocioException e) {
            carregarPaginaDoDono(empresarialId, principal, model);
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("formAberto", true);
            return "cardapio";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Prato adicionado ao cardápio.");
        return "redirect:/cardapio/" + empresarialId;
    }

    @PostMapping("/{empresarialId:\\d+}/pratos/{pratoId}/disponibilidade")
    public String alternarDisponibilidade(
            @PathVariable Long empresarialId,
            @PathVariable Long pratoId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        cardapioService.alternarDisponibilidade(empresarialId, pratoId, principal.getName());
        redirectAttributes.addFlashAttribute("sucesso", "Disponibilidade do prato atualizada.");
        return "redirect:/cardapio/" + empresarialId;
    }

    @PostMapping("/{empresarialId:\\d+}/pratos/{pratoId}/remover")
    public String removerPrato(
            @PathVariable Long empresarialId,
            @PathVariable Long pratoId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        cardapioService.removerPrato(empresarialId, pratoId, principal.getName());
        redirectAttributes.addFlashAttribute("sucesso", "Prato removido do cardápio.");
        return "redirect:/cardapio/" + empresarialId;
    }

    private void carregarPaginaDoDono(Long empresarialId, Principal principal, Model model) {
        Empresarial empresa = empresarialService.buscarEmpresaDoLogado(
                empresarialId, principal.getName());
        model.addAttribute("empresa", empresa);
        model.addAttribute("ehDono", true);
        model.addAttribute("ehUsuario", false);
        model.addAttribute("ehEmpresa", true);
        model.addAttribute("perfilAlimentar", null);
        model.addAttribute("tagsAlimentares", EnumTagAlimentar.values());
        List<PratoCardapio> pratos = cardapioService.listarTodos(
                empresarialId, principal.getName());
        model.addAttribute("pratos", pratos);
        model.addAttribute("resumosAvaliacoes", avaliacaoService.resumirPratos(pratos));
    }
}
