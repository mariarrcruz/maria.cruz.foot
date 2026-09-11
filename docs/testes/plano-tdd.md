# Plano TDD do PDV da Carla

**Status:** Plano de testes; nao implementa testes nem codigo.
**Data da analise:** 2026-09-11.
**Fonte de verdade:** RFCs, personas, `docs/product/arquitetura/arquitetura.md` e ADRs com status explicitamente indicado.

## 1. Escopo e inventario encontrado

### Artefatos consultados

- `docs/product/RFC/requisitos-funcionais.md`
- `docs/product/RFC/requisitos-nao-funcionais.md`
- `docs/product/personas/carla-operadora.md`
- `docs/product/personas/lucas-estudante.md`
- `docs/product/personas/marina-cliente-recorrente.md`
- `docs/product/arquitetura/arquitetura.md`
- `docs/product/arquitetura/ADR/README.md`
- `docs/product/arquitetura/ADR/ADR-001-autoridade-transacional.md`
- `docs/product/arquitetura/ADR/ADR-002-pagamento-online-condicionado.md`
- `docs/product/arquitetura/ADR/ADR-003-baixa-condicional-encomenda.md`
- `docs/product/arquitetura/ADR/ADR-004-batch-ml-fora-caminho-critico.md`

### Artefatos nao encontrados

Foi procurado codigo e teste nas pastas `src`, `app`, `packages`, `tests`, `test` e `lib` do workspace. Nenhum arquivo foi encontrado nesses caminhos. Tambem nao ha implementacao de cron, batch, ML ou provedor de pagamento documentada nos artefatos consultados.

### Regra para este plano

Os casos abaixo derivam somente de regras e requisitos encontrados. Quando o comportamento esperado nao esta especificado, o caso fica marcado como **bloqueado** ou **pergunta**, e nao recebe uma regra inventada.

As metas M-04 a M-08 sao metricas provisórias dos RFCs. Devem ser usadas somente depois de confirmacao com Carla e definicao da amostra.

## 2. Ciclo TDD proposto

Para cada comportamento aprovado:

1. **Red:** escrever um teste que expresse o requisito e falhe.
2. **Green:** implementar o menor comportamento que satisfaz o teste.
3. **Refactor:** melhorar a estrutura sem alterar o resultado observado.
4. Reexecutar o caso feliz, limites, invalidos, conflitos e estados proibidos relacionados.

Nenhum caso deve ser implementado enquanto sua regra estiver bloqueada por uma pergunta aberta.

## 3. Inventario das regras de negocio

### RN-01 — Precos fixos

**Fonte:** `requisitos-funcionais.md`, RN-01; RF-01 e RF-04; RNF-01 e RNF-13.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Produto do grupo salgados | Preco exibido e usado no calculo: R$ 8,00 | Obrigatorio |
| Feliz | Produto do grupo bolos ou tortas | Preco exibido e usado no calculo: R$ 15,00 | Obrigatorio |
| Feliz | Produto do grupo bebidas | Preco exibido e usado no calculo: R$ 6,00 | Obrigatorio |
| Limite | Pedido com um item de cada grupo | Total usa exatamente os tres precos cadastrados, sem arredondamento indevido | Obrigatorio |
| Limite | Quantidade informada para um produto | Total e quantidade permanecem consistentes entre selecao, revisao e registro | Obrigatorio; valor minimo de quantidade nao definido |
| Invalida | Produto sem preco cadastrado ou grupo desconhecido | Comportamento nao definido; nao permitir presumir preco | Bloqueado |
| Invalida | Tentativa de alterar o preco na tela de pedido | Comportamento de alteracao nao definido; deve ser decidido antes do teste | Pergunta |
| Conflito | Preco exibido diferente do preco usado no total | Nao concluir com divergencia; comportamento de bloqueio ou nova revisao ainda nao definido | Bloqueado |
| Estado proibido | Venda persistida com preco diferente do preco revisado | Deve ser considerado falha de RN-01/RNF-01 | Obrigatorio |

### RN-02 — Pagamentos aceitos

**Fonte:** `requisitos-funcionais.md`, RN-02; RF-06 e RF-17.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Pagamento por Pix | Forma Pix pode ser registrada, respeitando RN-03 | Obrigatorio |
| Feliz | Pagamento por cartao de debito | Forma cartao de debito pode ser registrada | Obrigatorio |
| Feliz | Pagamento por cartao de credito | Forma cartao de credito pode ser registrada | Obrigatorio |
| Limite | Cada uma das tres formas no mesmo conjunto de vendas | Consultas agrupam cada venda na forma correspondente | Obrigatorio |
| Invalida | Forma fora de Pix, debito e credito | Nao deve ser aceita como forma prevista pela regra | Obrigatorio |
| Invalida | Forma ausente | Comportamento de erro e mensagem nao especificados | Bloqueado |
| Conflito | Pagamento online com forma e estados nao definidos | Nao marcar como confirmado sem contrato do provedor | ADR-002 bloqueada |
| Estado proibido | Venda aprovada com forma de pagamento fora das tres aceitas | Rejeitar ou impedir registro; nao deve aparecer nas consultas | Obrigatorio |

### RN-03 — Confirmacao externa do Pix

**Fonte:** `requisitos-funcionais.md`, RN-03 e RF-09; persona Carla, observacao sobre Pix.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Carla confirma o recebimento no QR Code da maquininha Mercado Pago | PDV permite prosseguir com o registro da venda | Obrigatorio |
| Limite | Confirmacao externa ocorre antes do registro no PDV | Registro pode ocorrer apos a confirmacao, sem o PDV substituir a maquininha | Obrigatorio |
| Invalida | Carla nao confirmou o recebimento externamente | Nao deve registrar o Pix como confirmado | Obrigatorio por RF-09 |
| Invalida | QR Code da maquininha nao pode ser validado | Comportamento de mensagem e recuperacao nao especificado | Bloqueado |
| Conflito | PDV tenta confirmar o Pix sem Mercado Pago | PDV nao deve substituir a validacao externa | Obrigatorio |
| Estado proibido | Venda Pix apresentada como concluida sem confirmacao externa | Nao apresentar como concluida | Obrigatorio |

### RN-04 — Toda venda concluida e registrada

**Fonte:** `requisitos-funcionais.md`, RN-04 e RF-07/RF-08; RNF-02 e RNF-09.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Venda com itens, quantidades, total, pagamento e data/hora validos | Venda registrada com esses dados e confirmacao clara | Obrigatorio |
| Limite | Venda com um item | Registro contem o item e quantidade correspondente | Obrigatorio |
| Limite | Venda com varios itens | Registro preserva todos os itens e quantidades | Obrigatorio |
| Invalida | Falha de comunicacao antes da confirmacao | Informar falha e nao apresentar venda como concluida | Obrigatorio por RNF-10 |
| Invalida | Campo minimo ausente: itens, quantidade, total, pagamento ou data/hora | Comportamento de rejeicao nao detalhado; nao concluir silenciosamente | Bloqueado |
| Conflito | Reenvio da mesma venda depois de falha | Nao duplicar e manter resultado consistente; mecanismo de idempotencia esta proposto no ADR-001, ainda nao aprovado | Condicional |
| Estado proibido | Venda apresentada como concluida mas ausente nas consultas | Falha de RN-04/RNF-02/RNF-09 | Obrigatorio |
| Estado proibido | Mesma venda aparece duplicada nas consultas | Falha de RN-04/RNF-02 | Obrigatorio |

### RN-05 — Encomenda paga nao gera nova cobranca

**Fonte:** `requisitos-funcionais.md`, RN-05; RF-11 a RF-14; persona Carla.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | QR Code localiza encomenda ja paga | Exibir encomenda para conferencia e permitir retirada sem nova cobranca | Obrigatorio |
| Limite | Encomenda com os itens localizados e estado elegivel | Conferir itens e entregar sem iniciar cobranca adicional | Obrigatorio |
| Invalida | QR Code nao localizado | Informar que nao localizou; nao cobrar e nao baixar | Obrigatorio por RNF-08 |
| Invalida | QR Code de encomenda sem estado de paga | Origem e estados de encomenda nao estao definidos | Bloqueado |
| Conflito | Fluxo de retirada oferece pagamento de uma encomenda ja paga | Nao gerar nova cobranca; comportamento de interface precisa ser validado | Obrigatorio quanto a regra |
| Estado proibido | Retirada de encomenda gera venda ou cobranca nova | Proibido por RN-05 |

### RN-06 — Retirada somente apos conferencia e entrega

**Fonte:** `requisitos-funcionais.md`, RN-06 e RF-12/RF-13; RNF-07 e RNF-12.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Carla localiza, confere, entrega e confirma a retirada | Marcar encomenda como realizada | Obrigatorio |
| Limite | Confirmacao ocorre imediatamente depois da entrega | Permitir baixa, respeitando o estado atual | Obrigatorio |
| Invalida | Tentativa de baixa antes da conferencia | Solicitar conferencia antes da baixa; nao marcar como realizada | Obrigatorio |
| Invalida | Tentativa de baixa antes da entrega | Nao marcar como realizada | Obrigatorio |
| Conflito | Duas tentativas de baixa para a mesma encomenda | Nao permitir duas baixas; retorno para segunda tentativa depende do contrato de estado | RNF-12; ADR-003 proposta |
| Estado proibido | Encomenda marcada como realizada sem conferencia e entrega | Proibido por RN-06 |
| Estado proibido | Encomenda ja realizada recebe nova baixa | Proibido por RF-14/RNF-12 |

### RN-07 — Nao emitir comprovante

**Fonte:** `requisitos-funcionais.md`, RN-07 e RF-10; RNF-07 e escopo fora do sistema.

| Tipo | Caso | Resultado esperado | Estado |
|---|---|---|---|
| Feliz | Venda concluida | Confirmar a venda sem exigir nem emitir comprovante | Obrigatorio |
| Limite | Venda com qualquer forma de pagamento aceita | Regra de nao emissao permanece | Obrigatorio |
| Invalida | Fluxo tenta exigir comprovante para concluir | Nao exigir comprovante | Obrigatorio |
| Conflito | Cliente solicita comprovante | Tratamento especifico nao documentado; nao inventar resposta de negocio | Pergunta |
| Estado proibido | PDV emite comprovante ao cliente | Proibido por RN-07/RF-10 |

## 4. Requisitos nao funcionais e casos TDD

Os casos abaixo cobrem restricoes de qualidade. As metas M-04 a M-08 sao referencias de medicao provisórias, nao criterios definitivos.

### RNF-01 — Consistencia dos calculos

**Fonte:** `requisitos-nao-funcionais.md`, RNF-01; M-01.

- **Feliz:** total da revisao igual ao total persistido; esperado: igualdade sem arredondamento indevido.
- **Limite:** pedido de um item e pedido com varios itens; esperado: ambos preservam igualdade.
- **Invalida:** alterar item, quantidade ou preco entre revisao e registro; esperado: nao persistir divergencia; comportamento de bloqueio/revisao ainda precisa ser definido.
- **Conflito:** preco da tela diverge do preco cadastrado; esperado: nao concluir com valor divergente.
- **Estado proibido:** valor revisado diferente do valor registrado.

### RNF-02 — Integridade do registro

**Fonte:** RNF-02; M-02.

- **Feliz:** venda confirmada aparece no total do dia, produto e forma de pagamento.
- **Limite:** venda com um e varios itens; esperado: quantidades corretas em cada consulta.
- **Invalida:** falha antes de confirmar; esperado: nao aparecer como venda confirmada.
- **Conflito:** reenvio de operacao; esperado: nao duplicar, condicionado a decisao de idempotencia do ADR-001.
- **Estado proibido:** registro confirmado ausente ou duplicado nas consultas.

### RNF-03 e RNF-04 — Pico e pedidos variados

**Fonte:** RNF-03 e RNF-04; M-04; personas Carla, Lucas e Marina.

- **Feliz:** selecao, revisao e conclusao com pedido de um item; esperado: fluxo responder adequadamente.
- **Limite:** pedido com varios itens durante 20h30-21h; esperado: fluxo continuar adequado.
- **Invalida:** dispositivo ou navegador incompatível; esperado: compatibilidade ainda nao definida, portanto caso bloqueado.
- **Conflito:** confirmacoes adicionais aumentam passos; esperado: proteger integridade sem violar baixa carga, com trade-off a validar.
- **Estado proibido:** declarar desempenho aprovado usando M-04 sem confirmar meta, dispositivo e amostra.

### RNF-05 e RNF-06 — Baixa carga e legibilidade

**Fonte:** RNF-05 e RNF-06; personas Carla, Lucas e Marina; M-06 e M-07.

- **Feliz:** Carla conclui pedido sem digitacao extensa e identifica itens, precos, total e estados.
- **Limite:** atendimento rapido em ambiente externo; esperado: informacoes permanecem legiveis.
- **Invalida:** texto, preco ou estado nao legivel; esperado: caso falha, mas tamanho de tela e dispositivo nao estao definidos.
- **Conflito:** mais informacao de conferencia versus poucos passos; esperado: decisao de UX pendente, sem criterio inventado.
- **Estado proibido:** confirmar venda ou baixa sem destacar o total/estado exigido.

### RNF-07 e RNF-08 — Prevencao de erro e feedback

**Fonte:** RNF-07 e RNF-08.

- **Feliz:** destacar total antes da venda e pedir confirmacao antes da baixa; esperado: operacao prossegue apos confirmacao.
- **Limite:** QR nao localizado, encomenda ja realizada ou falha de registro; esperado: mensagem compreensivel para cada estado.
- **Invalida:** entrada QR inexistente; esperado: informar nao localizado e nao baixar.
- **Conflito:** erro de pagamento online sem contrato; esperado: nao marcar como pago, conforme ADR-002 bloqueada.
- **Estado proibido:** apresentar sucesso em operacao sem registro confirmado.

### RNF-09 e RNF-10 — Persistencia e falhas online

**Fonte:** RNF-09 e RNF-10.

- **Feliz:** venda confirmada permanece disponivel nas consultas.
- **Limite:** falha de comunicacao durante registro; esperado: informar falha e nao apresentar conclusao falsa.
- **Invalida:** reenvio apos falha; esperado: comportamento de duplicacao e recuperacao nao esta definido no RFC; ADR-001 propoe idempotencia, mas esta como proposta.
- **Conflito:** disponibilidade percebida versus falhar fechado; esperado: proteger integridade, sem operar offline, conforme escopo.
- **Estado proibido:** venda confirmada perdida ou falsa confirmacao.

### RNF-11 — Acesso restrito

**Fonte:** RNF-11; ASR-08; C-06.

- **Feliz:** usuario autorizado registra, consulta e baixa.
- **Limite:** cada operacao protegida deve validar autorizacao.
- **Invalida:** usuario nao autorizado tenta qualquer operacao; esperado: acesso negado.
- **Conflito:** ciclo de criacao, revogacao e auditoria nao esta definido.
- **Estado proibido:** usuario nao autorizado registrar venda, consultar movimento ou baixar encomenda.

### RNF-12 — Protecao contra retirada duplicada

**Fonte:** RNF-12; RF-14; M-03.

- **Feliz:** encomenda elegivel e baixada uma vez.
- **Limite:** segunda tentativa ou duplo clique; esperado: nenhuma segunda mutacao.
- **Invalida:** baixa com estado atual ja realizado; esperado: impedir.
- **Conflito:** mecanismo transacional nao definido; ADR-003 propoe baixa condicional.
- **Estado proibido:** duas baixas para a mesma encomenda.

### RNF-13 e RNF-14 — Precos centralizados e rastreabilidade

**Fonte:** RNF-13 e RNF-14; M-01, M-02 e M-03.

- **Feliz:** selecao, calculo, revisao e consulta usam a fonte central de precos; venda e alteracao de estado têm identificador e data/hora.
- **Limite:** alteracao de preco ou estado deve continuar rastreavel; governanca de alteracao nao definida.
- **Invalida:** origem de preco diferente entre telas; esperado: nao aceitar divergencia.
- **Conflito:** alteracao concorrente de estado; esperado: validar estado atual, mecanismo ainda pendente.
- **Estado proibido:** registro sem identificador ou timestamp exigido.

### RNF-15 — Site em navegador compativel

**Fonte:** RNF-15.

- **Feliz:** uso no navegador e dispositivo que Carla utilizar.
- **Limite:** selecao, revisao, conclusao e QR Code no mesmo ambiente.
- **Invalida:** navegador/dispositivo fora da compatibilidade definida; esperado: matriz de compatibilidade ainda nao existe.
- **Conflito:** nenhum dispositivo ou navegador foi informado.
- **Estado proibido:** declarar compatibilidade sem dispositivo e navegador alvo.

## 5. Casos derivados dos ADRs

Os ADRs nao aprovados nao devem ser tratados como comportamento obrigatório. Seus casos ficam separados para quando a decisão for validada.

| ADR | Caso condicional | Resultado esperado se o ADR for aprovado |
|---|---|---|
| ADR-001 | Reenvio com a mesma chave de idempotencia | Uma venda confirmada e nenhuma duplicacao |
| ADR-001 | Baixa concorrente | Apenas a transicao valida altera o estado |
| ADR-002 | Pagamento online pendente/recusado/estornado | Venda nao e confirmada como paga sem estado aprovado |
| ADR-002 | Webhook ou consulta repetida | Atualizacao idempotente, se esse contrato for aprovado |
| ADR-003 | QR Code repetido ou ja realizado | Exibir estado e nao criar nova baixa |
| ADR-003 | Falha entre entrega e baixa | Resultado e procedimento operacional precisam ser definidos |
| ADR-004 | Dados posteriores ao checkpoint | Processar somente dados novos, se batch for aprovado |
| ADR-004 | Falha parcial do job | Retry/reprocessamento sem duplicar saidas, se pipeline for aprovado |
| ADR-004 | Jobs sobrepostos | Lock/checkpoint impedem concorrencia destrutiva, se pipeline for aprovado |
| ADR-004 | Dados degradados | Rejeitar ou colocar em quarentena conforme regra futura |
| ADR-004 | Modelo candidato pior | Nao promover sem gate e baseline aprovado |
| ADR-004 | Rollback | Retornar a versao anterior registrada, se houver registry aprovado |
| ADR-004 | Leakage ou dados pessoais indevidos | Bloquear uso ate revisao de finalidade, LGPD e leakage |

## 6. Ambiguidades, lacunas e perguntas

### Ambiguidades

- O pagamento pelo site foi incluído por correção do solicitante, mas nao ha provedor, formas efetivas, estados, confirmacao, estorno ou conciliacao.
- Os RFCs dizem que pagamentos aceitos sao Pix, debito e credito, mas nao esclarecem se todos estarao disponiveis no site-PDV.
- O comportamento para preco alterado entre selecao e registro nao foi definido.
- O comportamento para falha de comunicacao depois de o servidor possivelmente registrar a venda nao foi definido.
- O tratamento de pedido sem quantidade, sem pagamento ou sem preco nao foi especificado.
- A origem, criacao, estados e ciclo de vida das encomendas nao foram definidos.
- O conteudo, validade e protecao contra replay do QR Code nao foram definidos.
- O ciclo de vida de usuarios autorizados nao foi definido.
- O cliente que solicitar comprovante nao tem tratamento documentado.

### Lacunas de teste

- Nao há implementacao para transformar os casos em testes executaveis.
- Nao ha dispositivo ou navegador de Carla.
- Nao ha quantidade de casos para M-04 a M-07.
- Nao ha plano de medicao de M-08 alem da sugestao de monitoramento.
- Nao ha contrato de dados, eventos, batch ou ML.
- Nao ha modelo de dados ou tecnologia para definir testes de concorrencia e atomicidade.

### Perguntas para desbloquear os testes

1. Qual e o provedor do pagamento online e quais estados ele garante?
2. O que acontece quando o pagamento ou o registro fica pendente?
3. Qual e a origem das encomendas e quais estados podem existir?
4. Qual e o formato e a validade do QR Code?
5. Como usuarios sao criados, autenticados, revogados e auditados?
6. Qual dispositivo e navegador Carla usara?
7. Quais metas de M-04 a M-08 serao confirmadas e qual sera a amostra?
8. Existe caso de uso aprovado para batch/ML? Se sim, quais dados e resultado esperado?

## 7. Priorizacao para o ciclo TDD

### P0 — primeiro ciclo

- RN-01/RNF-01: preco centralizado e total consistente.
- RN-04/RNF-02/RNF-09/RNF-10: registro, confirmacao, persistencia e falha sem falso sucesso.
- RN-06/RNF-12: baixa somente apos entrega e sem duplicacao.
- RN-02/RN-03: formas aceitas e confirmacao externa de Pix.
- RNF-11: acesso restrito.

### P1 — segundo ciclo

- RF-11/RF-12 e RNF-08: leitura, localizacao e feedback do QR Code.
- RF-15 a RF-17: consultas do movimento.
- RNF-05 a RNF-07: fluxo de baixa carga, legibilidade e prevencao de erro.
- RN-07: nao emitir comprovante.

### Condicional

- ADR-001, ADR-002 e ADR-003: somente depois de aprovadas as decisões.
- ADR-004: somente depois de finalidade, dados, custo e critérios de ML aprovados.

## 8. Criterio de conclusao do plano

O plano estará pronto para virar suite TDD quando:

- todas as perguntas que bloqueiam resultado esperado tiverem resposta;
- M-04 a M-08 forem confirmadas ou formalmente retiradas;
- dispositivo e navegador forem definidos;
- pagamento online e encomendas tiverem contratos aprovados;
- os ADRs condicionais tiverem status atualizado;
- codigo e testes existirem para receber os casos;
- cada caso puder apontar para uma regra de origem sem depender de suposicao.

## 9. Execucao RED — 2026-09-11 (fase red do TDD)

**Suite:** Maven + JUnit 5 (`pom.xml`, `src/test/java/com/mariacruz/foot/pdv/*RedTest.java`).
**Stubs:** `src/main/java/com/mariacruz/foot/pdv/` — apenas assinaturas
(`CatalogoPrecos`, `RegistroVendas`, `PagamentoPix`, `AtendimentoEncomendas`,
`FormasPagamentoAceitas`, `ControleAcesso` + records `Pedido/ItemPedido/Venda/Encomenda/Usuario`);
todos os métodos lançam `UnsupportedOperationException("RED: ... não implementado")`.
Nenhuma regra de negócio foi implementada.

**Resultado:** `mvn clean test -Dtest='Rn01*,Rn02*,Rn03*,Rn04*,Rn05*,Rn06*,Rn07*,Rnf*'`
→ `Tests run: 75, Failures: 3, Errors: 72, Skipped: 0`.
100% dos 75 testes novos falham pelo motivo esperado (raiz `UnsupportedOperationException`
dos stubs; os 3 `Failures` do `Rnf11AcessoRestritoRedTest.rnf11_estadoProibido_*`
são `assertThrows(SecurityException)` recebendo `UnsupportedOperationException` — ou seja,
também RED por "não implementado").
**Nenhum teste já passa.** Investigação: não há caso mal escrito nem regra já implementada;
todos os stubs estão sem lógica, conforme exigido na fase red.

**Convenção:** nome do teste cita o ID da regra; corpo em Arrange-Act-Assert;
asserções sobre comportamento esperado (valores, estados, pertinência às consultas),
nunca sobre implementação.

### 9.1 Matriz regra → caso do plano → teste → status

Legenda: `RED` = teste escrito, falha esperada por não implementado.
`SEM TESTE` = sem teste automatizado, com justificativa (não inventar requisito).

#### RN-01 — Preços fixos

| Caso do plano (§3) | Teste | Status |
|---|---|---|
| Feliz salgados R$ 8,00 | `Rn01PrecosFixosRedTest.rn01_feliz_salgadoCustaOitoReais` | RED |
| Feliz bolos/tortas R$ 15,00 | `Rn01PrecosFixosRedTest.rn01_feliz_bolosETortasCustamQuinzeReais` | RED |
| Feliz bebidas R$ 6,00 | `Rn01PrecosFixosRedTest.rn01_feliz_bebidaCustaSeisReais` | RED |
| Limite um de cada grupo (R$ 29,00) | `Rn01PrecosFixosRedTest.rn01_limite_pedidoComUmDeCadaGrupoSomaVinteENove` | RED |
| Limite quantidade consistente seleção/revisão/registro | `Rn01PrecosFixosRedTest.rn01_limite_quantidadeConsistenteEntreSelecaoRevisaoERegistro` | RED |
| Estado proibido venda persistida diverge da revisada | `Rn01PrecosFixosRedTest.rn01_estadoProibido_vendaPersistidaNaoDivergeDoPrecoRevisado` | RED |
| Inválida produto sem preço/grupo desconhecido | — | SEM TESTE (bloqueado: comportamento não definido; não presumir preço) |
| Inválida alterar preço na tela | — | SEM TESTE (pergunta aberta; decidir antes do teste) |
| Conflito preço exibido ≠ preço do total | — | SEM TESTE (bloqueado: bloqueio/nova revisão indefinidos) |

#### RN-02 — Pagamentos aceitos

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz Pix | `Rn02PagamentosAceitosRedTest.rn02_feliz_pixPodeSerRegistrado` | RED |
| Feliz débito | `Rn02PagamentosAceitosRedTest.rn02_feliz_debitoPodeSerRegistrado` | RED |
| Feliz crédito | `Rn02PagamentosAceitosRedTest.rn02_feliz_creditoPodeSerRegistrado` | RED |
| Limite três formas agrupadas | `Rn02PagamentosAceitosRedTest.rn02_limite_cadaVendaAgrupadaNaFormaCorrespondente` | RED |
| Inválida forma fora das três | `Rn02PagamentosAceitosRedTest.rn02_invalida_formaForaDasTresNaoEAceita` | RED |
| Estado proibido venda aprovada com forma inválida | `Rn02PagamentosAceitosRedTest.rn02_estadoProibido_vendaAprovadaComFormaForaDasTresERejeitada` | RED |
| Inválida forma ausente | — | SEM TESTE (bloqueado: erro/mensagem não especificados) |
| Conflito pagamento online | — | SEM TESTE (ADR-002 bloqueada; sem contrato do provedor) |

#### RN-03 — Confirmação externa do Pix

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz confirma na maquininha → prossegue | `Rn03PixExternoRedTest.rn03_feliz_confirmacaoExternaPermiteProsseguirRegistro` | RED |
| Limite confirmação antes do registro | `Rn03PixExternoRedTest.rn03_limite_confirmacaoAntesDoRegistroPermiteRegistrarDepois` | RED |
| Inválida sem confirmação externa | `Rn03PixExternoRedTest.rn03_invalida_semConfirmacaoExternaNaoRegistraComoConfirmado` | RED |
| Conflito PDV não substitui maquininha | `Rn03PixExternoRedTest.rn03_conflito_pdvNaoSubstituiValidacaoExterna` | RED |
| Estado proibido Pix concluído sem confirmação | `Rn03PixExternoRedTest.rn03_estadoProibido_pixSemConfirmacaoNaoEApresentadoComoConcluido` | RED |
| Inválida QR da maquininha não validável | — | SEM TESTE (bloqueado: mensagem/recuperação indefinidas) |

#### RN-04 — Toda venda concluída é registrada

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz venda válida registrada + confirmação | `Rn04RegistroVendaRedTest.rn04_feliz_vendaValidaRegistradaComConfirmacaoClara` | RED |
| Limite um item | `Rn04RegistroVendaRedTest.rn04_limite_vendaComUmItemPreservaItemEQuantidade` | RED |
| Limite vários itens | `Rn04RegistroVendaRedTest.rn04_limite_vendaComVariosItensPreservaTodos` | RED |
| Inválida falha antes da confirmação | `Rn04RegistroVendaRedTest.rn04_invalida_falhaAntesDaConfirmacaoNaoApresentaComoConcluida` | RED |
| Estado proibido concluída ausente | `Rn04RegistroVendaRedTest.rn04_estadoProibido_vendaConcluidaAusenteNasConsultasFalha` | RED |
| Estado proibido duplicada | `Rn04RegistroVendaRedTest.rn04_estadoProibido_mesmaVendaDuplicadaNasConsultasFalha` | RED |
| Inválida campo mínimo ausente | — | SEM TESTE (bloqueado: rejeição não detalhada) |
| Conflito reenvio após falha | — | SEM TESTE (condicional: idempotência ADR-001 proposta, não aprovada) |

#### RN-05 — Encomenda paga não gera nova cobrança

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz QR localiza paga, sem cobrança | `Rn05EncomendaPagaRedTest.rn05_feliz_qrLocalizaPagaExibeSemNovaCobranca` | RED |
| Limite elegível confere sem cobrança | `Rn05EncomendaPagaRedTest.rn05_limite_itensLocalizadosElegiveisConferemSemCobrancaAdicional` | RED |
| Inválida QR não localizado | `Rn05EncomendaPagaRedTest.rn05_invalida_qrNaoLocalizadoInformaSemCobrarNemBaixar` | RED |
| Conflito retirada oferece pagamento de já paga | `Rn05EncomendaPagaRedTest.rn05_conflito_fluxoRetiradaNaoOferecePagamentoDeJaPaga` | RED |
| Estado proibido retirada gera venda/cobrança | `Rn05EncomendaPagaRedTest.rn05_estadoProibido_retiradaNaoGeraVendaOuCobrancaNova` | RED |
| Inválida QR sem estado de paga | — | SEM TESTE (bloqueado: origem/estados de encomenda indefinidos) |

#### RN-06 — Retirada somente após conferência e entrega

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz localiza→confere→entrega→baixa | `Rn06RetiradaRedTest.rn06_feliz_localizaConfereEntregaEConfirmaMarcaRealizada` | RED |
| Limite confirmação imediata pós-entrega | `Rn06RetiradaRedTest.rn06_limite_confirmacaoImediataAposEntregaPermiteBaixa` | RED |
| Inválida baixa antes da conferência | `Rn06RetiradaRedTest.rn06_invalida_baixaAntesDaConferenciaExigeConferencia` | RED |
| Inválida baixa antes da entrega | `Rn06RetiradaRedTest.rn06_invalida_baixaAntesDaEntregaNaoMarcaRealizada` | RED |
| Conflito duas tentativas de baixa | `Rn06RetiradaRedTest.rn06_conflito_duasTentativasDeBaixaNaoGeramDuasBaixas` | RED |
| Estado proibido realizada sem conferência+entrega | `Rn06RetiradaRedTest.rn06_estadoProibido_realizadaSemConferenciaEEntregaEProibida` | RED |
| Estado proibido já realizada recebe nova baixa | `Rn06RetiradaRedTest.rn06_estadoProibido_encomendaJaRealizadaNaoRecebeNovaBaixa` | RED |

#### RN-07 — Não emitir comprovante

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz conclui sem comprovante | `Rn07SemComprovanteRedTest.rn07_feliz_vendaConcluidaSemExigirNemEmitirComprovante` | RED |
| Limite qualquer forma mantém regra | `Rn07SemComprovanteRedTest.rn07_limite_qualquerFormaAceitaMantemNaoEmissao` | RED |
| Inválida fluxo exige comprovante | `Rn07SemComprovanteRedTest.rn07_invalida_fluxoNaoExigeComprovanteParaConcluir` | RED |
| Estado proibido emite comprovante | `Rn07SemComprovanteRedTest.rn07_estadoProibido_pdvNaoEmiteComprovante` | RED |
| Conflito cliente solicita comprovante | — | SEM TESTE (pergunta aberta; sem tratamento documentado) |

#### RNF-01 — Consistência dos cálculos

| Caso do plano (§4) | Teste | Status |
|---|---|---|
| Feliz revisão = persistido | `Rnf01ConsistenciaCalculosRedTest.rnf01_feliz_totalRevisaoIgualTotalPersistido` | RED |
| Limite um item | `Rnf01ConsistenciaCalculosRedTest.rnf01_limite_pedidoDeUmItemPreservaIgualdade` | RED |
| Limite vários itens | `Rnf01ConsistenciaCalculosRedTest.rnf01_limite_pedidoDeVariosItensPreservaIgualdade` | RED |
| Conflito preço da tela diverge | `Rnf01ConsistenciaCalculosRedTest.rnf01_conflito_precoDaTelaDivergenteDoCadastradoNaoConclui` | RED |
| Estado proibido revisado ≠ registrado | `Rnf01ConsistenciaCalculosRedTest.rnf01_estadoProibido_valorRevisadoDiferenteDoRegistradoFalha` | RED |
| Inválida alteração entre revisão e registro | — | SEM TESTE (bloqueado: bloqueio/revisão indefinidos) |

#### RNF-02 — Integridade do registro

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz aparece em total/produto/forma | `Rnf02IntegridadeRegistroRedTest.rnf02_feliz_vendaConfirmadaApareceEmTotalProdutoEForma` | RED |
| Limite um item | `Rnf02IntegridadeRegistroRedTest.rnf02_limite_vendaDeUmItemComQuantidadeCorreta` | RED |
| Limite vários itens | `Rnf02IntegridadeRegistroRedTest.rnf02_limite_vendaDeVariosItensComQuantidadesCorretas` | RED |
| Inválida falha antes de confirmar | `Rnf02IntegridadeRegistroRedTest.rnf02_invalida_falhaAntesDeConfirmarNaoApareceComoConfirmada` | RED |
| Estado proibido ausente | `Rnf02IntegridadeRegistroRedTest.rnf02_estadoProibido_registroConfirmadoAusenteFalha` | RED |
| Estado proibido duplicado | `Rnf02IntegridadeRegistroRedTest.rnf02_estadoProibido_registroConfirmadoDuplicadoFalha` | RED |
| Conflito reenvio | — | SEM TESTE (condicional: ADR-001 proposta) |

#### RNF-07/RNF-08 — Prevenção de erro e feedback

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz total destacado + confirmação da baixa | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf07_feliz_totalDestacadoAntesDaVendaEConfirmacaoAntesDaBaixa` | RED |
| Limite QR não localizado | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf08_limite_qrNaoLocalizadoTemMensagemCompreensivel` | RED |
| Limite já realizada | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf08_limite_encomendaJaRealizadaTemMensagemCompreensivel` | RED |
| Limite falha de registro | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf08_limite_falhaDeRegistroTemMensagemCompreensivel` | RED |
| Inválida QR inexistente | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf08_invalida_qrInexistenteInformaNaoLocalizadoENaoBaixa` | RED |
| Estado proibido sucesso sem registro | `Rnf07Rnf08PrevencaoFeedbackRedTest.rnf08_estadoProibido_sucessoSemRegistroConfirmadoEProibido` | RED |
| Conflito erro pagamento online | — | SEM TESTE (ADR-002 bloqueada) |

#### RNF-09/RNF-10 — Persistência e falhas online

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz confirmada permanece disponível | `Rnf09Rnf10PersistenciaFalhasRedTest.rnf09_feliz_vendaConfirmadaPermaneceDisponivelNasConsultas` | RED |
| Limite falha de comunicação | `Rnf09Rnf10PersistenciaFalhasRedTest.rnf10_limite_falhaDeComunicacaoInformaFalhaSemFalsaConclusao` | RED |
| Conflito falhar fechado, sem offline | `Rnf09Rnf10PersistenciaFalhasRedTest.rnf10_conflito_falharFechadoSemOperarOffline` | RED |
| Estado proibido perdida | `Rnf09Rnf10PersistenciaFalhasRedTest.rnf09_estadoProibido_vendaConfirmadaPerdidaFalha` | RED |
| Estado proibido falsa confirmação | `Rnf09Rnf10PersistenciaFalhasRedTest.rnf10_estadoProibido_falsaConfirmacaoFalha` | RED |
| Inválida reenvio após falha | — | SEM TESTE (condicional: ADR-001 propõe idempotência, não aprovada) |

#### RNF-11 — Acesso restrito

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz autorizado registra/consulta/baixa | `Rnf11AcessoRestritoRedTest.rnf11_feliz_usuarioAutorizadoRegistraConsultaEBaixa` | RED |
| Limite cada operação valida autorização | `Rnf11AcessoRestritoRedTest.rnf11_limite_cadaOperacaoProtegidaValidaAutorizacao` | RED |
| Inválida não autorizado | `Rnf11AcessoRestritoRedTest.rnf11_invalida_usuarioNaoAutorizadoTemAcessoNegado` | RED |
| Estado proibido não autorizado opera (3 ops) | `Rnf11AcessoRestritoRedTest.rnf11_estadoProibido_usuarioNaoAutorizadoNaoOpera[registrar/consultar/baixar]` | RED |
| Conflito ciclo criação/revogação/auditoria | — | SEM TESTE (não definido nos RFCs; §6 pergunta 5) |

#### RNF-12 — Proteção contra retirada duplicada

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz elegível baixada uma vez | `Rnf12ProtecaoDuplicadaRedTest.rnf12_feliz_encomendaElegivelBaixadaUmaVez` | RED |
| Limite segunda tentativa/duplo clique | `Rnf12ProtecaoDuplicadaRedTest.rnf12_limite_segundaTentativaOuDuploCliqueNaoGeraSegundaMutacao` | RED |
| Inválida estado já realizado | `Rnf12ProtecaoDuplicadaRedTest.rnf12_invalida_baixaComEstadoJaRealizadoEImpedida` | RED |
| Estado proibido duas baixas | `Rnf12ProtecaoDuplicadaRedTest.rnf12_estadoProibido_duasBaixasParaMesmaEncomendaFalha` | RED |
| Conflito mecanismo transacional | — | SEM TESTE (condicional: ADR-003 propõe baixa condicional, não aprovada; comportamento coberto pelos 4 testes acima) |

#### RNF-13/RNF-14 — Preços centralizados e rastreabilidade

| Caso do plano | Teste | Status |
|---|---|---|
| Feliz fonte central | `Rnf13Rnf14PrecosRastreabilidadeRedTest.rnf13_feliz_selecaoCalculoRevisaoEConsultaUsamFonteCentral` | RED |
| Feliz id + data/hora | `Rnf13Rnf14PrecosRastreabilidadeRedTest.rnf14_feliz_vendaEAlteracaoDeEstadoTemIdEDataHora` | RED |
| Inválida origem divergente | `Rnf13Rnf14PrecosRastreabilidadeRedTest.rnf13_invalida_origemDePrecoDiferenteEntreTelasNaoEAceita` | RED |
| Estado proibido sem id/timestamp | `Rnf13Rnf14PrecosRastreabilidadeRedTest.rnf14_estadoProibido_registroSemIdOuTimestampFalha` | RED |
| Limite governança de alteração | — | SEM TESTE (governança de alteração não definida) |
| Conflito alteração concorrente | — | SEM TESTE (mecanismo pendente; validar estado atual sem contrato) |

### 9.2 Regras SEM teste automatizado nesta fase (destaque exigido)

Nenhum teste foi inventado para estes casos — escrever asserção seria presumir
comportamento fora dos artefatos (vedado pelo §3 e por `AGENTS.md`):

- **RNF-03/RNF-04 (pico e pedidos variados): SEM TESTE unitário.** Motivo: RNF-03/RNF-04
  exigem "responder adequadamente" no pico 20h30–21h; M-04/M-05 são metas provisórias
  (§4, §6, RNF M-04/M-05) e dispositivo/amostra não definidos. Sem critério aprovado,
  qualquer `assertTimeout` seria requisito inventado. Cobertura funcional do fluxo
  (1 item / vários itens) já é exercida pelos testes RN-04/RNF-02 acima.
- **RNF-05/RNF-06 (baixa carga e legibilidade): SEM TESTE unitário.** Motivo: sem tamanho
  de tela/dispositivo, sem contagem de passos e com M-06/M-07 provisórias; trade-off
  "mais conferência × poucos passos" pendente de decisão de UX (§4). Não há asserção
  objetiva derivável da fonte.
- **RNF-15 (navegador compatível): SEM TESTE.** Motivo: matriz de compatibilidade
  inexistente; dispositivo/navegador de Carla não informados (§6 perguntas 6–7, §8).
  Declarar compatibilidade seria falso-positivo (estado proibido do próprio plano).
- **ADRs (todos os casos do §5): SEM TESTE como comportamento obrigatório.**
  ADR-001 proposta, ADR-002 bloqueada, ADR-003 proposta, ADR-004 condicional/`[SEM REQUISITO]`.
  Seus casos só viram teste após aprovação (priorização "Condicional", §7).
- **Casos bloqueados/perguntas dos §§3–4** listados como `SEM TESTE` na matriz acima
  (preço sem cadastro, alteração de preço, forma ausente, QR sem estado, comprovante
  solicitado, idempotência, concorrência, governança de preços). Cada um aponta para
  a pergunta aberta correspondente do §6.

### 9.3 Cobertura final

- Todas as regras **RN-01 a RN-07** possuem ao menos um teste RED (total 39).
- Todas as regras **RNF-01, RNF-02, RNF-07, RNF-08, RNF-09, RNF-10, RNF-11, RNF-12,
  RNF-13, RNF-14** possuem ao menos um teste RED (total 36).
- **RNF-03, RNF-04, RNF-05, RNF-06, RNF-15** estão mapeadas mas SEM teste automatizado,
  com justificativa acima — são as únicas regras sem teste, destacadas conforme exigido.
- Nenhum caso bloqueado/pergunta/condicional recebeu teste inventado.
- Próximo passo (fora da fase red): fase green implementa o menor comportamento para
  fazer cada teste RED passar, sem alterar os testes.
