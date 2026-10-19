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

/**
 * CAMADA CONTROLLER (a "porta de entrada" das requisições do navegador).
 *
 * Papel: receber o que a pessoa faz no site (abrir uma página, enviar um
 * formulário), chamar o Service para fazer o trabalho de verdade e devolver
 * qual TELA deve ser mostrada. O controller NÃO deve ter regra de negócio
 * nem falar direto com o banco.
 *
 * @Controller      -> o Spring registra esta classe como controlador web.
 * @RequestMapping  -> todas as URLs daqui começam com "/usuarioCTR".
 *                     Ex.: o método de login responde em "/usuarioCTR/login".
 *
 * Os textos retornados (ex.: "login") são nomes de arquivos HTML do
 * Thymeleaf, que devem estar em src/main/resources/templates/ (ex.: login.html).
 * Quem retorna "redirect:..." manda o navegador ir para outra URL.
 */
@Controller
@RequestMapping("/usuarioCTR")
public class UsuarioController {

    // O Service que contém as regras de negócio dos usuários.
    // "final" garante que ele é definido uma única vez, no construtor.
    private final UsuarioService oUsuarioService;

    // Injeção de dependência: o Spring cria o UsuarioService e entrega pronto
    // aqui. Não precisamos usar "new UsuarioService(...)" manualmente.
    public UsuarioController(UsuarioService oUsuarioService) {
        this.oUsuarioService = oUsuarioService;
    }

    // GET /usuarioCTR/formCadastrarUsuario
    // Exibe a tela de cadastro do usuário.
    // Enviamos um DTO vazio para a tela: o formulário HTML usa esse objeto
    // para saber quais campos preencher (nome, e-mail, senha, perfil...).
    @GetMapping("/formCadastrarUsuario")
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
