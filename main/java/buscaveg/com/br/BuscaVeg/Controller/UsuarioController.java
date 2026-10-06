package buscaveg.com.br.BuscaVeg.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import buscaveg.com.br.BuscaVeg.DTO.CadastroUsuarioDTO;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Service.UsuarioService;

@Controller
@RequestMapping("/usuarioCTR")
public class UsuarioController {

    private final UsuarioService oUsuarioService;

    
    public UsuarioController(UsuarioService oUsuarioService) {
        this.oUsuarioService = oUsuarioService;
    }

    // GET /usuarioCTR/formCadastrarUsuario
    // Exibe a tela de cadastro do usuário.
    // Enviamos um DTO vazio para a tela: o formulário HTML usa esse objeto
    // para saber quais campos preencher (nome, e-mail, senha, perfil...).
    @GetMapping("/cadastro")
    public String telaCadastraUsuario(Model oModel) {

        // Model é a "caixa" que leva dados do Java até o HTML.
        // Na tela, o objeto fica disponível com o nome "usuario".
        oModel.addAttribute("usuario", new CadastroUsuarioDTO());

        return "cadastrarUsuario"; // abre templates/cadastrarUsuario.html
    }

    // GET /usuarioCTR/login
    // Apenas exibe a tela de login. O processamento do login (conferir
    // e-mail e senha) NÃO é feito aqui: quem faz isso é o Spring Security,
    // configurado em SecurityConfig.
    // A tela pode receber ?error (login errado), ?logout (saiu) ou
    // ?cadastrado (acabou de se cadastrar) para exibir a mensagem adequada.
    @GetMapping("/login")
    public String telaLogin() {

        return "login"; // abre templates/login.html
    }

    // POST /usuarioCTR/salvarUsuario
    // Recebe os dados do formulário de cadastro.
    // @ModelAttribute pega os campos do formulário e preenche
    // automaticamente o DTO (pelos nomes dos campos).
    @PostMapping("/salvarUsuario")
    public String cadastrarUsuario(
            @ModelAttribute("usuario") CadastroUsuarioDTO dto,
            Model oModel) {

        try {
            // Delega ao Service o trabalho de salvar (Conta + Usuario).
            oUsuarioService.cadastrarUsuario(dto);

        } catch (RegraDeNegocioException e) {
            // Erro causado pelo preenchimento (e-mail repetido, campo vazio...):
            // volta para o MESMO formulário, mantendo o que a pessoa digitou,
            // e mostra a mensagem em "erro".
            oModel.addAttribute("erro", e.getMessage());
            return "cadastrarUsuario";
        }

        // Cadastro feito: leva para o login (padrão "Post/Redirect/Get":
        // evita reenviar o formulário se a pessoa apertar F5).
        return "redirect:/usuarioCTR/login?cadastrado";
    }
}
