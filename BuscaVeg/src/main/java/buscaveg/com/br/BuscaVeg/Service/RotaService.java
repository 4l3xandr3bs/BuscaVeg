package buscaveg.com.br.BuscaVeg.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RotaService {

    private static final String ORS_DIRECTIONS_URL =
            "https://api.openrouteservice.org/v2/directions/driving-car";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ors.api-key:}")
    private String orsApiKey;

    public ResponseEntity<String> calcularRota(
            double startLat,
            double startLng,
            double destLat,
            double destLng) {
        validarCoordenada(startLat, -90, 90);
        validarCoordenada(destLat, -90, 90);
        validarCoordenada(startLng, -180, 180);
        validarCoordenada(destLng, -180, 180);
        if (orsApiKey == null || orsApiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "A chave da API de rotas não está configurada.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", orsApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> body = Map.of(
                "coordinates", List.of(
                        List.of(startLng, startLat),
                        List.of(destLng, destLat)),
                "format", "json",
                "geometry", true);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ORS_DIRECTIONS_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class);
            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Não foi possível conectar ao serviço de rotas.", e);
        }
    }

    private void validarCoordenada(double coordenada, double minimo, double maximo) {
        if (!Double.isFinite(coordenada) || coordenada < minimo || coordenada > maximo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Coordenadas inválidas para calcular a rota.");
        }
    }
}
