package buscaveg.com.br.BuscaVeg.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import buscaveg.com.br.BuscaVeg.Entity.Empresarial;
import buscaveg.com.br.BuscaVeg.Repository.EmpresarialRepository;
import buscaveg.com.br.BuscaVeg.Service.RotaService;

@RestController
public class ApiController {

    private final EmpresarialRepository empresarialRepository;
    private final RotaService rotaService;

    public ApiController(EmpresarialRepository empresarialRepository, RotaService rotaService) {
        this.empresarialRepository = empresarialRepository;
        this.rotaService = rotaService;
    }

    @GetMapping("/api/rotas")
    public ResponseEntity<String> calcularRota(
            @RequestParam Long empresarialId,
            @RequestParam double startLat,
            @RequestParam double startLng) {
        Empresarial empresa = empresarialRepository.findById(empresarialId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estabelecimento não encontrado."));
        if (empresa.getLatitude() == null || empresa.getLongitude() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O estabelecimento não possui coordenadas cadastradas.");
        }
        return rotaService.calcularRota(
                startLat, startLng, empresa.getLatitude(), empresa.getLongitude());
    }

    @GetMapping("/api/lugares/proximos")
    public List<Map<String, Object>> listarProximos(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double raioKm) {

        List<Empresarial> empresas = empresarialRepository.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Empresarial empresa : empresas) {
            if (empresa.getLatitude() == null || empresa.getLongitude() == null) {
                continue;
            }

            double distancia = calcularDistanciaKm(lat, lng, empresa.getLatitude(), empresa.getLongitude());
            if (distancia <= raioKm) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", empresa.getId());
                item.put("nome", empresa.getNomeFantasia());
                item.put("endereco", empresa.getEndereco());
                item.put("telefone", empresa.getTelefone());
                item.put("tipo", empresa.getTipoEspecializacao() != null ? empresa.getTipoEspecializacao().name() : "");
                item.put("latitude", empresa.getLatitude());
                item.put("longitude", empresa.getLongitude());
                resultado.add(item);
            }
        }

        return resultado;
    }

    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final double raioTerra = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return raioTerra * c;
    }
}
