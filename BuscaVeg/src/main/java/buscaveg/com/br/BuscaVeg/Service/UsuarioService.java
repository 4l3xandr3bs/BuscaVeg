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

        if (dto.getPerfilAlimentar() == null) {
            throw new RegraDeNegocioException("Escolha um perfil alimentar.");
        }

        if (dto.getEndereco() == null || dto.getEndereco().isBlank()) {
            throw new RegraDeNegocioException("Informe seu endereço para usar o mapa.");
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
}