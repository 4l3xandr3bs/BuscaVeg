package buscaveg.com.br.BuscaVeg.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;

@Service
public class FavoritoService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresarialRepository empresarialRepository;

    public FavoritoService(
            UsuarioRepository usuarioRepository,
            EmpresarialRepository empresarialRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresarialRepository = empresarialRepository;
    }

    @Transactional(readOnly = true)
    public List<Empresarial> listarFavoritos(String email) {
        Usuario usuario = buscarUsuario(email);
        return usuario.getFavoritos().stream()
                .sorted(Comparator.comparing(
                        Empresarial::getNomeFantasia,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public Set<Long> listarIdsFavoritos(String email) {
        return usuarioRepository.findByContaEmail(email)
                .map(Usuario::getFavoritos)
                .map(favoritos -> favoritos.stream()
                        .map(Empresarial::getId)
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    @Transactional
    public void adicionarFavorito(String email, Long empresarialId) {
        Usuario usuario = buscarUsuario(email);
        Empresarial empresa = empresarialRepository.findById(empresarialId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estabelecimento não encontrado."));
        usuario.getFavoritos().add(empresa);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerFavorito(String email, Long empresarialId) {
        Usuario usuario = buscarUsuario(email);
        usuario.getFavoritos().removeIf(empresa -> empresa.getId().equals(empresarialId));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerEstabelecimentoDeTodos(Long empresarialId) {
        for (Usuario usuario : usuarioRepository.findAllByFavoritos_Id(empresarialId)) {
            usuario.getFavoritos().removeIf(empresa -> empresa.getId().equals(empresarialId));
            usuarioRepository.save(usuario);
        }
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByContaEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Perfil de usuário não encontrado."));
    }
}
