package buscaveg.com.br.BuscaVeg.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO;
import buscaveg.com.br.BuscaVeg.Entity.AvaliacaoMercado;

@Repository
public interface AvaliacaoMercadoRepository extends JpaRepository<AvaliacaoMercado, Long> {

    Optional<AvaliacaoMercado> findByMercado_IdAndUsuario_Id(Long mercadoId, Long usuarioId);

    @Query("""
            select new buscaveg.com.br.BuscaVeg.DTO.ResumoAvaliacaoDTO(
                a.mercado.id, avg(a.nota), count(a.id))
            from AvaliacaoMercado a
            group by a.mercado.id
            """)
    List<ResumoAvaliacaoDTO> resumirMercados();

    @Modifying
    @Query("delete from AvaliacaoMercado a where a.mercado.id = :mercadoId")
    void deleteByMercadoId(@Param("mercadoId") Long mercadoId);
}
