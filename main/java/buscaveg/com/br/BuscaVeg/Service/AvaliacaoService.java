package buscaveg.com.br.BuscaVeg.Service;

import buscaveg.com.br.BuscaVeg.DTO.AvaliacaoDTO;
import buscaveg.com.br.BuscaVeg.Entity.Avaliacao;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.AvaliacaoRepository;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final EmpresarialRepository empresarialRepository;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            EmpresarialRepository empresarialRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.empresarialRepository = empresarialRepository;
    }

    /**
     * Salva uma nova avaliação e recalcula o índice de compatibilidade vegan-friendly.
     * 
     * @param usuario Usuário que está deixando a avaliação
     * @param dto Dados da avaliação
     * @throws RegraDeNegocioException se os dados forem inválidos
     */
    @Transactional
    public Avaliacao salvarAvaliacao(Usuario usuario, AvaliacaoDTO dto) {

        // Validações
        if (usuario == null) {
            throw new RegraDeNegocioException("Usuário não identificado.");
        }

        if (dto.getEmpresarialId() == null) {
            throw new RegraDeNegocioException("Estabelecimento não selecionado.");
        }

        if (dto.getNota() == null || dto.getNota() < 1 || dto.getNota() > 5) {
            throw new RegraDeNegocioException("Nota deve estar entre 1 e 5 estrelas.");
        }

        if (dto.getComentario() == null || dto.getComentario().isBlank()) {
            throw new RegraDeNegocioException("Comentário é obrigatório.");
        }

        if (dto.getComentario().length() > 500) {
            throw new RegraDeNegocioException("Comentário não pode ter mais de 500 caracteres.");
        }

        // Busca o estabelecimento
        Empresarial empresarial = empresarialRepository.findById(dto.getEmpresarialId())
                .orElseThrow(() ->
                    new RegraDeNegocioException("Estabelecimento não encontrado.")
                );

      
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setEmpresarial(empresarial);
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario().trim());
        avaliacao.setDataCriacao(LocalDateTime.now());

     
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

      
        atualizarIndiceVegFriendly(empresarial);

        return avaliacaoSalva;
    }

    /**
     * @param empresarial
     */
    @Transactional
    public void atualizarIndiceVegFriendly(Empresarial empresarial) {

        Double media = avaliacaoRepository.calcularMediaNotas(empresarial);

       
        if (media != null) {
            empresarial.setIndiceVegFriendly(media.floatValue());
        } else {
            empresarial.setIndiceVegFriendly(null);
        }
        empresarialRepository.save(empresarial);
    }

   
    public List<Avaliacao> listarAvaliacoesPorEmpresa(Empresarial empresarial) {
        return avaliacaoRepository.findByEmpresarial(empresarial);
    }

    public List<Avaliacao> listarTodasAvaliacoes() {
        return avaliacaoRepository.findAll();
    }
}