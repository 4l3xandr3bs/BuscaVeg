package buscaveg.com.br.BuscaVeg.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.DTO.PratoCardapioDTO;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Entity.PratoCardapio;
import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import buscaveg.com.br.BuscaVeg.Enum.EnumTagAlimentar;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.AvaliacaoRepository;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;
import buscaveg.com.br.BuscaVeg.Repository.PratoCardapioRepository;

@Service
public class CardapioService {

    private final EmpresarialRepository empresarialRepository;
    private final PratoCardapioRepository pratoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public CardapioService(
            EmpresarialRepository empresarialRepository,
            PratoCardapioRepository pratoRepository,
            AvaliacaoRepository avaliacaoRepository) {
        this.empresarialRepository = empresarialRepository;
        this.pratoRepository = pratoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<PratoCardapio> listarPratosDisponiveis(
            Long empresarialId,
            EnumPerfilAlimentar perfilAlimentar) {
        return pratoRepository.findByEmpresarialIdAndDisponivelTrueOrderByNomeAsc(empresarialId)
                .stream()
                .filter(prato -> atendePerfil(prato.getTags(), perfilAlimentar))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PratoCardapio> listarTodos(Long empresarialId, String emailDono) {
        exigirDono(empresarialId, emailDono);
        return pratoRepository.findByEmpresarialIdOrderByNomeAsc(empresarialId);
    }

    @Transactional
    public void adicionarPrato(Long empresarialId, String emailDono, PratoCardapioDTO dto) {
        Empresarial empresa = exigirDono(empresarialId, emailDono);
        validar(dto);

        PratoCardapio prato = new PratoCardapio();
        prato.setNome(dto.getNome().trim());
        prato.setDescricao(dto.getDescricao().trim());
        prato.setPreco(dto.getPreco());
        prato.setTags(Set.copyOf(dto.getTags()));
        prato.setDisponivel(true);
        prato.setEmpresarial(empresa);
        pratoRepository.save(prato);
    }

    @Transactional
    public void alternarDisponibilidade(
            Long empresarialId,
            Long pratoId,
            String emailDono) {
        exigirDono(empresarialId, emailDono);
        PratoCardapio prato = buscarPratoDoEstabelecimento(pratoId, empresarialId);
        prato.setDisponivel(!prato.isDisponivel());
        pratoRepository.save(prato);
    }

    @Transactional
    public void removerPrato(Long empresarialId, Long pratoId, String emailDono) {
        exigirDono(empresarialId, emailDono);
        PratoCardapio prato = buscarPratoDoEstabelecimento(pratoId, empresarialId);
        avaliacaoRepository.deleteByPratoId(pratoId);
        pratoRepository.delete(prato);
    }

    @Transactional
    public void removerCardapioDoEstabelecimento(Long empresarialId) {
        avaliacaoRepository.deleteByEmpresarialId(empresarialId);
        pratoRepository.deleteByEmpresarialId(empresarialId);
    }

    private Empresarial exigirDono(Long empresarialId, String emailDono) {
        Empresarial empresa = empresarialRepository.findById(empresarialId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estabelecimento não encontrado."));
        if (!empresa.getConta().getEmail().equals(emailDono)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Você não pode alterar o cardápio deste estabelecimento.");
        }
        return empresa;
    }

    private PratoCardapio buscarPratoDoEstabelecimento(Long pratoId, Long empresarialId) {
        PratoCardapio prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prato não encontrado."));
        if (!prato.getEmpresarial().getId().equals(empresarialId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Prato não encontrado neste estabelecimento.");
        }
        return prato;
    }

    private void validar(PratoCardapioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new RegraDeNegocioException("Informe o nome do prato.");
        }
        if (dto.getNome().trim().length() > 100) {
            throw new RegraDeNegocioException("O nome do prato deve ter no máximo 100 caracteres.");
        }
        if (dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new RegraDeNegocioException("Informe a descrição do prato.");
        }
        if (dto.getDescricao().trim().length() > 500) {
            throw new RegraDeNegocioException("A descrição deve ter no máximo 500 caracteres.");
        }
        if (dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Informe um preço maior que zero.");
        }
        if (dto.getTags() == null || dto.getTags().isEmpty()) {
            throw new RegraDeNegocioException("Selecione ao menos uma etiqueta alimentar.");
        }
        if (!dto.getTags().contains(EnumTagAlimentar.VEGANO)
                && !dto.getTags().contains(EnumTagAlimentar.VEGETARIANO)) {
            throw new RegraDeNegocioException(
                    "Marque o prato como vegano ou vegetariano para informar a quem ele é adequado.");
        }
    }

    private boolean atendePerfil(Set<EnumTagAlimentar> tags, EnumPerfilAlimentar perfil) {
        if (perfil == null
                || perfil == EnumPerfilAlimentar.FLEXITARIANO
                || perfil == EnumPerfilAlimentar.SIMPATIZANTE) {
            return true;
        }
        if (perfil == EnumPerfilAlimentar.VEGANO) {
            return tags.contains(EnumTagAlimentar.VEGANO);
        }
        return tags.contains(EnumTagAlimentar.VEGANO)
                || tags.contains(EnumTagAlimentar.VEGETARIANO);
    }
}
