package buscaveg.com.br.BuscaVeg.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import buscaveg.com.br.BuscaVeg.DTO.CadastroUsuarioDTO;
import buscaveg.com.br.BuscaVeg.DTO.PerfilUsuarioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
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

        return "cadastro";
    }

    
    @GetMapping("/login")
    public String telaLogin() {

        return "login"; // abre templates/login.html
    }

    @GetMapping("/perfil")
    public String meuPerfil(Principal principal, Model oModel) {
        Usuario usuario = oUsuarioService.buscarPorEmailDaConta(principal.getName());
        oModel.addAttribute("perfil", montarPerfilDTO(usuario));
        oModel.addAttribute("email", principal.getName());
        oModel.addAttribute("perfisAlimentares", EnumPerfilAlimentar.values());
        return "perfil";
    }

    @GetMapping("/configuracoes")
    public String configuracoes(Principal principal, Model model) {
        Usuario usuario = oUsuarioService.buscarPorEmailDaConta(principal.getName());
        model.addAttribute("perfil", montarPerfilDTO(usuario));
        carregarOpcoesConfiguracoes(model, principal.getName());
        return "configuracoes";
    }

    @PostMapping("/configuracoes")
    public String atualizarConfiguracoes(
            @ModelAttribute("perfil") PerfilUsuarioDTO dto,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            oUsuarioService.atualizarPerfil(principal.getName(), dto);
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfil", dto);
            carregarOpcoesConfiguracoes(model, principal.getName());
            return "configuracoes";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Suas informações foram atualizadas.");
        return "redirect:/usuarioCTR/configuracoes";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(
            @ModelAttribute("perfil") PerfilUsuarioDTO dto,
            Principal principal,
            Model oModel,
            RedirectAttributes redirectAttributes) {

        try {
            oUsuarioService.atualizarPerfil(principal.getName(), dto);
        } catch (RegraDeNegocioException e) {
            oModel.addAttribute("erro", e.getMessage());
            oModel.addAttribute("email", principal.getName());
            oModel.addAttribute("perfisAlimentares", EnumPerfilAlimentar.values());
            return "perfil";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        return "redirect:/usuarioCTR/perfil";
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

    private PerfilUsuarioDTO montarPerfilDTO(Usuario usuario) {
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();
        dto.setNome(usuario.getNome());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setPerfilAlimentar(usuario.getPerfilAlimentar());
        dto.setEndereco(usuario.getEndereco());
        return dto;
    }

    private void carregarOpcoesConfiguracoes(Model model, String email) {
        model.addAttribute("email", email);
        model.addAttribute("perfisAlimentares", EnumPerfilAlimentar.values());
    }
}
