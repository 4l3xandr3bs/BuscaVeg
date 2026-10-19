const query = "/main/java/buscaveg/com/br/BuscaVeg/Entity/Empresarial.java";
const url = `https://nominatim.openstreetmap.org/search?format=jsonv2&q=${encodeURIComponent(query)}`;

fetch(url, {
  headers: {
    "Accept-Language": "pt-BR"
  }
})
  .then(res => res.json())
  .then(data => {
    if (data && data[0]) {
      console.log(data[0].lat, data[0].lon);
    }
  });