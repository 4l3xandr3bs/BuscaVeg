package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import buscaveg.com.br.BuscaVeg.DTO.CadastroUsuarioDTO;
import buscaveg.com.br.BuscaVeg.DTO.PerfilUsuarioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository oUsuarioRepository;
    private final ContaService oContaService;
    private final GeoService geoService;

    public UsuarioService(
            UsuarioRepository oUsuarioRepository,
            ContaService oContaService,
            GeoService geoService) {

        this.oUsuarioRepository = oUsuarioRepository;
        this.oContaService = oContaService;
        this.geoService = geoService;
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
        if (dto.getEndereco() == null || dto.getEndereco().isBlank()) {
            throw new RegraDeNegocioException("Informe o endereço para localizar sua posição.");
        }

        double[] coords = geoService.geocodeAddress(dto.getEndereco());
        dto.setLatitude(coords[0]);
        dto.setLongitude(coords[1]);

        Conta conta = oContaService.cadastrarConta(
                dto.getEmail(),
                dto.getPassword(),
                EnumTipoConta.USUARIO
        );

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome().trim());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setPerfilAlimentar(dto.getPerfilAlimentar());
        usuario.setEndereco(dto.getEndereco().trim());
        usuario.setLatitude(dto.getLatitude());
        usuario.setLongitude(dto.getLongitude());
        usuario.setConta(conta);

        return oUsuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmailDaConta(String email) {
        return oUsuarioRepository.findByContaEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Não foi possível encontrar o perfil deste usuário."));
    }

    @Transactional
    public void atualizarPerfil(String emailAtual, PerfilUsuarioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RegraDeNegocioException("O nome é obrigatório.");
        }
        if (dto.getNome().trim().length() > 50) {
            throw new RegraDeNegocioException("O nome deve ter no máximo 50 caracteres.");
        }
        if (dto.getPerfilAlimentar() == null) {
            throw new RegraDeNegocioException("Escolha um perfil alimentar.");
        }
        if (dto.getEndereco() == null || dto.getEndereco().isBlank()) {
            throw new RegraDeNegocioException("Informe seu endereço.");
        }

        Usuario usuario = buscarPorEmailDaConta(emailAtual);
        String endereco = dto.getEndereco().trim();

        if (!endereco.equals(usuario.getEndereco())) {
            try {
                double[] coords = geoService.geocodeAddress(endereco);
                usuario.setLatitude(coords[0]);
                usuario.setLongitude(coords[1]);
            } catch (RestClientException e) {
                throw new RegraDeNegocioException(
                        "Não foi possível localizar o endereço. Verifique-o e tente novamente.");
            }
        }

        usuario.setNome(dto.getNome().trim());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setPerfilAlimentar(dto.getPerfilAlimentar());
        usuario.setEndereco(endereco);
        oUsuarioRepository.save(usuario);
    }
}
