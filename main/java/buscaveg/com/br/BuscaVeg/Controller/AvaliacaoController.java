package buscaveg.com.br.BuscaVeg.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import buscaveg.com.br.BuscaVeg.DTO.AvaliacaoDTO;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.AvaliacaoService;
import buscaveg.com.br.BuscaVeg.Service.UsuarioService;
import buscaveg.com.br.BuscaVeg.Service.EmpresarialService;

import java.security.Principal;

/**
 * CONTROLLER de AVALIAÇÕES.
 * 
 * Gerencia as telas e requisições relacionadas a avaliações de estabelecimentos.
 */
@Controller
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final UsuarioService usuarioService;
    private final EmpresarialService empresarialService;

    public AvaliacaoController(
            AvaliacaoService avaliacaoService,
            UsuarioService usuarioService,
            EmpresarialService empresarialService) {
        this.avaliacaoService = avaliacaoService;
        this.usuarioService = usuarioService;
        this.empresarialService = empresarialService;
    }

    /**
     * GET /avaliacoes
     * Exibe a página de avaliações com formulário para deixar uma avaliação
     * e lista de avaliações recentes.
     */
    @GetMapping
    public String telaAvaliacoes(
            Principal principal,
            Model model) {

        try {
            // Busca o usuário logado
            Usuario usuario = usuarioService.buscarPorEmail(principal.getName());

            // Envia para a tela:
            // - Um DTO vazio para o formulário de avaliação
            model.addAttribute("avaliacao", new AvaliacaoDTO());

            // - Lista de todos os estabelecimentos (para o select)
            model.addAttribute("empresasVisitadas", empresarialService.listarTodasEmpresas());

            // - Lista de todas as avaliações recentes
            model.addAttribute("avaliacoes", avaliacaoService.listarTodasAvaliacoes());

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar página de avaliações.");
        }

        return "avaliacoes"; // templates/avaliacoes.html
    }

    /**
     * POST /avaliacoes/salvar
     * Recebe os dados do formulário e salva a avaliação.
     * 
     * @ModelAttribute("avaliacao") pega os campos do formulário e preenche o DTO
     */
    @PostMapping("/salvar")
    public String salvarAvaliacao(
            @ModelAttribute("avaliacao") AvaliacaoDTO dto,
            Principal principal,
            Model model) {

        try {
            // Busca o usuário logado
            Usuario usuario = usuarioService.buscarPorEmail(principal.getName());

            // Delega ao Service o trabalho de salvar e recalcular índice
            avaliacaoService.salvarAvaliacao(usuario, dto);

            // Sucesso: redireciona com mensagem
            return "redirect:/avaliacoes?sucesso=true";

        } catch (RegraDeNegocioException e) {
            // Erro de validação: volta ao formulário
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("avaliacao", dto);
            model.addAttribute("empresasVisitadas", empresarialService.listarTodasEmpresas());
            model.addAttribute("avaliacoes", avaliacaoService.listarTodasAvaliacoes());
            return "avaliacoes";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao publicar avaliação. Tente novamente.");
            model.addAttribute("avaliacao", dto);
            model.addAttribute("empresasVisitadas", empresarialService.listarTodasEmpresas());
            model.addAttribute("avaliacoes", avaliacaoService.listarTodasAvaliacoes());
            return "avaliacoes";
        }
    }
}
