# BuscaVeg

Sistema web para **encontrar restaurantes e mercados com opções veganas e vegetarianas**.
Pessoas se cadastram informando seu perfil alimentar e localização; estabelecimentos se
cadastram para serem encontrados.



## Tecnologias

| Tecnologia | Para que serve no projeto |
|---|---|
| Java 17 | Linguagem |
| Spring Boot | Framework que "monta" a aplicação e sobe o servidor web |
| Spring Web MVC | Recebe as requisições do navegador (Controllers) |
| Spring Data JPA / Hibernate | Conversa com o banco sem escrever SQL (Repositories e Entities) |
| MySQL | Banco de dados |
| Spring Security | Login, controle de acesso e criptografia de senha (BCrypt) |
| Thymeleaf | Gera as páginas HTML a partir dos dados do Java |
| Lombok | Gera getters/setters automaticamente (`@Data`) |
| Maven | Gerencia dependências e compila (`pom.xml`) |

## Como o código está organizado

Cada pedido do navegador percorre este caminho:

```
Navegador -> Controller -> Service -> Repository -> Banco de dados
              (recebe)     (regras)     (grava/lê)
```

## Como rodar

1. Instale Java 17 e MySQL; crie um banco (ex.: `buscaveg`).
2. Em `src/main/resources/application.properties`, configure a conexão
   (exemplo comentado no próprio arquivo).
3. Na raiz do projeto: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
4. Acesse `http://localhost:8080/usuarioCTR/formCadastrarUsuario`.

## Diagrama x código

| Parte do diagrama | Situação no código |
|---|---|
| `Conta` | Pronto |
| `Usuario` (dados e cadastro) | Pronto. Métodos de busca, avaliação e favorito: pendentes |
| `Empresarial` (dados, cadastro, edição, exclusão) | Pronto |
| `Empresarial.indiceVegFriendly` | Coluna existe; o **cálculo** (a partir do cardápio) está pendente |
| `Empresarial`: `horarioAbertura`, `horarioFechamento`, `protocoloRestricoes`, `notaMedia` | Pendente |
| `ItemCardapio` (composição, 0..*) | Pendente (`tags` será `@ElementCollection`) |
| `StatusDeTempoReal` (composição, 1) | Pendente (há um trecho comentado em `Empresarial`) |
| `Avaliacao` (Usuario 1 — * Avaliacao * — 1 Empresarial) | Pendente |
| Favoritos (Usuario * — * Empresarial) | Pendente (`@ManyToMany`) |
| `FiltroBusca` | Pendente (usa a localização para calcular distância) |
| `EnumTagAlimentar`, `EnumNivelLotacao` | Criados, ainda sem uso |

## Pendências conhecidas

1. **Telas:** a pasta `templates/` não existe (veja "Contrato das telas").
2. **Banco:** faltam as linhas `spring.datasource.*` no `application.properties`.
3. **Validação:** hoje só há checagem de campos obrigatórios, e-mail/CNPJ repetidos e tamanho do nome.
   Falta validar formato de e-mail, CNPJ e força da senha (próximo passo: Bean Validation,
   `spring-boot-starter-validation`).
4. Não há tela/rota para trocar senha ou e-mail da conta.
5. Não há administrador; ninguém consegue listar ou moderar usuários.
6. Testes automatizados: apenas o `contextLoads` padrão do Spring (que exige o banco).
