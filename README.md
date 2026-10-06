# BuscaVeg

Sistema web para **encontrar restaurantes e mercados com opções veganas e vegetarianas**.
Pessoas se cadastram informando seu perfil alimentar e localização; estabelecimentos se
cadastram para serem encontrados.

> **Status:** em desenvolvimento. Já existem cadastro e login de pessoas e de empresas,
> edição/exclusão da própria empresa e listagem de empresas. A busca com filtros,
> avaliações, cardápio e status em tempo real ainda não foram implementados
> (veja "Diagrama x código").

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

Todas as pastas ficam em `src/main/java/buscaveg/com/br/BuscaVeg/`:

| Pasta | O que tem | Regra de ouro |
|---|---|---|
| `Controller/` | Recebe URLs, escolhe a tela | Sem regra de negócio, sem acesso direto ao banco |
| `Service/` | Regras de negócio | É aqui que se decide "o que fazer" |
| `Repository/` | Interfaces de acesso ao banco | Só fala com o banco |
| `Entity/` | Classes que viram tabelas | Representam os dados |
| `DTO/` | "Caixas de transporte" dos formulários | Só os campos que o formulário pode enviar |
| `Excecao/` | `RegraDeNegocioException` | Erro causado pelo preenchimento (mostrado na tela) |
| `Enum/` | Listas fechadas de valores | Evitam texto livre/erros de digitação |
| `SecrityConfig/` | Configuração de segurança | Quem acessa o quê |

(O nome da pasta `SecrityConfig` está sem o "u"; funciona, mas vale corrigir.)

### Por que DTOs?

Os formulários de cadastro pedem dados de **duas** entidades de uma vez (e-mail/senha da
`Conta` + dados do perfil). Além disso, ligar o formulário direto à entidade permitiria que
alguém enviasse campos extras (`id`, `conta`, `indiceVegFriendly`) e alterasse o que não
deveria. O DTO aceita só os campos listados.

## Modelo de dados

A decisão de design mais importante: **o login fica separado dos perfis**.

```
              +---------+
              |  Conta  |  e-mail, senha (hash), tipo (USUARIO | EMPRESA)
              +----+----+
      1 para 1     |     1 para 1      (cada Conta tem UM perfil: um OU outro)
        +----------+----------+
        v                     v
  +-----------+        +--------------+
  |  Usuario  |        | Empresarial  |
  +-----------+        +--------------+
```

Pessoas e empresas entram no sistema exatamente do mesmo jeito. O diagrama de classes
completo (inclusive o que ainda falta implementar) está em
`Class_Diagram_BuscaVeg_revisado.png`.

## Regras de negócio já implementadas

- **Senha** nunca é salva em texto puro: `ContaService` aplica BCrypt.
- **E-mail** e **CNPJ** não podem se repetir (mensagem amigável na tela).
- Cadastro **atômico** (`@Transactional`): Conta + perfil são gravados juntos ou nenhum dos dois.
- Uma empresa só **vê, edita e apaga a própria** empresa (conferido no `EmpresarialService`).
  Tentar acessar a de outra devolve erro 403.
- Apagar a empresa apaga também a conta de login e encerra a sessão.
- `indiceVegFriendly` **não é editável** por formulário (no diagrama ele é calculado).

## Rotas

| URL | Método | Acesso | O que faz |
|---|---|---|---|
| `/usuarioCTR/formCadastrarUsuario` | GET | Público | Tela de cadastro de pessoa |
| `/usuarioCTR/salvarUsuario` | POST | Público | Cria Conta + Usuario |
| `/usuarioCTR/login` | GET | Público | Tela de login |
| `/login` | POST | Público | Processado pelo Spring Security |
| `/logout` | POST | Logado | Sai do sistema |
| `/empresarialCTR/formCadastrarEmpresa` | GET | Público | Tela de cadastro de empresa |
| `/empresarialCTR/salvarEmpresa` | POST | Público | Cria Conta (EMPRESA) + Empresarial |
| `/empresarialCTR/listarEmpresas` | GET | Logado | Lista as empresas |
| `/empresarialCTR/minhaEmpresa` | GET | EMPRESA | Atalho para editar a própria empresa |
| `/empresarialCTR/editarEmpresa/{id}` | GET | EMPRESA (dona) | Formulário de edição |
| `/empresarialCTR/alterarEmpresa/{id}` | POST | EMPRESA (dona) | Salva a edição |
| `/empresarialCTR/deletarEmpresa/{id}` | POST | EMPRESA (dona) | Apaga empresa e conta |

Depois do login: conta **EMPRESA** vai para `/empresarialCTR/minhaEmpresa`;
conta **USUARIO** vai para `/empresarialCTR/listarEmpresas`.

## Contrato das telas (para quem for criar o HTML)

As telas ficam em `src/main/resources/templates/` e **ainda não existem**. Cada uma deve ter:

| Arquivo | Objeto / dados | Campos (`th:field`) ou ação |
|---|---|---|
| `login.html` | — | `<form method="post" th:action="@{/login}">` com campos `username` (o e-mail) e `password`; mensagens para `?error`, `?logout`, `?cadastrado` |
| `cadastrarUsuario.html` | `th:object="${usuario}"` | `nome`, `email`, `password`, `dataNascimento`, `perfilAlimentar`, `latitude`, `longitude`; ação `@{/usuarioCTR/salvarUsuario}`; mostrar `${erro}` |
| `cadastrarEmpresa.html` | `th:object="${empresarial}"` | `email`, `password`, `cnpj`, `nomeFantasia`, `razaoSocial`, `endereco`, `telefone`, `latitude`, `longitude`, `tipoEspecializacao`; ação `@{/empresarialCTR/salvarEmpresa}`; mostrar `${erro}` |
| `listarEmpresas.html` | `${empresas}` | percorrer a lista com `th:each` |
| `editarEmpresa.html` | `th:object="${empresarial}"`, `${empresaId}` | `nomeFantasia`, `razaoSocial`, `endereco`, `telefone`, `latitude`, `longitude`, `tipoEspecializacao`; ação `@{/empresarialCTR/alterarEmpresa/{id}(id=${empresaId})}`; botão de excluir = **form POST** para `@{/empresarialCTR/deletarEmpresa/{id}(id=${empresaId})}` |

Use sempre `th:action` nos formulários: com ele o Thymeleaf inclui o token CSRF
automaticamente (a proteção CSRF está **ativa**). O botão "Sair" também precisa ser um
formulário POST para `/logout`.

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
