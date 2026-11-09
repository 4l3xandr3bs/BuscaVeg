package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoConta;
import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;
import buscaveg.com.br.BuscaVeg.Repository.ContaRepository;


@Service
public class ContaService {

    private final ContaRepository oContaRepository;
    private final PasswordEncoder encoder;
    private final GeoService oGeoService;


    public ContaService(
            ContaRepository oContaRepository,
            PasswordEncoder encoder, GeoService oGeoService) {

        this.oContaRepository = oContaRepository;
        this.encoder = encoder;
        this.oGeoService = oGeoService;
    }


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

       
        oConta.setPassword(encoder.encode(senhaPura));

       
        oConta.setTipo(tipo != null ? tipo : EnumTipoConta.USUARIO);

        return oContaRepository.save(oConta);
    }

   
    @Transactional
    public void excluirConta(Conta oConta) {
        oContaRepository.delete(oConta);
    }
}
