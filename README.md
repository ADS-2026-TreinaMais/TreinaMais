# TreinaMais

Aplicativo móvel para gerenciamento de treinos físicos, conectando professores de educação física e seus alunos.

## Sobre o projeto

O **TreinaMais** é uma aplicação móvel desenvolvida como parte do Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas da Pontifícia Universidade Católica de Goiás (PUC Goiás), atividade curricular voltada à aplicação prática dos conhecimentos desenvolvidos ao longo do período.

O Projeto Integrador tem como proposta o desenvolvimento, em equipe, de uma aplicação móvel de média a alta complexidade, abrangendo o ciclo completo de desenvolvimento de software, desde o levantamento e especificação de requisitos até a implementação, testes, documentação e distribuição.

Dentro desse contexto, o TreinaMais tem como domínio o acompanhamento de treinos físicos. O aplicativo permite que o professor monte treinos personalizados, cadastrando exercícios com séries, repetições e carga, e os associe a um ou mais alunos. O aluno visualiza os treinos atribuídos a ele, acompanha sua execução e registra sua evolução física por meio de fotos e, opcionalmente, medidas corporais. A aplicação utiliza regras de negócio, persistência de dados, integração com serviços externos e recursos nativos do dispositivo.

O desenvolvimento é realizado de forma incremental, com utilização de prototipação, modelagem, controle de versão, documentação técnica e testes.

## Equipe

**Grupo 1**

- Cauê Antônio Gomes de Oliveira
- João Pedro Dias
- Luan Felipe Goulart Cardoso Trindade
- Yuri de Sousa Silva

**Orientador:** Prof. Welington Julio Dias Rodrigues

## Problema

Muitos professores de educação física ainda montam e repassam treinos em papel ou planilhas soltas, o que dificulta a comunicação com os alunos, a atualização dos treinos e o acompanhamento da evolução física ao longo do tempo.

## Solução

O aplicativo centraliza em um único lugar a criação de treinos pelo professor e a consulta e execução desses treinos pelo aluno, além do registro da evolução física, facilitando a comunicação entre ambos e reduzindo o uso de papel e planilhas.

## Público-alvo

- Professores de educação física;
- Alunos de academia e de treinamento físico.

## Escopo

O TreinaMais é destinado exclusivamente à plataforma **Android**, com pacote instalável (APK ou AAB) testado em dispositivo físico.

Estão **fora do escopo** deste projeto:

- Controle de dieta/nutrição;
- Pagamentos;
- Agendamento de aulas;
- Funcionalidades de marketplace entre professores e alunos.

## Funcionalidades previstas

- Cadastro e login com seleção de perfil (aluno ou professor);
- Controle de acesso por perfil de usuário;
- Cadastro, edição e exclusão de treinos pelo professor;
- Cadastro, edição e remoção de exercícios de um treino, com séries, repetições e carga;
- Pesquisa e importação de exercícios (nome, instruções e GIF demonstrativo) a partir de API externa;
- Associação de treinos a um ou mais alunos;
- Consulta dos treinos atribuídos pelo aluno;
- Marcação de séries, exercícios ou treino completo como concluído;
- Registro de foto de evolução física com a câmera do dispositivo;
- Registro de avaliação física (peso e medidas) com histórico;
- Persistência local dos dados com funcionamento offline;
- Sincronização de dados com servidor remoto;
- Lembrete/notificação do treino do dia.

## Telas previstas

- **Login/Cadastro:** com seleção de perfil (aluno ou professor);
- **Home do Aluno:** lista de treinos atribuídos ao aluno logado;
- **Detalhe do Treino (aluno):** exercícios, séries, repetições e carga, botão de concluir e acesso à câmera para foto de evolução;
- **Home do Professor:** lista de alunos vinculados;
- **Criar/Editar Treino (professor):** busca de exercícios na API externa, definição de séries/repetições e associação a alunos;
- **Perfil/Configurações:** dados da conta e logout;
- **Evolução Física (desejável):** galeria de fotos e histórico de medidas do aluno.

## Tecnologias

- **Android** — plataforma de destino;
- **Java** — linguagem principal *(a confirmar pela equipe)*;
- **Android Studio** — ambiente de desenvolvimento;
- **XML** — construção das interfaces;
- **GitHub** — controle de versão, hospedagem do código e colaboração;
- **Banco de Dados (a definir)** — persistência local;
- **Backend remoto gratuito (a definir, ex.: Firebase)** — persistência e sincronização de dados;
- **API REST de exercícios (a definir)** — base de dados externa de exercícios.

> As tecnologias e bibliotecas poderão ser atualizadas durante o desenvolvimento do projeto.

## Arquitetura

O projeto será organizado em camadas, buscando separar as responsabilidades da aplicação:

```text
Interface
   ↓
Lógica de apresentação
   ↓
Regras de negócio
   ↓
Persistência e serviços
```
---

## Especificação de Requisitos

### Convenções
- **Identificadores:** `RFxx` para Requisitos Funcionais, `RNFxx` para Requisitos Não Funcionais e `RNxx` para Regras de Negócio.
- **Prioridades:**
  - **Obrigatório:** Indispensável ao funcionamento do aplicativo e aos requisitos mínimos do projeto.
  - **Desejável:** Importante, mas pode ser entregue em uma etapa posterior.
  - **Opcional:** Complementar, implementado se houver tempo disponível.

---

### Requisitos Funcionais (RF)

| ID | Requisito | Prioridade |
| :--- | :--- | :--- |
| **Autenticação e Perfis** | | |
| **RF01** | O sistema deve permitir cadastro e login com seleção de perfil (aluno ou professor). | Obrigatório |
| **RF02** | O sistema deve autenticar o usuário e controlar o acesso conforme o perfil (aluno ou professor). | Obrigatório |
| **Treinos e Exercícios** | | |
| **RF03** | O professor deve poder criar, editar e excluir treinos (CRUD de Treino). | Obrigatório |
| **RF04** | O professor deve poder adicionar, editar e remover exercícios de um treino, definindo séries, repetições e carga (CRUD de Exercício). | Obrigatório |
| **RF05** | O professor deve poder pesquisar exercícios em uma base de dados externa (API) e importar nome, instruções e GIF demonstrativo para o treino. | Obrigatório |
| **RF06** | O professor deve poder associar um treino a um ou mais alunos. | Obrigatório |
| **Execução e Acompanhamento** | | |
| **RF07** | O aluno deve poder visualizar a lista de treinos a ele atribuídos. | Obrigatório |
| **RF08** | O aluno deve poder marcar séries/exercícios ou o treino completo como concluído. | Desejável |
| **RF10** | O aluno deve poder registrar foto de evolução física utilizando a câmera do dispositivo. | Obrigatório |
| **RF11** | O sistema deve permitir o registro de avaliação física do aluno (peso, medidas) com histórico ao longo do tempo. | Desejável |
| **RF12** | O sistema deve enviar notificação/lembrete do treino do dia. | Opcional |
| **Dados e Integração** | | |
| **RF09** | O sistema deve sincronizar a base de dados local com o servidor remoto, mantendo os dados disponíveis offline. | Obrigatório |

---

### Requisitos Não Funcionais (RNF)

| ID | Categoria | Requisito | Prioridade |
| :--- | :--- | :--- | :--- |
| **RNF01** | Desempenho | A sincronização de dados deve ocorrer em segundo plano, sem bloquear a interface do usuário. | Obrigatório |
| **RNF02** | Disponibilidade Offline | O aplicativo deve permanecer funcional offline, sincronizando os dados pendentes assim que a conexão for reestabelecida. | Obrigatório |
| **RNF03** | Segurança | Senhas armazenadas com hash; autenticação via token de sessão; permissão de câmera solicitada apenas no momento do uso. | Obrigatório |
| **RNF04** | Compatibilidade | O aplicativo deve ser compatível com a plataforma Android, a partir de uma versão mínima a ser definida pela equipe. | Obrigatório |
| **RNF05** | Usabilidade | Interface simples e objetiva, adequada ao uso rápido dentro do ambiente de academia. | Desejável |

---

### Regras de Negócio (RN)

| ID | Regra |
| :--- | :--- |
| **RN01** | Somente usuários com perfil "professor" podem criar, editar ou excluir treinos e exercícios. |
| **RN02** | Um aluno só pode visualizar e executar treinos que estejam associados a ele. |
| **RN03** | Um treino só pode ser salvo se tiver pelo menos 1 exercício cadastrado. |
| **RN04** | Todo exercício de um treino deve ter série e repetição maiores que zero. |
| **RN05** | Um professor só pode editar ou excluir treinos criados por ele mesmo. |
| **RN06** | O aluno pode marcar um treino como concluído, mas não pode alterar sua estrutura (exercícios, séries, repetições). |
| **RN07** | Cada foto de evolução física fica vinculada exclusivamente ao aluno que a capturou. |
| **RN08** | Alterações feitas offline só são consideradas confirmadas após sincronização bem-sucedida com o servidor remoto. |
| **RN09** | O e-mail de cadastro deve ser único por usuário no sistema. |
| **RN10** | Cada aluno fica vinculado a um único professor por vez; essa vinculação pode ser alterada posteriormente, tanto pelo professor quanto pelo aluno. |

---

## Rastreabilidade com os Requisitos Mínimos do Projeto

| Requisito Mínimo do Projeto Integrador | Atendido Por |
| :--- | :--- |
| **Mínimo de 6 telas funcionais com navegação estruturada** | Seção "Telas previstas" (Login/Cadastro, Home do Aluno, Detalhe do Treino, Home do Professor, Criar/Editar Treino, Perfil/Configurações e Evolução Física) |
| **CRUD completo em pelo menos 2 entidades** | `RF03` (Treino) e `RF04` (Exercício) |
| **Consumo de pelo menos 1 serviço ou API externa relevante** | `RF05` |
| **Repositório Git com histórico de contribuições da equipe** | Repositório no GitHub com commits de todos os integrantes |
| **Autenticação com pelo menos 2 perfis de usuário distintos** | `RF01`, `RF02`, `RN01`, `RN02` (aluno e professor) |
| **Persistência local e remota com sincronização de dados** | `RF09`, `RNF01`, `RNF02`, `RN08` |
| **Uso de pelo menos 1 recurso nativo do dispositivo** | `RF10` (câmera) |
| **Pacote instalável (APK ou AAB) em dispositivo físico** | `RNF04` e seção "Escopo" |

---

## Premissas e Pendências

1. **Servidor Remoto:** O `RF09` exige um backend gratuito para persistência e sincronização (ex: Firebase), ainda a ser definido. A escolha afetará os requisitos `RF01`, `RF02` e `RNF03`.
2. **API Externa:** A API de exercícios do `RF05` precisa ser escolhida e confirmada; o projeto depende da sua disponibilidade e estabilidade.
3. **Versão Mínima do Android:** A versão mínima do `RNF04` deve ser definida pela equipe.
4. **Permissão de Câmera:** O `RF10` pressupõe que o usuário conceda a permissão de acesso à câmera quando solicitada.
5. **Restrições:** Prazo limitado ao semestre letivo, equipe de quatro integrantes e uso obrigatório de serviços e APIs gratuitos.
6. **Levantamento de Requisitos:** Os requisitos foram levantados por brainstorming em equipe; o documento deve ser atualizado caso sejam realizadas entrevistas com professores de educação física.
