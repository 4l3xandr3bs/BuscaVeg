package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buscaveg.com.br.BuscaVeg.DTO.CadastroUsuarioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;


@Service
public class UsuarioService {

    private final UsuarioRepository oUsuarioRepository;
    private final ContaService oContaService;

    public UsuarioService(
            UsuarioRepository oUsuarioRepository,
            ContaService oContaService) {

        this.oUsuarioRepository = oUsuarioRepository;
        this.oContaService = oContaService;
    }

    @Transactional
    public Usuario cadastrarUsuario(CadastroUsuarioDTO dto) {

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RegraDeNegocioException("O nome é obrigatório.");
        }
        if (dto.getNome().length() > 50) {
            throw new RegraDeNegocioException("O nome deve ter no máximo 50 caracteres.");
        }
        if (dto.getPerfilAlimentar() == null) {
            throw new RegraDeNegocioException("Escolha um perfil alimentar.");
        }

       
        Conta conta = oContaService.cadastrarConta(
                dto.getEmail(),
                dto.getPassword(),
                EnumTipoConta.USUARIO
        );


        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome().trim());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setPerfilAlimentar(dto.getPerfilAlimentar());
        usuario.setLatitude(dto.getLatitude());
        usuario.setLongitude(dto.getLongitude());
        usuario.setConta(conta);

        return oUsuarioRepository.save(usuario);
    }
    public Usuario buscarPorEmail(String email) {
    Conta conta = contaRepository.findByEmail(email)
        .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    
    return oUsuarioRepository.findByConta(conta)
        .orElseThrow(() -> new RegraDeNegocioException("Perfil de usuário não encontrado"));
}
}
