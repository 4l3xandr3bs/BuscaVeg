package buscaveg.com.br.BuscaVeg.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO;
import buscaveg.com.br.BuscaVeg.Entity.Avaliacao;
import buscaveg.com.br.BuscaVeg.Entity.PratoCardapio;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.AvaliacaoRepository;
import buscaveg.com.br.BuscaVeg.Repository.PratoCardapioRepository;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PratoCardapioRepository pratoRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            PratoCardapioRepository pratoRepository,
            UsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.pratoRepository = pratoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Map<Long, ResumoAvaliacaoDTO> resumirPratos(Collection<PratoCardapio> pratos) {
        List<Long> pratoIds = pratos.stream()
                .map(PratoCardapio::getId)
                .toList();
        if (pratoIds.isEmpty()) {
            return Map.of();
        }
        return indexarPorItem(avaliacaoRepository.resumirPratos(pratoIds));
    }

    @Transactional(readOnly = true)
    public Map<Long, ResumoAvaliacaoDTO> resumirEstabelecimentos() {
        return indexarPorItem(avaliacaoRepository.resumirEstabelecimentos());
    }

    @Transactional(readOnly = true)
    public PratoCardapio buscarPrato(Long pratoId) {
        return pratoRepository.findById(pratoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prato não encontrado."));
    }

    @Transactional(readOnly = true)
    public Integer buscarNotaAtual(Long pratoId, String emailUsuario) {
        Usuario usuario = buscarUsuario(emailUsuario);
        return avaliacaoRepository.findByPrato_IdAndUsuario_Id(pratoId, usuario.getId())
                .map(Avaliacao::getNota)
                .orElse(null);
    }

    @Transactional
    public Long avaliar(Long pratoId, String emailUsuario, Integer nota) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new RegraDeNegocioException("Escolha uma nota de 1 a 5 estrelas.");
        }

        PratoCardapio prato = buscarPrato(pratoId);
        if (!prato.isDisponivel()) {
            throw new RegraDeNegocioException("Este prato não está disponível para avaliação.");
        }

        Usuario usuario = buscarUsuario(emailUsuario);
        Avaliacao avaliacao = avaliacaoRepository
                .findByPrato_IdAndUsuario_Id(pratoId, usuario.getId())
                .orElseGet(Avaliacao::new);
        avaliacao.setNota(nota);
        avaliacao.setPrato(prato);
        avaliacao.setUsuario(usuario);
        avaliacaoRepository.save(avaliacao);
        return prato.getEmpresarial().getId();
    }

    private Usuario buscarUsuario(String emailUsuario) {
        return usuarioRepository.findByContaEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Perfil de usuário não encontrado."));
    }

    private Map<Long, ResumoAvaliacaoDTO> indexarPorItem(List<ResumoAvaliacaoDTO> resumos) {
        return resumos.stream().collect(Collectors.toMap(
                ResumoAvaliacaoDTO::itemId,
                Function.identity()));
    }
}
