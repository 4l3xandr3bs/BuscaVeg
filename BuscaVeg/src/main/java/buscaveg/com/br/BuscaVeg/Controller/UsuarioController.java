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

    // Injeção de dependência: o Spring cria o UsuarioService e entrega pronto
    // aqui. Não precisamos usar "new UsuarioService(...)" manualmente.
    public UsuarioController(UsuarioService oUsuarioService) {
        this.oUsuarioService = oUsuarioService;
    }

    @GetMapping("/formCadastrarUsuario")
    public String telaCadastraUsuario(Model oModel) {

   
        oModel.addAttribute("usuario", new CadastroUsuarioDTO());

        return "templates/cadastro.html"; // abre templates/cadastrarUsuario.html
    }

    
    @GetMapping("/login")
    public String telaLogin() {

        return "login"; // abre templates/login.html
    }

   
    @PostMapping("/salvarUsuario")
    public String cadastrarUsuario(
            @ModelAttribute("usuario") CadastroUsuarioDTO dto,
            Model oModel) {

        try {
            // Delega ao Service o trabalho de salvar (Conta + Usuario).
            oUsuarioService.cadastrarUsuario(dto);

        } catch (RegraDeNegocioException e) {
      
            oModel.addAttribute("erro", e.getMessage());
            return "cadastro";
        }


        return "redirect:/usuarioCTR/login?cadastrado";
    }
}
