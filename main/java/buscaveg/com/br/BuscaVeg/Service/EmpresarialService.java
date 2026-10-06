package buscaveg.com.br.BuscaVeg.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.DTO.AlterarEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.DTO.CadastroEmpresarialDTO;
import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;

/**
 * Regras de negócio da EMPRESA (restaurante ou mercado).
 *
 * Regra de ouro de segurança desta classe: uma empresa só pode ver/editar/
 * apagar os PRÓPRIOS dados. Isso é conferido em buscarEmpresaDoLogado().
 */
@Service
public class EmpresarialService {

    private final EmpresarialRepository oEmpresarialRepository;
    private final ContaService oContaService;

    public EmpresarialService(
            EmpresarialRepository oEmpresarialRepository,
            ContaService oContaService) {

        this.oEmpresarialRepository = oEmpresarialRepository;
        this.oContaService = oContaService;
    }

    // Busca uma empresa pelo id. Se não existir, responde 404 (página não encontrada).
    public Empresarial buscarEmpresarialPorId(Long id) {

        return oEmpresarialRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada com ID: " + id
                    )
                );
    }

    // Descobre qual é a empresa da pessoa logada, a partir do e-mail dela.
    public Empresarial buscarPorEmailDaConta(String email) {

        return oEmpresarialRepository.findByContaEmail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nenhuma empresa vinculada a esta conta."
                    )
                );
    }

    public List<Empresarial> listarTodasEmpresas() {

        return oEmpresarialRepository.findAll();
    }

    /**
     * Busca a empresa pelo id E confere se ela pertence a quem está logado.
     * Sem essa conferência, qualquer pessoa logada poderia editar ou apagar a
     * empresa de outra só trocando o número na URL (/editarEmpresa/7 -> /8).
     *
     * @throws AccessDeniedException se a empresa for de outra conta (vira erro 403)
     */
    public Empresarial buscarEmpresaDoLogado(Long id, String emailLogado) {

        Empresarial empresa = buscarEmpresarialPorId(id);

        if (!empresa.getConta().getEmail().equals(emailLogado)) {
            throw new AccessDeniedException(
                "Você não tem permissão para acessar esta empresa.");
        }

        return empresa;
    }

    // Converte a empresa salva nos dados do formulário de edição
    // (para a tela já abrir preenchida com os valores atuais).
    public AlterarEmpresarialDTO montarDtoDeEdicao(Empresarial empresa) {

        AlterarEmpresarialDTO dto = new AlterarEmpresarialDTO();

        dto.setNomeFantasia(empresa.getNomeFantasia());
        dto.setRazaoSocial(empresa.getRazaoSocial());
        dto.setEndereco(empresa.getEndereco());
        dto.setTelefone(empresa.getTelefone());
        dto.setLatitude(empresa.getLatitude());
        dto.setLongitude(empresa.getLongitude());
        dto.setTipoEspecializacao(empresa.getTipoEspecializacao());

        return dto;
    }

    /**
     * Cadastra uma empresa: cria a Conta (tipo EMPRESA) e depois a Empresarial.
     * @Transactional: se qualquer passo falhar, nada fica gravado.
     */
    @Transactional
    public Empresarial cadastrarEmpresa(CadastroEmpresarialDTO dto) {

        // Valida antes de mexer no banco.
        exigirPreenchido(dto.getCnpj(), "CNPJ");
        validarDadosDoEstabelecimento(
                dto.getNomeFantasia(),
                dto.getRazaoSocial(),
                dto.getEndereco(),
                dto.getTelefone(),
                dto.getTipoEspecializacao()
        );

        if (oEmpresarialRepository.existsByCnpj(dto.getCnpj().trim())) {
            throw new RegraDeNegocioException(
                "Já existe uma empresa cadastrada com este CNPJ.");
        }

        // 1) Cria a Conta de login (senha criptografada, e-mail único).
        Conta conta = oContaService.cadastrarConta(
                dto.getEmail(),
                dto.getPassword(),
                EnumTipoConta.EMPRESA
        );

        // 2) Cria o perfil do estabelecimento e liga à Conta.
        Empresarial empresa = new Empresarial();

        empresa.setCnpj(dto.getCnpj().trim());
        empresa.setNomeFantasia(dto.getNomeFantasia().trim());
        empresa.setRazaoSocial(dto.getRazaoSocial().trim());
        empresa.setEndereco(dto.getEndereco().trim());
        empresa.setTelefone(dto.getTelefone().trim());
        empresa.setLatitude(dto.getLatitude());
        empresa.setLongitude(dto.getLongitude());
        empresa.setTipoEspecializacao(dto.getTipoEspecializacao());
        empresa.setConta(conta);

        return oEmpresarialRepository.save(empresa);
    }

    /**
     * Altera os dados da empresa. Só altera o que o AlterarEmpresarialDTO
     * permite: CNPJ, conta e índice veg-friendly NÃO mudam por aqui.
     */
    @Transactional
    public Empresarial alterarEmpresa(
            Long id,
            AlterarEmpresarialDTO dto,
            String emailLogado) {

        Empresarial empresa = buscarEmpresaDoLogado(id, emailLogado);

        validarDadosDoEstabelecimento(
                dto.getNomeFantasia(),
                dto.getRazaoSocial(),
                dto.getEndereco(),
                dto.getTelefone(),
                dto.getTipoEspecializacao()
        );

        empresa.setNomeFantasia(dto.getNomeFantasia().trim());
        empresa.setRazaoSocial(dto.getRazaoSocial().trim());
        empresa.setEndereco(dto.getEndereco().trim());
        empresa.setTelefone(dto.getTelefone().trim());
        empresa.setLatitude(dto.getLatitude());
        empresa.setLongitude(dto.getLongitude());
        empresa.setTipoEspecializacao(dto.getTipoEspecializacao());

        return oEmpresarialRepository.save(empresa);
    }

    /**
     * Apaga a empresa E a conta de login dela (para não sobrar conta órfã).
     * A ordem importa: primeiro a Empresarial (que depende da Conta),
     * depois a Conta.
     */
    @Transactional
    public void deletarEmpresa(Long id, String emailLogado) {

        Empresarial empresa = buscarEmpresaDoLogado(id, emailLogado);
        Conta conta = empresa.getConta();

        oEmpresarialRepository.delete(empresa);
        oContaService.excluirConta(conta);
    }

    // ---------- métodos auxiliares de validação (uso interno) ----------

    private void exigirPreenchido(String valor, String nomeDoCampo) {

        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(
                "O campo " + nomeDoCampo + " é obrigatório.");
        }
    }

    private void validarDadosDoEstabelecimento(
            String nomeFantasia,
            String razaoSocial,
            String endereco,
            String telefone,
            EnumTipoEstabelecimento tipo) {

        exigirPreenchido(nomeFantasia, "nome fantasia");
        exigirPreenchido(razaoSocial, "razão social");
        exigirPreenchido(endereco, "endereço");
        exigirPreenchido(telefone, "telefone");

        if (tipo == null) {
            throw new RegraDeNegocioException(
                "Informe se é restaurante ou mercado.");
        }
    }
}
