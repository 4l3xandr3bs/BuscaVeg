package buscaveg.com.br.BuscaVeg.Service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import buscaveg.com.br.BuscaVeg.Excecao.RegraDeNegocioException;

@Service
public class GeoService {
    private final RestTemplate restTemplate = new RestTemplate();

    public double[] geocodeAddress(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            throw new RegraDeNegocioException(
                    "Informe o endereço para localizar sua posição.");
        }

        
String consulta = endereco.trim();

if (!consulta.toLowerCase(java.util.Locale.ROOT)
        .matches(".*\\b(brasil|brazil)\\b.*")) {
    consulta += ", Brasil";
}

java.net.URI url = UriComponentsBuilder
        .fromUriString("https://nominatim.openstreetmap.org/search")
        .queryParam("format", "jsonv2")
        .queryParam("limit", "1")
        .queryParam("countrycodes", "br")
        .queryParam("q", consulta)
        .encode()
        .build()
        .toUri();


        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "BuscaVeg/1.0 (contato@seudominio.com)");
        headers.set("Accept", "application/json");

        try {
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map[].class);

            Map[] results = response.getBody();
            
            if (results == null || results.length == 0) {
                throw new RegraDeNegocioException(
                        "Não foi possível localizar este endereço.");
            }







            Object latValue = results[0].get("lat");
            Object lonValue = results[0].get("lon");

            if (latValue == null || lonValue == null) {
                throw new RegraDeNegocioException(
                        "Não foi possível obter as coordenadas deste endereço.");
            }

            double lat = Double.parseDouble(latValue.toString());
            double lon = Double.parseDouble(lonValue.toString());

            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                throw new RegraDeNegocioException(
                        "O endereço devolveu coordenadas inválidas.");
            }

            return new double[]{lat, lon};
        } catch (HttpStatusCodeException | IllegalArgumentException e) {
            throw new RegraDeNegocioException(
                    "Não foi possível localizar o endereço. Verifique-o e tente novamente.");
        }
    }
}