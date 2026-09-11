# ADR-002 — Pagamento pelo site condicionado a contrato

- **Status:** Bloqueada
- **Data:** 2026-09-11
- **Escopo:** pagamento realizado pelo site-PDV
- **Fonte:** correção de escopo do solicitante, avaliação ATAM e `docs/product/arquitetura/arquitetura.md` §§1, 3, 7 e 8

## Contexto

O escopo foi corrigido para permitir pagamento pelo site-PDV. Os documentos definem Pix, débito e crédito, mas não definem provedor online, estados, confirmação, falha, estorno ou conciliação.

O Pix documentado continua sendo validado por Carla na maquininha Mercado Pago. O PDV não deve substituir essa validação externa.

## Forças

- Evita marcar pagamento como aprovado sem confirmação confiável.
- Mantém separado o Pix externo da futura integração online.
- Permite trocar o provedor sem acoplar as regras de venda ao fornecedor.
- Protege o driver P0 de integridade do registro e do valor.

## Alternativas consideradas

1. Marcar a venda como paga imediatamente após iniciar o pagamento.
2. Escolher um provedor e seus estados sem validação do negócio.
3. Usar confirmação manual para o pagamento online.
4. Bloquear a confirmação da venda até existir contrato operacional e técnico aprovado.

As alternativas 1 e 2 podem produzir venda paga sem prova. A alternativa 3 exige regras adicionais não documentadas.

## Decisão proposta

Manter o fluxo de pagamento pelo site bloqueado até definir e aprovar:

- provedor e formas de pagamento;
- estados do pagamento e transições válidas;
- confirmação síncrona, webhook ou consulta;
- idempotência do pagamento;
- recusas, expiração, cancelamento e falhas;
- estorno e conciliação;
- relação entre estado de pagamento e confirmação da venda.

Enquanto isso, nenhum estado iniciado ou pendente deve ser apresentado como pagamento confirmado.

## Consequências

### Positivas

- Evita falsa confirmação e inconsistência financeira.
- Expõe as decisões de negócio que ainda faltam.
- Preserva o fluxo Pix externo documentado.

### Negativas

- O novo pagamento online não pode ser considerado pronto para implementação.
- Pode reduzir a disponibilidade percebida até existir fluxo alternativo aprovado.
- A escolha do provedor e os custos permanecem indefinidos.

## Riscos

- A operação pode não saber como agir diante de pagamento pendente.
- Falta de conciliação pode causar divergência entre provedor e PDV.
- Webhooks sem autenticação ou idempotência podem gerar atualização indevida.

## Requisitos relacionados

- RF-06, RF-07 e RF-09.
- RN-02 e RN-03.
- ASR-04 e driver P0 de integridade.
- Cenário ATAM C-08.

## Pendências para desbloqueio

- Responder `docs/arquitetura.md` §8, pergunta 7.
- Aprovar contrato de estados, confirmação, estorno e conciliação.
- Definir tratamento operacional de falhas e tentativas ambíguas.
- Avaliar segurança, LGPD e custo do provedor escolhido.
