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
