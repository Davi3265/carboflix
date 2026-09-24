# CarboFlix: Flyway e entidades

Entrega de 24/09/2026: criação do banco pelo Flyway e mapeamento das entidades JPA, conforme o DER do documento de análise do CarboFlix.

Projeto acadêmico do grupo CarboFlix em Java 21 e Spring Boot 4.1.1, conforme o starter do Spring Initializr, com Spring Web, JPA, Flyway e PostgreSQL. Utiliza Maven Wrapper 3.9.16, pacote `com.example.demo` e classe `DemoApplication`.

## Conteúdo desta etapa

- Seis migrations SQL com tabelas, chaves primárias, estrangeiras e relacionamento N:N.
- Cinco entidades: `Usuario`, `Perfil`, `Filme`, `Categoria` e `Avaliacao`.
- Enum `TipoPerfil`: `ADULTO` e `INFANTIL`.
- Configuração PostgreSQL + Flyway com `spring.jpa.hibernate.ddl-auto=validate`.

O escopo é o banco e seu mapeamento. Os CRUDs, controllers, serviços, DTOs e autenticação ficam para as próximas entregas. Spring Web foi mantido para essa evolução, mas ainda não existem endpoints.

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

## Referências técnicas

- [Spring Initializr](https://start.spring.io/)
- [Spring Boot: Flyway e inicialização do banco](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
- [Spring Boot: JPA e bancos SQL](https://docs.spring.io/spring-boot/reference/data/sql.html)
- [Flyway: migrations versionadas](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/versioned-migrations)
