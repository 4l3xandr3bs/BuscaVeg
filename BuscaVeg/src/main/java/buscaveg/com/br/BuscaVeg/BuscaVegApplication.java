package buscaveg.com.br.BuscaVeg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PONTO DE ENTRADA DO SISTEMA.
 *
 * É daqui que tudo começa: ao executar o método main, o Spring Boot sobe o
 * servidor web embutido (Tomcat), conecta no banco de dados e "descobre"
 * automaticamente todas as classes do projeto marcadas com @Controller,
 * @Service, @Repository, @Entity e @Configuration.
 *
 * @SpringBootApplication junta três recursos em um só:
 *  - configuração automática (Spring monta o que for possível sozinho);
 *  - varredura de componentes (procura as classes anotadas dentro deste
 *    pacote e dos pacotes abaixo dele);
 *  - marca esta classe como fonte de configuração.
 *
 * IMPORTANTE: esta classe precisa ficar no pacote "raiz"
 * (buscaveg.com.br.BuscaVeg). Se for movida para fora dele, o Spring deixa
 * de enxergar as demais classes.
 */
@SpringBootApplication
public class BuscaVegApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscaVegApplication.class, args);
	}

}
