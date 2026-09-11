# ADR-003 — Baixa condicional de encomenda

- **Status:** Proposta
- **Data:** 2026-09-11
- **Escopo:** leitura de QR Code, conferência, entrega e baixa
- **Fonte:** avaliação ATAM e `docs/product/arquitetura/arquitetura.md` §§3, 5, 7 e 8

## Contexto

Carla deve localizar a encomenda por QR Code, conferir os itens, entregar e marcar a encomenda como realizada. Uma encomenda já realizada não pode ser baixada novamente.

A arquitetura-base exige proteção contra concorrência, mas não define banco, estados ou mecanismo de QR Code.

## Forças

- Protege contra duplo clique e leituras repetidas.
- Faz a baixa depender da situação real no momento da operação.
- Mantém a ordem conferência → entrega → baixa.
- Produz resultado auditável com ator e data/hora.

## Alternativas consideradas

1. Desabilitar o botão somente na interface.
2. Baixar antes da confirmação da entrega.
3. Permitir várias baixas e corrigir posteriormente.
4. Aceitar uma transição condicional do estado esperado para realizado.

As alternativas 1 a 3 não garantem RNF-12 sob repetição ou concorrência.

## Decisão proposta

Adotar uma transição condicional e atômica:

- localizar a encomenda;
- exibir itens e estado;
- exigir confirmação após conferência e entrega;
- aceitar a baixa somente se a encomenda ainda estiver no estado elegível;
- retornar estado já realizado sem nova mutação quando a operação for repetida;
- registrar identificador, ator e data/hora.

## Consequências

### Positivas

- Reduz o risco de retirada duplicada.
- Torna a decisão independente do estado visual antigo da tela.
- Apoia M-03 como hipótese de aceitação, sem transformar a meta em fato.

### Negativas

- Exige controle transacional e contrato de estados.
- A origem e o ciclo de vida da encomenda precisam ser definidos.
- QR Code inválido, expirado ou reutilizado exigirá política específica.

## Riscos

- QR Code pode expor identificador ou permitir replay.
- Uma falha após entrega e antes da baixa exige procedimento operacional.
- Sem origem de encomenda, o fluxo pode localizar registros órfãos ou inexistentes.

## Requisitos relacionados

- RF-11, RF-12, RF-13 e RF-14.
- RN-05 e RN-06.
- RNF-07, RNF-08, RNF-12 e RNF-14.
- Cenários ATAM C-03 e C-04.

## Pendências para aprovação

- Definir origem, estados e ciclo de vida das encomendas.
- Definir formato, validade e proteção do QR Code.
- Definir procedimento para falha entre entrega e baixa.
