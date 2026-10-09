# CarboFlix: Flyway, entidades, autenticação JWT, CRUD de Usuario e Swagger

Entrega de 24/09/2026: criação do banco pelo Flyway e mapeamento das entidades JPA, conforme o DER do documento de análise do CarboFlix.

Entrega de 01/10/2026 (Aula 9): autenticação e autorização com Spring Security + JWT, seguindo a demonstração do professor em aula, adaptada para a entidade `Usuario` e o pacote `com.example.demo` já existentes no projeto.

Entrega de 08/10/2026 (Aula 10): aplicação das camadas Controller/Service/Repository — nesta branch, a parte do `Usuario` (CRUD completo) e a infraestrutura compartilhada (`GlobalExceptionHandler` e documentação Swagger/OpenAPI). As demais entidades (`Perfil`, `Filme`, `Categoria`, `Avaliacao`) ficam para os PRs dos colegas responsáveis por cada uma. Veja a seção [CRUD de Usuario, tratamento de erro e Swagger](#crud-de-usuario-tratamento-de-erro-e-swagger-aula-10).

Projeto acadêmico do grupo CarboFlix em Java 21 e Spring Boot 4.1.1, conforme o starter do Spring Initializr, com Spring Web, JPA, Flyway e PostgreSQL. Utiliza Maven Wrapper 3.9.16, pacote `com.example.demo` e classe `DemoApplication`.

## Conteúdo desta etapa

- Seis migrations SQL com tabelas, chaves primárias, estrangeiras e relacionamento N:N.
- Cinco entidades: `Usuario`, `Perfil`, `Filme`, `Categoria` e `Avaliacao`.
- Enum `TipoPerfil`: `ADULTO` e `INFANTIL`.
- Configuração PostgreSQL + Flyway com `spring.jpa.hibernate.ddl-auto=validate`.

O escopo desta primeira entrega era o banco e seu mapeamento; CRUDs, controllers, serviços, DTOs e autenticação vieram nas entregas seguintes (Aula 9 e Aula 10, descritas mais abaixo). Spring Web foi mantido desde esta etapa para essa evolução.

## Dependências

Dependências utilizadas nesta etapa:

| Dependência | Função |
| --- | --- |
| `spring-boot-starter-data-jpa` | JPA e Hibernate |
| `spring-boot-starter-flyway` | Executar migrations na inicialização |
| `flyway-database-postgresql` | Suporte do Flyway ao PostgreSQL |
| `postgresql` | Driver JDBC |
| Starters de teste JPA e Flyway | Suporte a testes |

As entidades possuem construtor vazio, getters e setters explícitos. Não foi necessário acrescentar Lombok.

## Scripts

Todos estão em `src/main/resources/db/migration`:

| Versão | Arquivo | Conteúdo |
| --- | --- | --- |
| 1 | `V1__criar_tabela_usuario.sql` | Usuário e e-mail único |
| 2 | `V2__criar_tabela_perfil.sql` | Perfil e FK para usuário |
| 3 | `V3__criar_tabela_filme.sql` | Filme |
| 4 | `V4__criar_tabela_categoria.sql` | Categoria |
| 5 | `V5__criar_tabela_filme_categoria.sql` | Associação N:N com PK composta |
| 6 | `V6__criar_tabela_avaliacao.sql` | Avaliação e FKs para perfil e filme |

A ordem garante que as tabelas referenciadas existam antes das chaves estrangeiras. A tabela `flyway_schema_history` é gerenciada pelo próprio Flyway.

Em `V1__criar_tabela_usuario.sql`, `V` indica uma migration versionada, `1` é a versão e `__` são dois sublinhados. O trecho depois de `__` é uma descrição e não determina a ordem de execução. A extensão continua sendo `.sql`.

Depois que uma migration for aplicada, preserve seu nome e conteúdo. Novas alterações devem ser feitas em `V7__descricao.sql`, `V8__descricao.sql` e assim por diante.

## Entidades

Pacote: `src/main/java/com/example/demo/entity`.

| Relação do DER | Atributo Java | Implementação |
| --- | --- | --- |
| Usuário 1:N Perfil | `Perfil.usuario` | `@ManyToOne`, coluna `usuario_id` |
| Perfil 1:N Avaliação | `Avaliacao.perfil` | `@ManyToOne`, coluna `perfil_id` |
| Filme 1:N Avaliação | `Avaliacao.filme` | `@ManyToOne`, coluna `filme_id` |
| Filme N:N Categoria | `Filme.categorias` | `@ManyToMany` e `@JoinTable` |

As relações 1:N são mapeadas pelo lado da chave estrangeira. Não é necessário declarar coleções inversas para representar esses vínculos. `filme_categoria` não precisa de classe própria porque só armazena as duas FKs, usadas como chave primária composta.

Os nomes das colunas seguem o DER. `Perfil.tipo` corresponde a `tipo_perfil`; `nomePerfil`, a `nome_perfil`. IDs usam `Long`/`BIGINT` e `GenerationType.IDENTITY`. Datas usam `LocalDateTime`/`TIMESTAMP`; `sinopse` e `comentario` usam `TEXT`. O campo `criadoEm` recebe a data atual ao persistir a entidade e também tem valor padrão no banco.

O DER não informa os comprimentos dos `VARCHAR`. Foram definidos tamanhos iguais no SQL e nas entidades: nomes 120; e-mail 254; hash 255; papel 30; título 200; classificação e tipo de perfil 20; URLs 500; descrição de categoria 255. Descrição, URLs, sinopse e comentário são opcionais.

Somente o e-mail recebeu a restrição única prevista no DER. Não foi acrescentada unicidade para categorias ou avaliações por perfil/filme. A escala da nota não foi definida no documento, portanto foi preservado apenas o tipo inteiro. Não há exclusão em cascata nem regras extras de limite de perfis.

`senhaHash` corresponde à coluna `senha_hash`. O cadastro e o processamento da senha com BCrypt serão implementados na etapa de cadastro; esta entrega não insere usuários nem dados de exemplo.

## Rodar no Windows

1. Use **JDK 21** e PostgreSQL. O enunciado indica PostgreSQL 12 a 17; PostgreSQL 16 pode ser usado para a demonstração. Confira `java -version`.
2. Após clonar o repositório, abra a pasta do projeto, onde está o `pom.xml`.
3. No pgAdmin, crie um banco vazio. Não crie as tabelas manualmente:

```sql
CREATE DATABASE carboflix;
```

4. Abra o PowerShell na pasta do projeto, onde está o `pom.xml`, e execute:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/carboflix"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "SUA_SENHA_DO_POSTGRES"
.\mvnw.cmd spring-boot:run
```

Troque `SUA_SENHA_DO_POSTGRES` pela senha da sua instalação. Para usar outro banco vazio, altere o nome no final da URL. As variáveis valem para esse terminal. Na IDE, configure as mesmas variáveis na execução de `DemoApplication` e selecione o JDK 21. A primeira execução do Maven precisa de internet.

O Flyway executa as seis migrations; depois o Hibernate valida o mapeamento. Como Spring Web está presente, o servidor inicia na porta 8080. Abrir `/` no navegador retorna 404 nesta etapa, pois não há controller ou página inicial.

Não coloque a senha real no GitHub. A senha do exemplo do professor não foi copiada para o projeto.

## Configuração da Aula 8

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

O arquivo do professor usava `spring.flyway.enable`; a propriedade correta é **`spring.flyway.enabled`**. O Flyway altera o banco pelas migrations, e o Hibernate apenas confere a estrutura. O projeto mantém `validate`; não usa Hibernate para criar ou atualizar tabelas.

## Conferir no banco

Após iniciar a aplicação, execute no pgAdmin:

```sql
SELECT version, description, script, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Devem aparecer as versões **1 a 6**, todas com `success = true`.

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
```

Devem existir `usuario`, `perfil`, `filme`, `categoria`, `filme_categoria`, `avaliacao` e `flyway_schema_history`.

## Compilar e testar

Para compilar sem conexão com banco:

```powershell
.\mvnw.cmd -DskipTests package
```

Para executar o teste original `DemoApplicationTests`, crie um banco separado e vazio:

```sql
CREATE DATABASE carboflix_test;
```

Com as variáveis de usuário e senha já configuradas:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/carboflix_test"
.\mvnw.cmd test
$env:DB_URL = "jdbc:postgresql://localhost:5432/carboflix"
```

O teste inicia o contexto Spring: o Flyway aplica as migrations e o Hibernate valida as entidades. Compilar com `-DskipTests` não executa essa verificação de banco.

## Equipe e responsabilidades

| Integrante | Responsabilidade |
| --- | --- |
| Gustavo Borges Pessi | Usuário |
| Davi D'Stefani | Perfil |
| Kauan Rosso | Filme |
| Nicolas Frezza | Categoria e Avaliação |

A escrita das migrations Flyway é uma responsabilidade compartilhada. Nesta etapa, a contribuição de cada integrante se concentra nos scripts e nas entidades correspondentes; as operações CRUD serão implementadas nas próximas entregas.

## Trabalho no GitHub

1. Atualize sua cópia da branch `main` antes de iniciar uma alteração.
2. Crie uma branch para sua tarefa, como `feature/perfil`.
3. Faça commits individuais com mensagens que descrevam as alterações realizadas.
4. Envie a branch e abra um pull request para revisão do grupo antes de integrar à `main`.

Preserve o histórico e as contribuições dos colegas. Não envie `target/`, arquivos locais de IDE ou senhas. Depois que uma migration for aplicada, mantenha seu nome e conteúdo; alterações do banco devem entrar em uma nova versão SQL.

## Autenticação com Spring Security + JWT (Aula 9)

A entidade `Usuario` já existia desde a Aula 8 (tabela `usuario`, migration `V1`). Nesta etapa ela passou a implementar `UserDetails`, e foram adicionados os pacotes abaixo em `src/main/java/com/example/demo`:

| Pacote/arquivo | Função |
| --- | --- |
| `repository/UsuarioRepository.java` | `findByEmail` e `existsByEmail`, usados no login/cadastro |
| `dto/request/LoginRequest.java`, `RegisterUserRequest.java` | Dados de entrada validados com Bean Validation (`@NotBlank`, `@Email`, `@Size`) |
| `dto/response/LoginResponse.java`, `RegisterUserResponse.java` | Dados de saída (nunca devolvemos a entidade `Usuario` direto) |
| `config/SecurityConfig.java` | Define as rotas públicas/privadas, desliga sessão (STATELESS) e CSRF, registra o filtro JWT |
| `config/AuthConfig.java` | `UserDetailsService` — busca o `Usuario` pelo e-mail para o Spring Security autenticar |
| `config/TokenConfig.java` | Gera e valida o token JWT (biblioteca `com.auth0:java-jwt`) |
| `security/JwtAuthenticationFilter.java` | Filtro que lê o header `Authorization: Bearer`, valida o token e autentica a requisição |
| `controller/AuthController.java` | Endpoints `POST /auth/register` e `POST /auth/login` |
| `controller/TesteController.java` | `GET /test`, endpoint protegido só para conferir se o token está funcionando |

Não foi necessária uma nova migration: a tabela `usuario` da Aula 8 já tinha todas as colunas (`email`, `senha_hash`, `role`) usadas pela autenticação.

### Rotas

| Rota | Autenticação | Descrição |
| --- | --- | --- |
| `POST /auth/register` | Pública | Cria um usuário (senha criptografada com BCrypt) |
| `POST /auth/login` | Pública | Autentica e devolve um token JWT válido por 24h |
| `GET /test` | **Exige token** | Só responde 200 com um `Authorization: Bearer <token>` válido |
| Qualquer outra rota | Exige token | Regra padrão (`anyRequest().authenticated()`) |

### Exemplo de uso (curl)

```bash
# 1) Registrar usuário
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Gustavo","email":"gustavo@carboflix.com","senha":"123456"}'

# resposta 201:
# {"nome":"Gustavo","email":"gustavo@carboflix.com"}

# 2) Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"gustavo@carboflix.com","senha":"123456"}'

# resposta 200:
# {"token":"eyJhbGciOiJIUzI1NiJ9..."}

# 3) Chamar rota protegida com o token recebido
curl http://localhost:8080/test \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# resposta 200: "Testando segurança do CarboFlix!"
# sem o header, ou com token inválido/expirado: 401
```

### Variável de ambiente nova

```powershell
$env:JWT_SECRET = "uma-chave-bem-grande-e-secreta"
```

Se `JWT_SECRET` não for definida, é usado o valor padrão de `application.properties` (`jwt.secret`) — suficiente para rodar localmente, mas deve ser trocado antes de qualquer deploy real.

### Diferenças em relação à demonstração do professor

- A entidade de autenticação é a própria `Usuario` do CarboFlix (campos `nome`, `email`, `senhaHash`, `role`), não uma classe `User` separada — então os DTOs usam os mesmos nomes em português (`nome`, `senha`) em vez de `name`/`password`.
- `getUsername()` devolve o `email` (não o nome), pois é o e-mail que é usado para logar.
- Foi criado o `JwtAuthenticationFilter`, que valida o token em cada requisição — essa parte ainda não tinha sido fechada em aula (o método `login` do professor retornava `null`); aqui o login já devolve o token e as rotas protegidas já validam esse token de verdade.
- `/auth/register` responde `409 Conflict` se o e-mail já existir, e `/auth/login` responde `401 Unauthorized` com uma mensagem clara em vez de erro 500.

## CRUD de Usuario, tratamento de erro e Swagger (Aula 10)

Até a Aula 9, `Usuario` tinha camada parcial: o `AuthController` só criava conta e autenticava, sem `GET`/`PUT`/`DELETE`. Esta branch fecha essa parte (a responsabilidade do Usuario no grupo) e acrescenta duas peças de infraestrutura compartilhada que o restante dos Controllers (Perfil, Filme, Categoria, Avaliacao, cada um no PR do colega responsável) também vai usar: tratamento de erro centralizado e documentação Swagger.

### CRUD de Usuario

| Arquivo | Função |
| --- | --- |
| `service/UsuarioService.java` | Regra de negócio: cadastro (usado pelo `AuthController`), busca, atualização (e-mail duplicado é rejeitado, senha só muda se enviada) e remoção |
| `controller/UsuarioController.java` | `GET /usuarios`, `GET /usuarios/{id}`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}` |
| `dto/request/UpdateUsuarioRequest.java` | Nome e e-mail obrigatórios; senha opcional (só altera se vier preenchida) |
| `dto/response/UsuarioResponse.java` | Nunca expõe `senhaHash` |

`AuthController` passou a delegar a criação de conta para `UsuarioService.registrar(...)` em vez de acessar o `UsuarioRepository` direto — assim a checagem de e-mail duplicado fica num único lugar.

`GET /usuarios` devolve somente a conta autenticada. Buscar, atualizar ou remover outro id retorna 404, seguindo o mesmo critério de propriedade de Perfil. Os DTOs limitam nome a 120 caracteres e e-mail a 254, conforme o banco. Cadastro e login continuam públicos.

### Tratamento de erro centralizado (`GlobalExceptionHandler`)

Antes desta entrega, só o `AuthController` tratava algo (`BadCredentialsException`, só para o login). Agora `exception/GlobalExceptionHandler.java` (`@RestControllerAdvice`) cobre qualquer Controller da API — inclusive os que o resto do grupo for fechando:

| Situação | Status | Exceção lançada pelo Service |
| --- | --- | --- |
| `@Valid` rejeita o DTO | 400 | — (Spring lança `MethodArgumentNotValidException` antes de chegar no Service) |
| Parâmetro com tipo inválido (ex.: id não numérico) | 400 | — (`MethodArgumentTypeMismatchException`) |
| JSON malformado ou enum inválido | 400 | — (`HttpMessageNotReadableException`) |
| Recurso não encontrado (ou não pertence à conta) | 404 | `exception.RecursoNaoEncontradoException` |
| Regra de negócio violada (ex.: e-mail já cadastrado) | 409 | `exception.RegraNegocioException` |
| Exclusão bloqueada por FK (ex.: registro vinculado a outro) | 409 | — (`DataIntegrityViolationException`) |
| Token ausente ou inválido em rota protegida | 401 | — (`ApiAuthenticationEntryPoint`, no filtro de segurança) |
| E-mail/senha inválidos no login | 401 | — (`BadCredentialsException`, lançada pelo `AuthenticationManager`) |
| Qualquer outro erro não previsto | 500 | — |

Todo erro vem no mesmo formato (`ErroResponse`/`ErroValidacaoResponse`, em `dto/response`), por exemplo:

```json
{"status":409,"mensagem":"E-mail já cadastrado.","timestamp":"2026-10-08T21:00:00"}
```

Pra usar nos Services das outras entidades, basta lançar `new RecursoNaoEncontradoException("...")` ou `new RegraNegocioException("...")` — o handler cuida do resto.

### Swagger / OpenAPI

Seguindo a aula sobre documentação de API (springdoc-openapi, `@Tag`, `@Operation`, `@ApiResponses`):

| Arquivo | Função |
| --- | --- |
| `pom.xml` | Dependência `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1` |
| `config/OpenAPIConfig.java` | Informações da API e o esquema de segurança Bearer/JWT (botão "Authorize" no Swagger UI) |
| `config/SecurityConfig.java` | `/swagger-ui.html`, `/swagger-ui/**` e `/v3/api-docs/**` liberados (`permitAll()`) |
| `doc/AuthControllerDoc.java`, `doc/UsuarioControllerDoc.java`, `doc/CategoriaControllerDoc.java`, `doc/AvaliacaoControllerDoc.java` | Interfaces com `@Tag` (uma por Controller) e `@Operation`/`@ApiResponses` por endpoint |

O Controller implementa a interface `*Doc` (ex.: `AuthController implements AuthControllerDoc`) e herda as anotações do Swagger — as anotações de mapeamento HTTP (`@PostMapping`, `@RequestBody`, `@Valid`) continuam só no Controller, pra não misturar documentação com a lógica da rota. Cada colega responsável por uma entidade deve criar sua própria `*ControllerDoc` (ex.: `PerfilControllerDoc`, `FilmeControllerDoc`) seguindo o mesmo padrão.

Com a aplicação rodando, a documentação interativa fica em `http://localhost:8080/swagger-ui.html` (clique em "Authorize" e cole o token JWT obtido em `POST /auth/login`, no formato `Bearer <token>`, pra testar as rotas protegidas direto pela interface).

## Referências técnicas

- [Spring Initializr](https://start.spring.io/)
- [Spring Boot: Flyway e inicialização do banco](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
- [Spring Boot: JPA e bancos SQL](https://docs.spring.io/spring-boot/reference/data/sql.html)
- [Flyway: migrations versionadas](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/versioned-migrations)
- [Spring Security: autenticação com JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Spring Boot: validação de DTOs com Bean Validation](https://docs.spring.io/spring-boot/reference/io/validation.html)
- [Spring: tratamento de erro com `@ControllerAdvice`/`@ExceptionHandler`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html)
- [springdoc-openapi: documentação do Swagger/OpenAPI no Spring Boot](https://springdoc.org/)
