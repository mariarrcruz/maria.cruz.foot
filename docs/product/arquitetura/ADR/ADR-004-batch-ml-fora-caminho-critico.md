# ADR-004 — Batch e ML fora do caminho crítico

- **Status:** Condicional
- **Data:** 2026-09-11
- **Escopo:** processamento diário, dados novos e eventual ML
- **Fonte:** avaliação ATAM; não há requisito correspondente nos RFCs atuais

## Contexto

A avaliação ATAM analisou execução diária, dados novos, falha parcial, jobs sobrepostos, qualidade, candidato pior, rollback, indisponibilidade e vazamento. Entretanto, os requisitos e a arquitetura-base não definem finalidade analítica, consumidor, dados, volume, modelo ou benefício de ML.

Não existe cron, job batch, evento analítico ou pipeline ML documentado no repositório. Esses elementos são apenas extensão hipotética.

## Forças

- Mantém o PDV independente de processamento analítico.
- Evita custo e complexidade sem finalidade aprovada.
- Permite futura evolução somente leitura.
- Reduz risco de vazamento analítico e de modelo não validado.

## Alternativas consideradas

1. Criar execução diária imediatamente.
2. Executar processamento contínuo.
3. Acoplar inferência ao registro da venda.
4. Adiar batch/ML até finalidade, dados e critérios de aceite serem aprovados.

## Decisão proposta

Não criar nem assumir batch/ML como parte do sistema atual. Se a extensão for aprovada futuramente, ela deve permanecer fora do caminho crítico transacional e definir, antes da implementação:

- finalidade e consumidor do resultado;
- contrato e qualidade dos dados;
- processamento incremental, watermark e checkpoint;
- idempotência e controle de jobs sobrepostos;
- retry e recuperação de falha parcial;
- quarentena, linhagem e reprodutibilidade;
- validação temporal, baseline e gates do modelo;
- registry, promoção manual e rollback;
- observabilidade, segurança, LGPD e custo.

## Consequências

### Positivas

- O atendimento não depende de batch ou modelo.
- Não há custo de plataforma sem volume ou uso comprovados.
- Evita tratar hipóteses como componentes existentes.

### Negativas

- Nenhuma análise preditiva ou agregação futura será entregue sem novos requisitos.
- Dados históricos podem não estar preparados para um caso de uso posterior.
- A decisão sobre tecnologia é deliberadamente adiada.

## Riscos

- Uma necessidade futura pode exigir preparação de dados retroativa.
- Sem retenção e linhagem definidas, reprodutibilidade pode ser perdida.
- Sem finalidade e métrica, um candidato pior pode ser promovido indevidamente.
- Dados pessoais podem ser reutilizados fora da finalidade original.

## Requisitos relacionados

- Nenhum requisito atual aprova batch ou ML.
- A decisão é explicitamente `[SEM REQUISITO]`.
- RNF-09, RNF-10 e M-08 reforçam que extensões futuras não devem prejudicar o PDV.

## Pendências para eventual ativação

- Aprovar caso de uso e benefício de negócio.
- Confirmar volume, crescimento, retenção, orçamento e consumidor.
- Definir requisitos de dados, segurança/LGPD e critérios do modelo.
