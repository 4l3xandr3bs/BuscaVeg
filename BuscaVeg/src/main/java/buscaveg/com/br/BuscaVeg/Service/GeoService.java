package buscaveg.com.br.BuscaVeg.Service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;

@Service
public class GeoService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] geocodeAddress(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            throw new RegraDeNegocioException("Informe o endereço para localizar sua posição.");
        }

        String url = UriComponentsBuilder
            .fromUriString("https://nominatim.openstreetmap.org/search")
            .queryParam("format", "jsonv2")
            .queryParam("limit", "1")
            .queryParam("q", endereco)
            .build()
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "BuscaVeg/1.0");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map[].class
        );

        Map[] results = response.getBody();

        if (results == null || results.length == 0) {
            throw new RegraDeNegocioException("Não foi possível localizar este endereço.");
        }

        Map<String, Object> data = results[0];

        double lat = Double.parseDouble(data.get("lat").toString());
        double lon = Double.parseDouble(data.get("lon").toString());

        return new double[] { lat, lon };
    }
}
