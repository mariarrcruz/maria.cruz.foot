# Avaliação ATAM independente — PDV da Carla

> Documento de análise. Não altera a fonte de verdade (`docs/arquitetura.md`, personas e RFCs).
> Data: 2026-09-04. Entradas lidas: `docs/arquitetura.md` §1-9, `docs/product/personas/carla-operadora.md`, `lucas-estudante.md`, `marina-cliente-recorrente.md`, `docs/product/RFC/requisitos-funcionais.md`, `requisitos-nao-funcionais.md` + correção pagamento site-PDV.
> Regra: M-04 a M-08 são hipóteses provisórias pendentes com Carla — não são fatos. Sem tecnologia, volume, dispositivo/navegador, provedor site-PDV ou origem de encomenda definidos.

## 1. Drivers, atributos e abordagens

**Drivers P0:** integridade registro/valor; prevenção retirada duplicada; operação só por autorizado.
**P1:** agilidade no pico 20h30-21h operadora sozinha; localização rápida QR; clareza/prevenção erro; persistência sem falsa confirmação.
**P2:** manutenibilidade/rastreabilidade.

**Atributos prioritários:** adequação funcional/integridade > segurança > confiabilidade > usabilidade/desempenho no pico > manutenibilidade. Disponibilidade restrita à janela 20h30-21h (M-08 hipótese).

**Abordagens identificadas (lógicas, sem stack):**
A1. Preço centralizado + total selado revisão==persistência.
A2. Registro com id único + idempotency-key, confirmação só pós-commit.
A3. Pagamento dual: (a) Pix externo declarativo, (b) seam site-PDV bloqueada até contrato.
A4. Baixa condicional (`estadoEsperado=pendente` em transação).
A5. Feedback explícito de falha, sem sucesso presumido.
A6. Gate de auth em venda/consulta/baixa.
A7. Extensão batch `[SEM REQUISITO]` somente-leitura — não avaliada como existente.

## 2. Utility tree

```text
Utilidade PDV
├─ Integridade funcional (P0)
│  ├─ total revisado == persistido [M-01 hipótese 100%]
│  └─ venda confirmada localizável sem duplicação [M-02 hipótese]
├─ Segurança (P0)
│  ├─ 0 baixas duplicadas [M-03 hipótese]
│  └─ só autorizado opera [RNF-11]
├─ Confiabilidade (P0/P1)
│  ├─ sem perda pós-confirmação [RNF-09]
│  ├─ sem falsa confirmação em falha [RNF-10]
│  └─ disponível 20h30-21h [M-08 hipótese 99%]
├─ Desempenho pico (P1)
│  ├─ venda fim-a-fim [M-04 hipótese 90%<=60s]
│  └─ QR->exibição [M-05 hipótese 95%<=5s]
├─ Usabilidade (P1)
│  ├─ poucos passos/pouca digitação [RNF-05]
│  ├─ legibilidade externa [RNF-06]
│  └─ erro <=5%, sem ajuda >=90% [M-06/M-07 hipóteses]
└─ Manutenibilidade (P2)
   ├─ preço central [RNF-13]
   └─ id+timestamp em venda/mudança estado [RNF-14]
```

## 3. Cenários (fonte, estímulo, ambiente, artefato, resposta, métrica)

| ID | Fonte | Estímulo | Ambiente | Artefato | Resposta | Métrica | Imp x Dif |
|---|---|---|---|---|---|---|---|
| C-01 venda pico | Carla | pedido 3 itens + Pix externo | pico 20h30-21h fila | Site+API+Base | total selado e confirmação só pós-commit | M-04/M-01/M-02 hipóteses | H x M |
| C-02 falha parcial | Carla | queda comunicação no registra venda | uso normal | RegistroVenda | informa falha e não exibe como concluída; reenvio mesma key não duplica | M-02 0 dup | H x H |
| C-03 baixa duplicada | Carla | relê QR já realizado / duplo clique | pico | Retirada | 2ª tentativa retorna jaRealizada sem nova mutação | M-03=0 hipótese | H x M |
| C-04 QR não localizado | Carla | QR inválido/inexistente | pico | Retirada | msg compreensível + sem baixa | RNF-08 | M x L |
| C-05 indisponibilidade | Sistema | instabilidade no horário atendimento | 20h30-21h | Site | degradado com msg + sem perda confirmada | M-08 hipótese | H x H |
| C-06 acesso indevido | Não-autorizado | tenta venda/consulta/baixa | operação normal | Acesso | nega + audita | RNF-11 | H x M |
| C-07 divergência preço | Carla | preço alterado entre seleção e registro | uso normal | Catálogo | usa fonte única; divergência bloqueia ou re-revisa | M-01 | H x L |
| C-08 pagamento site sem contrato | Cliente | quer pagar no site | pico | Pagamento | seam retorna erro explícito + orienta fluxo externo; jamais marca pago sem estado confirmado | sem métrica — bloqueado | H x H |
| C-09 consulta conferência | Carla | confere total-dia/produto/forma pós-pico | pós-pico | Base+Consultas | só confirmadas aparecem 1x | M-02 | M x L |
| C-10 legibilidade/erro | Lucas/Marina + Carla | ambiente externo + pressa | pico | Site | poucos passos + destaque total + confirmação baixa | M-06/M-07 hipóteses | M x M |

## 4. Avaliação

* **C-01:** A1+A2 atendem se transação for atômica. Sensível a validação declarativa Pix (risco humano). Sem dispositivo definido, M-04 não verificável.
* **C-02:** A2+A5 corretos em lógica, mas sem estratégia recuperação documentada (risco §7 arquitetura). Reenvio exige key persistida no Site — lacuna.
* **C-03:** A4 é a decisão crítica. Exige update condicional com estado + constraint única. Sem isso, M-03 falha sob concorrência mesmo com operadora única (duplo clique).
* **C-04/C-10:** A5 cobre, mas sem teste com Carla (§8 q2) não há evidência.
* **C-05:** Sem SLO além de M-08 hipótese, sem plano offline (fora de escopo) — única resposta honesta é falhar explicitamente.
* **C-06:** A6 enunciado, sem ciclo criar/revogar/auditar (pergunta aberta q4 arquitetura) — não testável.
* **C-07:** A1 resolve se governança de alteração for central; hoje não documentada.
* **C-08:** Maior risco arquitetural. Sem provedor/estados/estorno/conciliação, qualquer atalho (marcar pago otimista) viola P0. Manter bloqueado é correto.
* **C-09:** Depende de RNF-02/14; sem deleção lógica ambígua.

## 5. Sensibilidade, trade-offs, riscos, não-riscos, temas

**Pontos de sensibilidade:**
S1. Atomicidade registro → C-01/C-02/C-09.
S2. Guarda condicional baixa → C-03.
S3. Gestão idempotency-key no Site → C-02.
S4. Centralização preço → C-07.
S5. Definição estados pagamento site → C-08.

**Trade-offs:**
T1. Agilidade pico vs. confirmações anti-erro (RNF-05 vs RNF-07) — passos a mais protegem M-03/M-06 mas ameaçam M-04.
T2. Fail-closed em falha/pagamento indefinido vs. disponibilidade percebida — protege integridade, piora M-08.
T3. Legibilidade externa vs. densidade de info (RNF-06 vs conferência).

**Riscos (vinculados):**
R1. Pagamento site sem contrato → venda marcada paga sem prova (C-08, S5).
R2. Sem mecanismo baixa documentado → duplicação (C-03, S2) — mitigado só se A4 implementado.
R3. Sem estratégia reenvio/recuperação → perda/duplicação (C-02, S1/S3).
R4. M-04/05/08 sem dispositivo/casos definidos → aceitação inválida (C-01/C-05).
R5. Origem encomenda indefinida → QR órfão (C-04).
R6. Auth sem ciclo de vida → vazamento/uso indevido (C-06).

**Não-riscos:**
N1. Sem estoque/comprovante/offline — exclusão reduz superfície.
N2. Preços fixos em 3 grupos — simplifica A1.
N3. Operadora única — reduz contenção, exceto duplo clique.

**Temas de risco:** 1) Confirmação confiável (pagamento + registro + baixa). 2) Pico com operadora sozinha. 3) Indefinição operacional (auth, dispositivo, metas, origem encomenda).

## 6. Condicional `[SEM REQUISITO]` — batch/diário/ML

Execução diária, só-dados-novos, falha parcial de job, jobs sobrepostos, degradação de dados, candidato pior, rollback de modelo, indisponibilidade e vazamento analítico: sem requisito, sem dados, sem finalidade aprovada. O termo "cron" não representa um componente existente no repositório; nesta análise, o disparo diário é apenas um cenário hipotético solicitado para avaliação.

### 6.1 Execução diária e dados novos

**Cenário condicional:** uma execução batch recebe registros posteriores ao último ponto processado.

- **Risco:** sem uma finalidade analítica aprovada e sem contrato de evento, não é possível saber quais dados devem ser processados nem qual resultado seria correto.
- **Avaliação:** processamento somente de dados novos pode reduzir custo e tempo, mas não é requisito do PDV nem decisão atual.
- **Pré-condição para considerar:** definir finalidade, origem, esquema, campo de ordenação, atraso aceitável e política de reprocessamento.
- **Controle possível:** watermark/checkpoint persistido por fluxo, avançado somente após a saída estar validada e persistida.

### 6.2 Falha parcial e recuperação

**Cenário condicional:** o job grava parte dos resultados e falha antes de concluir.

- **Risco:** perda, duplicação ou resultado parcialmente atualizado.
- **Avaliação:** retry só é seguro se a operação for idempotente e o checkpoint não avançar antes da conclusão; isso é abordagem futura, não capacidade existente.
- **Controles possíveis:** upsert determinístico, quarentena para registros inválidos, retry limitado para falhas transitórias e reprocessamento desde o último checkpoint válido.
- **Métrica:** nenhuma definida nos requisitos; não propor limiar neste documento.

### 6.3 Jobs sobrepostos e concorrência

**Cenário condicional:** duas execuções do mesmo fluxo começam antes de a primeira terminar.

- **Risco:** avanço concorrente do checkpoint, duplicação de agregados e resultados não determinísticos.
- **Avaliação:** um lock com lease ou mecanismo equivalente pode garantir exclusividade, mas a escolha depende da tecnologia ainda não definida.
- **Controles possíveis:** identificador de execução, lock com expiração, verificação de versão do checkpoint e saída idempotente.
- **Limite:** a operação transacional do PDV não deve depender desse processamento analítico para registrar vendas ou baixas.

### 6.4 Degradação e qualidade dos dados

**Cenário condicional:** chegam eventos com esquema inválido, identificador ausente, duplicidade, valor inconsistente ou atraso temporal.

- **Risco:** agregações incorretas e modelo treinado com dados inadequados.
- **Avaliação:** o lote deve separar rejeitados dos aceitos, preservar a origem e impedir que dados inválidos sejam silenciosamente descartados.
- **Controles possíveis:** validação de esquema e regras de domínio, quarentena, relatório de rejeições e decisão explícita entre bloquear o lote ou continuar parcialmente.
- **Rastreabilidade necessária:** origem, período, checkpoint, versão do esquema, regras aplicadas e motivos de rejeição.

### 6.5 Candidato pior e validação do modelo

**Cenário condicional:** um modelo candidato apresenta resultado inferior ao baseline ou ao modelo atualmente aprovado.

- **Risco:** automatizar decisão ou recomendação pior sem finalidade, métrica ou critério de aceite definidos.
- **Avaliação:** não há caso de uso de ML, variável-alvo, baseline ou métrica documentados; portanto, não há base para declarar um candidato melhor ou promover automaticamente.
- **Pré-condições:** finalidade validada, dados de treino identificados, separação temporal quando aplicável, baseline, métrica aprovada e análise de erro.
- **Decisão provisória:** rejeitar o candidato e manter o modelo anterior, caso exista; se não houver modelo aprovado, não publicar inferência.

### 6.6 Rollback e versionamento

**Cenário condicional:** um modelo ou transformação de dados promovido apresenta regressão ou falha operacional.

- **Risco:** não conseguir reproduzir o resultado anterior ou retornar a uma versão conhecida.
- **Avaliação:** rollback exige versões imutáveis de código, dados, configuração, features e modelo, além de um registro do que foi promovido.
- **Controles possíveis:** registry com estados, aprovação manual, ponteiro para versão ativa e retenção da versão anterior.
- **Limite:** não definir ferramenta, política de retenção ou prazo, pois esses dados não constam nos requisitos.

### 6.7 Indisponibilidade

**Cenário condicional:** o processamento analítico ou uma dependência de dados fica indisponível.

- **Risco:** confundir indisponibilidade analítica com falha do PDV ou bloquear o atendimento.
- **Avaliação:** o batch deve falhar de forma observável e ser recuperável sem alterar o estado transacional. Para o site, a indisponibilidade já é tratada em C-05 segundo RNF-10 e M-08, esta última ainda hipótese.
- **Controle possível:** isolamento entre os caminhos e alerta de execução atrasada, sem afirmar disponibilidade ou tempo de recuperação não documentados.

### 6.8 Vazamento analítico e segurança

**Cenário condicional:** atributos usados no treinamento carregam informação posterior ao momento que se pretende prever, ou dados pessoais são copiados sem necessidade.

- **Risco:** avaliação artificialmente otimista, exposição de dados e uso incompatível com a finalidade.
- **Avaliação:** sem finalidade de ML e sem inventário de dados pessoais, o risco não pode ser quantificado, mas deve bloquear o uso do modelo até revisão.
- **Controles possíveis:** corte temporal das features, revisão de leakage, minimização, pseudonimização/anonimização quando aplicável, controle de acesso, criptografia, logs sem dados sensíveis e retenção definida.
- **Dependência:** base legal, dados coletados, retenção e papéis LGPD são perguntas abertas em `docs/arquitetura.md` §8.

### 6.9 Condições de saída da análise condicional

Se a extensão batch/ML for aprovada futuramente, ela deve trazer ao menos: finalidade e consumidor do resultado; contrato dos dados; watermark/checkpoint; idempotência; controle de concorrência; retry e recuperação; regras de qualidade e quarentena; linhagem; reprodutibilidade; validação do modelo; registry; promoção e rollback; observabilidade; segurança/LGPD; e estimativa de custo baseada em volume real. Nenhum desses itens é afirmado como implementado ou decidido neste documento.

**Bloqueadores para fechar ATAM:** responder `docs/arquitetura.md` §8 q1-q7 + confirmar M-04/08 + contrato pagamento site + origem encomenda.
