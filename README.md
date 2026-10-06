# BuscaVeg

Sistema web para **encontrar restaurantes e mercados com opções veganas e vegetarianas**.
Pessoas se cadastram informando seu perfil alimentar e localização; estabelecimentos se
cadastram para serem encontrados.


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

1. CORRIGIR todos os <form> (th:action, th:object, th:field)
2. REVISAR as rotas (GET /usuarioCTR/cadastro vs /formCadastrarUsuario)
3. ADICIONAR os campos que faltam nos templates (latitude/longitude, datas)
4. USAR th:href em vez de href hardcoded
5. REMOVER campos que não existem no backend (username, role no formulário)
6. TESTAR a mensagem de erro (th:if="${erro}") nas páginas
7. IMPLEMENTAR o controlador/service de busca de empresas


