package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import buscaveg.com.br.BuscaVeg.DTO.AlterarEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.DTO.CadastroEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.EmpresarialService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/empresarialCTR")
public class EmpresarialController {

    private final EmpresarialService oEmpresarialService;

    public EmpresarialController(
            EmpresarialService oEmpresarialService) {

        this.oEmpresarialService = oEmpresarialService;
    }

   
    @GetMapping("/formCadastrarEmpresa")
    public String telaCadastrarEmpresa(Model oModel) {

        oModel.addAttribute("empresarial", new CadastroEmpresarialDTO());

        return "cadastro"; // templates/cadastrarEmpresa.html
    }

    
    @PostMapping("/salvarEmpresa")
    public String cadastrarEmpresa(
            @ModelAttribute("empresarial") CadastroEmpresarialDTO dto,
            Model oModel) {

        try {
            oEmpresarialService.cadastrarEmpresa(dto);

        } catch (RegraDeNegocioException e) {
            
            oModel.addAttribute("erro", e.getMessage());
            return "cadastro";
        }

        return "redirect:/usuarioCTR/login?cadastrado";
    }

    
    @GetMapping("/listarEmpresas")
    public String listarEmpresas(Model oModel) {

        oModel.addAttribute(
                "empresas",
                oEmpresarialService.listarTodasEmpresas()
        );

        return "lugares"; // templates/listarEmpresas.html
    }

    // GET /empresarialCTR/minhaEmpresa
    // Atalho para a empresa logada: descobre qual é a empresa dela pelo
    // e-mail e redireciona para a tela de edição. É para cá que o login
    // leva as contas do tipo EMPRESA.
    @GetMapping("/minhaEmpresa")
    public String minhaEmpresa(Principal principal) {

        Empresarial empresa =
                oEmpresarialService.buscarPorEmailDaConta(principal.getName());

        return "redirect:/empresarialCTR/editarEmpresa/" + empresa.getId();
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

        return "editarEmpresa"; // templates/editarEmpresa.html
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
            return "editarEmpresa";
        }

        return "redirect:/empresarialCTR/listarEmpresas";
    }

    // POST /empresarialCTR/detarEmpresa/{id}
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

        return "redirect:/usuarioCTR/login";
    }
}
