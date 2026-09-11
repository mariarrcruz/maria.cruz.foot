# Arquitetura do PDV da Carla

## 1. Contexto e objetivo

### Fatos

O produto e um PDV online, acessado por um site, para registrar vendas presenciais de salgados, bolos, tortas e bebidas durante o intervalo da faculdade, das 20h30 as 21h. Carla opera sozinha no ponto de venda. O sistema tambem apoia a retirada de encomendas ja pagas por leitura de QR Code. (Origem: `docs/product/RFC/requisitos-funcionais.md`, Objetivo do produto e Escopo funcional; personas de Carla, Lucas e Marina.)

O objetivo arquitetural inicial e sustentar o registro correto e agil de pedidos e pagamentos, inclusive pagamentos realizados pelo proprio site-PDV, a consulta das vendas e a localizacao e baixa controlada de encomendas ja pagas, respeitando os requisitos e as exclusoes documentadas.

### Escopo e restricoes conhecidas

- Incluido: catalogo e precos, selecao de itens, quantidades, calculo, revisao, registro de venda, formas de pagamento, confirmacao, retirada por QR Code e consultas de vendas. (Origem: RF-01 a RF-17.)
- Correcao de escopo informada pelo solicitante: o pagamento tambem podera ser realizado pelo site-PDV. Os artefatos atuais documentam as formas aceitas e o registro da forma de pagamento, mas nao definem provedor, integracao, estados de pagamento, confirmacao, estorno ou conciliacao para esse novo fluxo.
- Excluido: controle de estoque, venda fiada, operacao offline e emissao de comprovante ao cliente. (Origem: `docs/product/RFC/requisitos-funcionais.md`, Fora do escopo; `docs/product/RFC/requisitos-nao-funcionais.md`, Escopo relacionado; Carla, Fora do escopo.)
- O Pix e validado por Carla usando o QR Code da maquininha Mercado Pago; o PDV apenas permite registrar a venda apos essa confirmacao e nao substitui a validacao externa. (Origem: RN-03, RF-09 e Observacao sobre Pix em `docs/product/personas/carla-operadora.md`.)

## 2. Stakeholders e personas

| Stakeholder/persona | Papel e necessidades relevantes | Origem |
| --- | --- | --- |
| Carla | Persona primaria; opera sozinha, registra vendas, valida Pix externamente, localiza e entrega encomendas, consulta o movimento. Precisa de baixa carga operacional, legibilidade, confirmacoes e protecao contra erros. | `docs/product/personas/carla-operadora.md` |
| Lucas | Persona secundaria; estudante com pressa. Precisa reconhecer produtos e precos, confirmar pedido e valor e pagar sem etapas desnecessarias. | `docs/product/personas/lucas-estudante.md` |
| Marina | Persona secundaria; cliente recorrente. Precisa reconhecer rapidamente itens e combinacoes comuns e concluir compras com pouca interacao. | `docs/product/personas/marina-cliente-recorrente.md` |
| Negocio da Carla | Beneficiario operacional; precisa de registro confiavel, conferencia das vendas e retirada correta das encomendas. | Personas e RFCs funcionais/normais |

Nao ha outro usuario, papel administrativo ou sistema externo documentado alem da maquininha Mercado Pago para a validacao do Pix.

## 3. Requisitos arquiteturalmente significativos

| ID | Implicacao arquitetural | Origem |
| --- | --- | --- |
| ASR-01 | Precos devem ter uma fonte centralizada e ser usados de modo consistente na selecao, no calculo, na revisao e nas consultas. | RN-01, RF-01, RF-04, RNF-01, RNF-13, M-01 |
| ASR-02 | O registro de venda deve preservar itens, quantidades, valor total, forma de pagamento e data/hora, com identificacao e sem duplicacao nas consultas. | RF-07, RN-04, RNF-02, RNF-09, RNF-14, M-02 |
| ASR-03 | O fluxo de venda deve separar selecao, revisao, confirmacao e registro, mantendo o mesmo total entre revisao e persistencia. | RF-02 a RF-08, RNF-01, RNF-05, RNF-07 |
| ASR-04 | O sistema deve suportar pagamento pelo site-PDV, mantendo tambem o fluxo de Pix documentado em que Carla confirma o recebimento externamente na maquininha Mercado Pago. O mecanismo de escolha e confirmacao entre os fluxos ainda precisa ser definido. | Correcao informada pelo solicitante; RF-06, RF-09, RN-02, RN-03 |
| ASR-05 | A retirada deve localizar a encomenda por QR Code, mostrar itens antes da entrega e permitir baixa somente depois da conferencia e entrega. | RF-11 a RF-14, RN-05, RN-06 |
| ASR-06 | A baixa de encomenda deve validar o estado no momento da operacao e impedir baixa duplicada. | RF-14, RNF-12, M-03 |
| ASR-07 | Falhas de comunicacao nao podem resultar em uma venda apresentada como concluida sem confirmacao do registro. | RNF-09, RNF-10, RNF-08 |
| ASR-08 | As operacoes de venda, consulta e baixa devem ser restritas a usuarios autorizados. | RNF-11 |
| ASR-09 | A interface deve favorecer poucos passos, pouca digitacao, legibilidade em ambiente externo e feedback compreensivel no pico das 20h30 as 21h. | RNF-03, RNF-05 a RNF-08; Carla, Lucas e Marina |

## 4. Atributos de qualidade

| Atributo | Expectativa documentada | Evidencia/medida |
| --- | --- | --- |
| Adequacao funcional e integridade | Calculos consistentes; vendas confirmadas localizaveis nas consultas e sem duplicacao. | RNF-01, RNF-02, M-01 e M-02 |
| Eficiencia de desempenho | Resposta adequada no pico e para pedidos de um ou varios itens. | RNF-03, RNF-04; M-04: 90% das vendas de teste em ate 60 segundos; M-05: 95% das encomendas em ate 5 segundos |
| Usabilidade | Poucos passos, pouca digitacao, legibilidade, prevencao de erros e feedback claro. | RNF-05 a RNF-08; M-06: no maximo 5% de erros; M-07: pelo menos 90% sem ajuda |
| Confiabilidade | Vendas confirmadas persistem; falhas online sao comunicadas e nao geram falsa conclusao. | RNF-09 e RNF-10 |
| Seguranca | Operacoes restritas a usuarios autorizados e baixa protegida contra duplicacao. | RNF-11 e RNF-12; M-03: 0 baixas duplicadas |
| Manutenibilidade e rastreabilidade | Precos centralizados; vendas e alteracoes de estado identificadas e datadas. | RNF-13 e RNF-14 |
| Compatibilidade e portabilidade | Acesso como site online em navegador compativel com o dispositivo usado por Carla. | RNF-15 |
| Disponibilidade | Site disponivel durante o horario de atendimento. | M-08: pelo menos 99% ao mes entre 20h30 e 21h |

M-04 a M-08 sao metas provisórias e dependem de confirmacao com Carla. (Origem: `docs/product/RFC/requisitos-nao-funcionais.md`, Metricas ISO/IEC 25010 e Como medir e revisar as metas.)

## 5. Drivers priorizados e origem

Prioridade inicial baseada no impacto direto no atendimento, na integridade financeira/operacional e nas metas documentadas. A ordem e uma priorizacao arquitetural inicial, nao uma alteracao dos requisitos.

| Prioridade | Driver | Motivo | Origem |
| --- | --- | --- | --- |
| P0 | Integridade do registro e do valor | Uma venda deve ter valor correto, ser registrada uma vez e aparecer nas consultas, independentemente de o pagamento ocorrer no site-PDV ou pelo fluxo externo documentado. | RN-01, RN-04, RF-04, RF-07, RF-15 a RF-17, RNF-01, RNF-02, RNF-09, M-01 e M-02; correcao informada pelo solicitante |
| P0 | Prevencao de retirada duplicada | Uma encomenda paga nao pode ser cobrada ou baixada novamente; a baixa ocorre somente apos entrega. | RN-05, RN-06, RF-11 a RF-14, RNF-07, RNF-12, M-03 |
| P0 | Operacao segura por usuario autorizado | Registrar, consultar, pagar pelo site-PDV e baixar encomendas sao operacoes protegidas. | RNF-11; correcao informada pelo solicitante |
| P1 | Agilidade no pico | Carla opera sozinha e atende concentradamente entre 20h30 e 21h; o fluxo deve exigir poucos passos. | Personas; RF-02, RF-05; RNF-03, RNF-05; M-04 |
| P1 | Localizacao rapida de encomenda | A retirada por QR Code precisa exibir a encomenda dentro da meta provisoria de localizacao. | RF-11 e RF-12; RNF-07, RNF-08; M-05 |
| P1 | Clareza e prevencao de erro | O usuario precisa confirmar item, quantidade, total, estado da encomenda e resultado da operacao. | RF-05, RF-08, RF-12, RF-14; RNF-06 a RNF-08; M-06 e M-07 |
| P1 | Persistencia e comportamento explicito em falha online | O sistema nao deve perder vendas nem afirmar conclusao sem registro confirmado, inclusive quando o pagamento ocorre pelo site-PDV. | RNF-08 a RNF-10; M-08; correcao informada pelo solicitante |
| P2 | Manutenibilidade e rastreabilidade | Precos e registros devem ser centralizados, identificados e datados para conferencia e suporte. | RNF-13 e RNF-14 |

## 6. Glossario

- **PDV:** ponto de venda online usado para registrar vendas presenciais.
- **Venda concluida/confirmada:** venda cujo registro foi efetivamente concluido e pode ser apresentado como confirmado ao operador. (RF-07, RF-08, RNF-10.)
- **Encomenda ja paga:** pedido que deve ser localizado por QR Code e entregue sem nova cobranca. (RF-11, RN-05.)
- **Baixa:** marcacao de uma encomenda como realizada depois da conferencia e entrega. (RF-13, RN-06.)
- **Horario de pico:** intervalo das 20h30 as 21h, quando Carla atende sozinha. (Personas e RFCs.)

## 7. Riscos iniciais

| Risco | Impacto | Evidencia/lacuna relacionada |
| --- | --- | --- |
| Meta de desempenho pode nao refletir o uso real | Priorizacao ou aceitacao inadequada do fluxo no pico. | M-04 a M-08 sao provisorias; quantidade de casos de teste ainda nao definida. |
| Dispositivo e navegador incompatveis | Carla pode nao conseguir operar o site no ponto de venda. | RNF-15 e ponto de validacao sem dispositivo/navegador definidos. |
| Falha online durante o registro | Perda, duplicacao ou falsa confirmacao de venda. | RNF-09 e RNF-10 exigem comportamento, mas nao definem estrategia operacional de recuperacao. |
| Concorrencia ou repeticao na baixa | Encomenda pode ser entregue ou marcada duas vezes. | RF-14, RNF-12 e M-03 exigem protecao, mas nao definem mecanismo. |
| Pagamento pelo site sem contrato definido | Venda pode ser marcada como paga sem confirmacao confiavel, ou pode faltar tratamento de falha, estorno e conciliacao. | Correcao informada pelo solicitante; nao ha provedor, integracao ou estados de pagamento documentados. |
| Validacao de Pix fora do PDV | No fluxo documentado, a venda pode ser registrada sem confirmacao correta da maquininha. | RF-09 e RN-03; nao ha integracao ou contrato tecnico documentado. |
| Alteracao de precos em fonte inconsistente | Divergencia entre selecao, calculo, revisao e consultas. | RN-01, RNF-01 e RNF-13; governanca de alteracao nao documentada. |

## 8. Fatos, lacunas, conflitos, suposicoes e perguntas abertas

### Fatos

- As tres personas estao marcadas como validadas.
- Os RFCs funcional e nao funcional estao marcados como rascunho para validacao.
- Os pagamentos aceitos sao Pix, cartao de debito e cartao de credito.
- O solicitante corrigiu o escopo: tambem sera possivel pagar pelo site-PDV.
- Nao ha estoque, venda fiada, operacao offline ou comprovante no escopo.
- As metas M-04 a M-08 sao provisórias.

### Lacunas

- Nao ha tecnologia, topologia, modelo de dados, estrategia de autenticacao ou mecanismo de QR Code definidos.
- Nao ha volume de vendas, quantidade de produtos, quantidade de encomendas, tamanho de dados ou crescimento documentados.
- Nao ha definicao de dispositivo/navegador de Carla nem de quantidade de casos de teste.
- Nao ha documento de decisoes arquiteturais anteriores localizado entre os artefatos consultados.
- O pagamento pelo site-PDV ainda nao possui especificacao operacional ou tecnica.

### Conflitos

- Nao foi identificado conflito textual entre os artefatos consultados.
- Ha uma tensao de validacao, nao um conflito: personas estao validadas, mas os RFCs ainda sao rascunhos e as metas M-04 a M-08 aguardam confirmacao.

### Suposicoes controladas

- O pagamento pelo site-PDV usara as mesmas formas de pagamento ja documentadas; isso e uma interpretacao provisoria da expressao “tambem”, sujeita a confirmacao.

### Perguntas abertas

1. Qual dispositivo e navegador Carla usara no ponto de venda?
2. Quais sao as metas finais de M-04 a M-08 e quantos casos comporao os testes?
3. Qual e o comportamento esperado para uma venda interrompida, repetida ou reenviada apos falha de comunicacao?
4. Como usuarios autorizados serao criados, autenticados, revogados e auditados?
5. Qual origem e ciclo de vida das encomendas ja pagas, ja que os RFCs definem a retirada, mas nao o cadastro ou a criacao da encomenda?
6. Qual e o contrato operacional entre o PDV e a confirmacao feita na maquininha Mercado Pago?
7. No pagamento pelo site-PDV, quais formas serao aceitas, qual provedor sera usado e quais estados, confirmacoes, falhas, estornos e conciliacoes devem ser suportados?

## 9. Fontes consultadas

- `docs/product/personas/carla-operadora.md`
- `docs/product/personas/lucas-estudante.md`
- `docs/product/personas/marina-cliente-recorrente.md`
- `docs/product/RFC/requisitos-funcionais.md`
- `docs/product/RFC/requisitos-nao-funcionais.md`

Nao foram encontrados, no conjunto documental localizado, arquivos separados de regras de negocio, restricoes adicionais ou decisoes arquiteturais anteriores. A possibilidade de pagamento pelo site-PDV foi acrescentada por correcao do solicitante nesta conversa e ainda precisa ser refletida nos RFCs para eliminar a divergencia documental.