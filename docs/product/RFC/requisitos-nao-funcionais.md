# Requisitos nao funcionais do PDV da Carla

**Status:** Rascunho para validacao
**Origem:** Elicitacao das personas e do fluxo operacional
**Referencia de qualidade:** ISO/IEC 25010

## Contexto de qualidade

O PDV sera um site online usado por Carla, que atende sozinha durante o intervalo da faculdade, das 20h30 as 21h. Os requisitos abaixo estao organizados pelas caracteristicas de qualidade da ISO/IEC 25010 e possuem metricas acionaveis.

## Escopo relacionado

O sistema registra vendas de salgados, bolos, tortas e bebidas e apoia a retirada de encomendas ja pagas por QR Code.

A operacao offline, o controle de estoque, a venda fiada e a emissao de comprovante estao fora do escopo.

## Requisitos nao funcionais

### Adequacao funcional

**RNF-01 - Consistencia dos calculos**

O sistema deve calcular os totais usando os precos fixos cadastrados, sem arredondamentos indevidos, e deve manter o mesmo valor na revisao e no registro da venda.

**RNF-02 - Integridade do registro**

Uma venda confirmada deve aparecer nas consultas de total do dia, produto e forma de pagamento sem duplicacao.

### Eficiencia de desempenho

**RNF-03 - Agilidade no horario de pico**

As telas de selecao, revisao e conclusao devem responder adequadamente ao atendimento concentrado entre 20h30 e 21h.

**RNF-04 - Operacao com pedidos variados**

O desempenho deve permanecer adequado para pedidos de um item e para pedidos com varios itens.

### Usabilidade

**RNF-05 - Baixa carga operacional**

A interface deve permitir que Carla registre uma venda com poucos passos e sem depender de digitacao extensa.

**RNF-06 - Legibilidade no ponto de venda**

Textos, precos, itens, totais e estados da encomenda devem ser legiveis em ambiente externo e durante o atendimento rapido.

**RNF-07 - Prevencao de erros**

O sistema deve destacar o total antes da conclusao, diferenciar venda nova de encomenda ja paga e solicitar confirmacao antes de marcar uma encomenda como realizada.

**RNF-08 - Feedback de operacao**

Cada acao importante deve apresentar um resultado compreensivel, incluindo venda registrada, QR Code nao localizado, encomenda ja realizada e falha no registro.

### Confiabilidade

**RNF-09 - Persistencia das vendas**

Vendas confirmadas nao devem ser perdidas durante o uso normal do site e devem permanecer disponiveis nas consultas.

**RNF-10 - Tratamento de falhas online**

Quando houver falha de comunicacao com o site, o sistema deve informar Carla claramente e evitar apresentar uma venda como concluida sem confirmacao do registro.

### Seguranca

**RNF-11 - Acesso restrito a operacao**

As funcoes de registrar vendas, consultar vendas e marcar encomendas como realizadas devem ser acessiveis apenas a usuarios autorizados.

**RNF-12 - Protecao contra retirada duplicada**

O sistema deve validar o estado da encomenda no momento da baixa para evitar que a mesma encomenda seja marcada como realizada mais de uma vez.

### Manutenibilidade

**RNF-13 - Precos centralizados**

Os precos dos grupos de produtos devem ser mantidos em uma configuracao centralizada, evitando valores diferentes entre selecao, calculo e consultas.

**RNF-14 - Rastreabilidade dos registros**

Cada venda e cada alteracao de estado de encomenda devem possuir identificacao e data e hora para facilitar conferencia e suporte.

### Compatibilidade e portabilidade

**RNF-15 - Acesso via navegador**

O PDV deve funcionar como site online em um navegador compativel com o dispositivo utilizado por Carla no ponto de venda.

## Metricas ISO/IEC 25010

Uma metrica e acionavel quando possui indicador, unidade, ponto de medicao e criterio de aprovacao. As metas M-04 a M-08 sao sugestoes provisórias e devem ser confirmadas com Carla antes dos testes de aceitacao finais.

| ID | Caracteristica ISO/IEC 25010 | Indicador | Unidade | Ponto de medicao | Criterio de aprovacao |
| --- | --- | --- | --- | --- | --- |
| M-01 | Adequacao funcional | Consistencia do valor da venda | Percentual | Comparar valor da revisao com valor persistido | 100% dos registros com o mesmo valor |
| M-02 | Adequacao funcional | Integridade das vendas confirmadas | Percentual de vendas | Conferir presenca nas consultas e ausencia de duplicacao | 100% localizaveis; 0 duplicacoes |
| M-03 | Seguranca | Retiradas duplicadas | Quantidade de ocorrencias | Contar baixas de encomendas ja realizadas | 0 baixas duplicadas |
| M-04 | Eficiencia de desempenho | Tempo para concluir uma venda | Segundos por venda | Da selecao do primeiro item a confirmacao do registro | 90% das vendas de teste em ate 60 segundos |
| M-05 | Eficiencia de desempenho | Tempo para localizar uma encomenda | Segundos por encomenda | Da leitura do QR Code a exibicao da encomenda | 95% das encomendas em ate 5 segundos |
| M-06 | Usabilidade | Taxa de erro de registro | Percentual de vendas | Vendas corrigidas ou refeitas por item, quantidade, valor ou pagamento | No maximo 5% das vendas de teste |
| M-07 | Usabilidade | Taxa de conclusao sem ajuda | Percentual de operacoes | Operacoes concluidas por Carla sem instrucoes externas | Pelo menos 90% das operacoes de teste |
| M-08 | Confiabilidade | Disponibilidade do site | Percentual de tempo | Periodo das 20h30 as 21h em dias de atendimento | Pelo menos 99% ao mes no horario de atendimento |

## Como medir e revisar as metas

- Medir M-04 a M-07 em teste com Carla, usando pedidos de um e varios itens e retiradas por QR Code.
- Medir M-08 por monitoramento do site durante os dias de atendimento.
- Registrar quantidade de casos, resultado observado e data de cada medicao.
- Revisar as metas com Carla apos a observacao em uso real e registrar a decisao neste RFC.

## Rastreabilidade nao funcional

| Origem validada | Requisitos e metricas relacionados |
| --- | --- |
| Carla atende sozinha no pico das 20h30 as 21h | RNF-03, RNF-05, M-04 |
| Precos fixos | RNF-01, RNF-13, M-01 |
| Registro de toda venda | RNF-02, RNF-09, RNF-14, M-02 |
| Encomenda ja paga por QR Code | RNF-07, RNF-12, M-03, M-05 |
| Site online | RNF-10, RNF-15, M-08 |
| Sem estoque e sem venda fiada | Fora do escopo |

## Pontos para validar antes da implementacao

- Dispositivo e navegador que Carla utilizara para acessar o site.
- Confirmacao das metas provisórias M-04 a M-08.
- Quantidade de casos no teste de usabilidade e desempenho.
