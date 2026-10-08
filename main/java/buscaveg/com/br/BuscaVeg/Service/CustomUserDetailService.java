package buscaveg.com.br.BuscaVeg.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import buscaveg.com.br.BuscaVeg.Entity.Conta;
import buscaveg.com.br.BuscaVeg.Repository.ContaRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final ContaRepository oContaRepository;

    public CustomUserDetailService(ContaRepository oContaRepository) {
        this.oContaRepository = oContaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

       
        Conta oConta = oContaRepository.findByEmail(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Conta não encontrada!"
                    )
                );

        
        return User.builder()
                .username(oConta.getEmail())
                .password(oConta.getPassword()) 
                
                .roles(oConta.getTipo().name())
                .build();
    }
}
