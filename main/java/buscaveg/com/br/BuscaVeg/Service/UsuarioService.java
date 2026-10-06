package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buscaveg.com.br.BuscaVeg.DTO.CadastroUsuarioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;

/**
 * Regras de negócio do USUÁRIO (perfil da pessoa que busca lugares).
 */
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

    /**
     * Cadastra uma pessoa: cria a Conta (login) e depois o Usuario (perfil).
     *
     * @Transactional faz as duas gravações valerem juntas: se salvar o Usuario
     * falhar, a Conta criada um passo antes é desfeita (rollback). Sem isso,
     * ficaria uma conta de login sem perfil no banco.
     */
    @Transactional
    public Usuario cadastrarUsuario(CadastroUsuarioDTO dto) {

        // Valida antes de mexer no banco.
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RegraDeNegocioException("O nome é obrigatório.");
        }
        if (dto.getNome().length() > 50) {
            throw new RegraDeNegocioException("O nome deve ter no máximo 50 caracteres.");
        }
        if (dto.getPerfilAlimentar() == null) {
            throw new RegraDeNegocioException("Escolha um perfil alimentar.");
        }

        // 1) Cria a Conta (criptografa a senha e recusa e-mail repetido).
        Conta conta = oContaService.cadastrarConta(
                dto.getEmail(),
                dto.getPassword(),
                EnumTipoConta.USUARIO
        );

        // 2) Cria o perfil e liga à Conta.
        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome().trim());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setPerfilAlimentar(dto.getPerfilAlimentar());
        usuario.setLatitude(dto.getLatitude());
        usuario.setLongitude(dto.getLongitude());
        usuario.setConta(conta);

        return oUsuarioRepository.save(usuario);
    }
}
