package buscaveg.com.br.BuscaVeg.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.Entity.AvaliacaoMercado;
import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Entity.Usuario;
import buscaveg.com.br.BuscaVeg.Enum.EnumTipoEstabelecimento;
import buscaveg.com.br.BuscaVeg.Repository.AvaliacaoMercadoRepository;
import buscaveg.com.br.BuscaVeg.Repository.AvaliacaoRepository;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;
import buscaveg.com.br.BuscaVeg.Repository.PratoCardapioRepository;
import buscaveg.com.br.BuscaVeg.Repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AvaliacaoMercadoRepository avaliacaoMercadoRepository;

    @Mock
    private EmpresarialRepository empresarialRepository;

    @Mock
    private PratoCardapioRepository pratoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Test
    void deveSalvarAvaliacaoDeMercado() {
        Empresarial mercado = new Empresarial();
        mercado.setId(42L);
        mercado.setTipoEspecializacao(EnumTipoEstabelecimento.MERCADO);
        Usuario usuario = new Usuario();
        usuario.setId(7L);

        when(empresarialRepository.findById(42L)).thenReturn(Optional.of(mercado));
        when(usuarioRepository.findByContaEmail("usuario@exemplo.com"))
                .thenReturn(Optional.of(usuario));
        when(avaliacaoMercadoRepository.findByMercado_IdAndUsuario_Id(42L, 7L))
                .thenReturn(Optional.empty());

        Long mercadoId = avaliacaoService.avaliarMercado(
                42L, "usuario@exemplo.com", 5);

        assertEquals(42L, mercadoId);
        ArgumentCaptor<AvaliacaoMercado> avaliacaoCaptor =
                ArgumentCaptor.forClass(AvaliacaoMercado.class);
        verify(avaliacaoMercadoRepository).save(avaliacaoCaptor.capture());
        assertEquals(5, avaliacaoCaptor.getValue().getNota());
        assertEquals(mercado, avaliacaoCaptor.getValue().getMercado());
        assertEquals(usuario, avaliacaoCaptor.getValue().getUsuario());
    }

    @Test
    void naoDevePermitirAvaliarRestauranteComoMercado() {
        Empresarial restaurante = new Empresarial();
        restaurante.setId(42L);
        restaurante.setTipoEspecializacao(EnumTipoEstabelecimento.RESTAURANTE);
        when(empresarialRepository.findById(42L)).thenReturn(Optional.of(restaurante));

        assertThrows(
                ResponseStatusException.class,
                () -> avaliacaoService.avaliarMercado(42L, "usuario@exemplo.com", 5));

        verify(usuarioRepository, never()).findByContaEmail("usuario@exemplo.com");
        verify(avaliacaoMercadoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
