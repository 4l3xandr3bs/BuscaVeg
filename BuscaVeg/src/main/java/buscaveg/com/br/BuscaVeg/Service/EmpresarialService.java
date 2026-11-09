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

@Service
public class EmpresarialService {

    private final EmpresarialRepository oEmpresarialRepository;
    private final ContaService oContaService;
    private final GeoService geoService;

    public EmpresarialService(
            EmpresarialRepository oEmpresarialRepository,
            ContaService oContaService,
            GeoService geoService) {

        this.oEmpresarialRepository = oEmpresarialRepository;
        this.oContaService = oContaService;
        this.geoService = geoService;
    }

    public Empresarial buscarEmpresarialPorId(Long id) {
        return oEmpresarialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empresa não encontrada com ID: " + id
                ));
    }

    public Empresarial buscarPorEmailDaConta(String email) {
        return oEmpresarialRepository.findByContaEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nenhuma empresa vinculada a esta conta."
                ));
    }

    public List<Empresarial> listarTodasEmpresas() {
        return oEmpresarialRepository.findAll();
    }

    public Empresarial buscarEmpresaDoLogado(Long id, String emailLogado) {
        Empresarial empresa = buscarEmpresarialPorId(id);

        if (!empresa.getConta().getEmail().equals(emailLogado)) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta empresa.");
        }

        return empresa;
    }

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

    @Transactional
    public Empresarial cadastrarEmpresa(CadastroEmpresarialDTO dto) {

        exigirPreenchido(dto.getCnpj(), "CNPJ");
        validarDadosDoEstabelecimento(
                dto.getNomeFantasia(),
                dto.getRazaoSocial(),
                dto.getEndereco(),
                dto.getTelefone(),
                dto.getTipoEspecializacao()
        );

        if (oEmpresarialRepository.existsByCnpj(dto.getCnpj().trim())) {
            throw new RegraDeNegocioException("Já existe uma empresa cadastrada com este CNPJ.");
        }

        if (dto.getEndereco() == null || dto.getEndereco().isBlank()) {
            throw new RegraDeNegocioException("Informe o endereço do estabelecimento.");
        }

        double[] coords = geoService.geocodeAddress(dto.getEndereco());
        dto.setLatitude(coords[0]);
        dto.setLongitude(coords[1]);

        Conta conta = oContaService.cadastrarConta(
                dto.getEmail(),
                dto.getPassword(),
                EnumTipoConta.EMPRESA
        );

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

    @Transactional
    public Empresarial alterarEmpresa(Long id, AlterarEmpresarialDTO dto, String emailLogado) {
        Empresarial empresa = buscarEmpresaDoLogado(id, emailLogado);

        validarDadosDoEstabelecimento(
                dto.getNomeFantasia(),
                dto.getRazaoSocial(),
                dto.getEndereco(),
                dto.getTelefone(),
                dto.getTipoEspecializacao()
        );

        if (dto.getEndereco() != null && !dto.getEndereco().isBlank()) {
            double[] coords = geoService.geocodeAddress(dto.getEndereco());
            dto.setLatitude(coords[0]);
            dto.setLongitude(coords[1]);
        }

        empresa.setNomeFantasia(dto.getNomeFantasia().trim());
        empresa.setRazaoSocial(dto.getRazaoSocial().trim());
        empresa.setEndereco(dto.getEndereco().trim());
        empresa.setTelefone(dto.getTelefone().trim());
        empresa.setLatitude(dto.getLatitude());
        empresa.setLongitude(dto.getLongitude());
        empresa.setTipoEspecializacao(dto.getTipoEspecializacao());

        return oEmpresarialRepository.save(empresa);
    }

    @Transactional
    public void deletarEmpresa(Long id, String emailLogado) {
        Empresarial empresa = buscarEmpresaDoLogado(id, emailLogado);
        Conta conta = empresa.getConta();

        oEmpresarialRepository.delete(empresa);
        oContaService.excluirConta(conta);
    }

    private void exigirPreenchido(String valor, String nomeDoCampo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("O campo " + nomeDoCampo + " é obrigatório.");
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
            throw new RegraDeNegocioException("Informe se é restaurante ou mercado.");
        }
    }
}
