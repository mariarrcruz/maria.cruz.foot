# ADR-001 — Autoridade transacional para venda e baixa

- **Status:** Proposta
- **Data:** 2026-09-11
- **Escopo:** venda, registro, consulta e baixa de encomenda
- **Fonte:** avaliação ATAM e `docs/product/arquitetura/arquitetura.md` §§3, 5, 7 e 8

## Contexto

O PDV precisa preservar itens, quantidades, total, forma de pagamento e data/hora da venda. Também precisa impedir baixa duplicada de encomendas e não pode apresentar uma venda como concluída sem confirmação do registro.

Os documentos atuais não definem tecnologia, banco, modelo de dados ou estratégia de recuperação. Esta ADR registra uma abordagem lógica, não uma implementação existente.

## Forças

- Preserva o total revisado no registro.
- Permite confirmação somente após persistência confirmada.
- Reduz duplicação causada por reenvio ou duplo clique.
- Mantém a baixa de encomenda protegida contra concorrência.
- Facilita auditoria por identificador e data/hora.

## Alternativas consideradas

1. Manter estado apenas na interface do site.
2. Gravar vendas sem chave de idempotência.
3. Baixar encomendas sem validar o estado anterior.
4. Usar uma autoridade transacional que valide total, idempotência e transição de estado.

As três primeiras alternativas não protegem adequadamente RNF-02, RNF-09, RNF-10 ou RNF-12.

## Decisão proposta

Adotar uma autoridade transacional para:

- calcular e validar o total usando a fonte central de preços;
- persistir venda e itens com identificador único;
- aceitar uma chave de idempotência para reenvios;
- confirmar a venda somente após o registro persistido;
- efetuar a baixa apenas quando o estado esperado da encomenda ainda for válido;
- registrar ator, identificador e data/hora das mutações.

## Consequências

### Positivas

- Alinha integridade, rastreabilidade e prevenção de duplicação.
- Permite que consultas exibam apenas operações confirmadas.
- Torna falhas e reenvios observáveis.

### Negativas

- Exige contrato de persistência e comportamento para tentativas ambíguas.
- Pode acrescentar uma confirmação ou bloqueio ao fluxo, afetando a agilidade no pico.
- Ainda não define tecnologia ou mecanismo específico.

## Riscos

- Uma chave de idempotência mal gerenciada pode permitir duplicação.
- Sem origem e ciclo de vida da encomenda, o QR Code pode não localizar um registro válido.
- Sem estratégia de recuperação, uma falha após o commit pode confundir a operação.

## Requisitos relacionados

- RF-04, RF-07, RF-08, RF-13 e RF-14.
- RN-01, RN-04 e RN-06.
- RNF-01, RNF-02, RNF-09, RNF-10, RNF-12 e RNF-14.
- Cenários ATAM C-01, C-02, C-03 e C-09.

## Pendências para aprovação

- Definir modelo de dados e tecnologia.
- Definir comportamento para reenvio após falha de comunicação.
- Definir origem, estados e ciclo de vida das encomendas.
