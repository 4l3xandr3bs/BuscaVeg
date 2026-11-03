package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.ContaRepository;

/**
 * CAMADA SERVICE = onde ficam as REGRAS DE NEGÓCIO.
 *
 * O Controller recebe o pedido, o Service decide o que fazer
 * (validar, criptografar, aplicar regras) e o Repository grava no banco.
 *
 * Este service é o ÚNICO lugar que cria e apaga Contas. Tanto o cadastro de
 * usuário quanto o de empresa passam por aqui, então a regra "senha sempre
 * criptografada e e-mail nunca repetido" existe em um só lugar.
 */
@Service
public class ContaService {

    private final ContaRepository oContaRepository;
    private final PasswordEncoder encoder;
    private final GeoService oGeoService;

    // Dependências entregues pelo Spring (injeção via construtor).
    public ContaService(
            ContaRepository oContaRepository,
            PasswordEncoder encoder, GeoService oGeoService) {

        this.oContaRepository = oContaRepository;
        this.encoder = encoder;
        this.oGeoService = oGeoService;
    }

    /**
     * Cria uma conta de login.
     *
     * @param email     e-mail que será o "nome de login"
     * @param senhaPura senha como a pessoa digitou (é criptografada aqui)
     * @param tipo      USUARIO ou EMPRESA (se vier vazio, assume USUARIO)
     * @return a Conta já salva (com o id gerado pelo banco)
     * @throws RegraDeNegocioException se faltar e-mail/senha ou o e-mail já existir
     */
    // @Transactional: se algo falhar, nada fica gravado pela metade.
    @Transactional
    public Conta cadastrarConta(String email, String senhaPura, EnumTipoConta tipo) {

        if (email == null || email.isBlank()
                || senhaPura == null || senhaPura.isBlank()) {
            throw new RegraDeNegocioException("E-mail e senha são obrigatórios.");
        }

        String emailLimpo = email.trim();

        if (oContaRepository.existsByEmail(emailLimpo)) {
            throw new RegraDeNegocioException(
                "Já existe uma conta cadastrada com este e-mail.");
        }

        Conta oConta = new Conta();
        oConta.setEmail(emailLimpo);

        // Criptografa a senha antes de salvar no banco
        // (a senha original nunca é armazenada; fica só o hash BCrypt).
        oConta.setPassword(encoder.encode(senhaPura));

        // Define o tipo padrão da conta
        // (se ninguém informou, assume que é uma pessoa comum).
        oConta.setTipo(tipo != null ? tipo : EnumTipoConta.USUARIO);

        return oContaRepository.save(oConta);
    }

    // Apaga uma conta (usado quando a empresa é excluída: o login dela
    // também deixa de existir, para não sobrar conta "órfã" no banco).
    @Transactional
    public void excluirConta(Conta oConta) {
        oContaRepository.delete(oConta);
    }
}
