package buscaveg.com.br.BuscaVeg.Repository;

import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumPerfilAlimentar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso ao banco para a entidade Usuario.
 * Os métodos básicos (save, findAll, findById...) já vêm de JpaRepository.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Lista todos os usuários de um perfil alimentar.
    // Ex.: findByPerfilAlimentar(EnumPerfilAlimentar.VEGANO)
    //      => SELECT * FROM usuario WHERE perfil_alimentar = 'VEGANO'
    List<Usuario> findByPerfilAlimentar(EnumPerfilAlimentar perfilAlimentar);

}
