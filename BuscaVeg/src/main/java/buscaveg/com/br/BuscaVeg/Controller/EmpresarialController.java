package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import buscaveg.com.br.BuscaVeg.DTO.AlterarEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.DTO.CadastroEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.FavoritoService;
import buscaveg.com.br.BuscaVeg.Service.EmpresarialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/empresarialCTR")
public class EmpresarialController {

    private final EmpresarialService oEmpresarialService;
    private final FavoritoService favoritoService;

    public EmpresarialController(
            EmpresarialService oEmpresarialService,
            FavoritoService favoritoService) {

        this.oEmpresarialService = oEmpresarialService;
        this.favoritoService = favoritoService;
    }

    // GET /empresarialCTR/formCadastrarEmpresa
    // Tela de cadastro (pública). Envia um DTO vazio para o formulário.
    @GetMapping("/formCadastrarEmpresa")
    public String telaCadastrarEmpresa(Model oModel) {

        oModel.addAttribute("empresarial", new CadastroEmpresarialDTO());

        return "cadastroEmp";
    }

  
    @PostMapping("/salvarEmpresa")
    public String cadastrarEmpresa(
            @ModelAttribute("empresarial") CadastroEmpresarialDTO dto,
            Model oModel) {

        try {
            oEmpresarialService.cadastrarEmpresa(dto);

        } catch (RegraDeNegocioException e) {
            // Mesmo tratamento do cadastro de usuário: volta ao formulário
            // com a mensagem de erro.
            oModel.addAttribute("erro", e.getMessage());
            return "cadastroEmp";
        }

        return "redirect:/usuarioCTR/login?cadastrado";
    }

    // GET /empresarialCTR/listarEmpresas
    // Lista todas as empresas (qualquer pessoa logada pode ver).
    @GetMapping("/listarEmpresas")
    public String listarEmpresas(
            Authentication authentication,
            @RequestParam(required = false) String busca,
            Model oModel) {

        var empresas = oEmpresarialService.listarTodasEmpresas();
        if (busca != null && !busca.isBlank()) {
            String termo = busca.trim().toLowerCase(java.util.Locale.ROOT);
            empresas = empresas.stream()
                    .filter(empresa -> empresa.getNomeFantasia().toLowerCase(java.util.Locale.ROOT).contains(termo)
                            || empresa.getEndereco().toLowerCase(java.util.Locale.ROOT).contains(termo)
                            || empresa.getTipoEspecializacao().name().toLowerCase(java.util.Locale.ROOT).contains(termo))
                    .toList();
        }
        oModel.addAttribute(
                "empresas",
                empresas
        );
        oModel.addAttribute("busca", busca);
        oModel.addAttribute("favoritosIds", favoritoService.listarIdsFavoritos(authentication.getName()));
        oModel.addAttribute("ehUsuario", authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USUARIO")));

        return "lugares"; // templates/listarEmpresas.html
    }

    // GET /empresarialCTR/minhaEmpresa
    // Atalho para o perfil da empresa vinculada à conta autenticada.
    @GetMapping("/minhaEmpresa")
    public String minhaEmpresa() {
        return "redirect:/empresarialCTR/perfil";
    }

    @GetMapping("/perfil")
    public String perfilEmpresa(Principal principal, Model model) {
        Empresarial empresa = oEmpresarialService.buscarPorEmailDaConta(principal.getName());
        model.addAttribute("empresa", empresa);
        model.addAttribute("email", principal.getName());
        return "perfilEMP";
    }

    // GET /empresarialCTR/editarEmpresa/{id}
    // Abre o formulário de edição já preenchido. {id} vem da própria URL
    // e é capturado por @PathVariable.
    @GetMapping("/editarEmpresa/{id}")
    public String telaEditarEmpresa(
            @PathVariable Long id,
            Principal principal,
            Model oModel) {

        // Se a empresa não for da pessoa logada, o Service nega o acesso (403).
        Empresarial empresa =
                oEmpresarialService.buscarEmpresaDoLogado(id, principal.getName());

        // O formulário precisa do id para montar o th:action="@{/empresarialCTR/alterarEmpresa/{id}(id=${empresaId})}".
        oModel.addAttribute("empresaId", empresa.getId());
        oModel.addAttribute(
                "empresarial",
                oEmpresarialService.montarDtoDeEdicao(empresa)
        );
        oModel.addAttribute("empresa", empresa);
        oModel.addAttribute("tiposEstabelecimento", EnumTipoEstabelecimento.values());

        return "editaEmpresa";
    }

    // POST /empresarialCTR/alterarEmpresa/{id}
    // Recebe o formulário de edição e salva as alterações.
    @PostMapping("/alterarEmpresa/{id}")
    public String alterarEmpresa(
            @PathVariable Long id,
            @ModelAttribute("empresarial") AlterarEmpresarialDTO dto,
            Principal principal,
            Model oModel) {

        try {
            oEmpresarialService.alterarEmpresa(id, dto, principal.getName());

        } catch (RegraDeNegocioException e) {
            oModel.addAttribute("erro", e.getMessage());
            oModel.addAttribute("empresaId", id);
            Empresarial empresa = oEmpresarialService.buscarEmpresaDoLogado(id, principal.getName());
            oModel.addAttribute("empresa", empresa);
            oModel.addAttribute("tiposEstabelecimento", EnumTipoEstabelecimento.values());
            return "editaEmpresa";
        }

        return "redirect:/empresarialCTR/perfil?atualizado";
    }

    // POST /empresarialCTR/deletarEmpresa/{id}
    // Apaga a empresa e a conta dela.
    // IMPORTANTE: é POST, não GET. Um link GET que apaga dados pode ser
    // disparado sem querer (por exemplo, por um robô ou um site malicioso).
    // Na tela, use um <form method="post" th:action="..."> com botão.
    @PostMapping("/deletarEmpresa/{id}")
    public String deletarEmpresa(
            @PathVariable Long id,
            Principal principal,
            HttpServletRequest request) throws ServletException {

        oEmpresarialService.deletarEmpresa(id, principal.getName());

        // A conta acabou de ser apagada: encerra a sessão para a pessoa não
        // continuar "logada" com uma conta que não existe mais.
        request.logout();

        return "redirect:/usuarioCTR/login?logout";
    }
}
