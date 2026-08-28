# Requisitos funcionais do PDV da Carla

**Status:** Rascunho para validacao
**Origem:** Elicitacao das personas e do fluxo operacional

## Objetivo do produto

Disponibilizar um PDV online, acessado por um site, para agilizar o registro das vendas presenciais da Carla durante o intervalo da faculdade, das 20h30 as 21h.

## Escopo funcional

O sistema atende Carla, que opera sozinha, e registra vendas de salgados, bolos, tortas e bebidas. Tambem permite a retirada de encomendas ja pagas por leitura de QR Code.

### Fora do escopo

- Controle de estoque.
- Venda fiada.
- Operacao offline.
- Emissao de comprovante ao cliente.

## Requisitos funcionais

### Catalogo e pedido

**RF-01 - Exibir produtos e precos**

O sistema deve exibir os produtos disponiveis para venda com os precos fixos definidos:

- Salgados: R$ 8,00.
- Bolos e tortas: R$ 15,00.
- Bebidas: R$ 6,00.

**RF-02 - Selecionar itens**

O sistema deve permitir que Carla selecione um ou varios itens para compor o pedido.

**RF-03 - Informar quantidades**

O sistema deve permitir informar a quantidade de cada item selecionado.

**RF-04 - Calcular total**

O sistema deve calcular e exibir o total do pedido com base nos itens, quantidades e precos cadastrados.

**RF-05 - Revisar pedido**

O sistema deve permitir que Carla confira os itens, quantidades e total antes de concluir o registro da venda.

### Registro de venda e pagamento

**RF-06 - Selecionar forma de pagamento**

O sistema deve permitir registrar uma das formas de pagamento aceitas: Pix, cartao de debito ou cartao de credito.

**RF-07 - Registrar venda**

O sistema deve registrar cada venda concluida, incluindo no minimo itens, quantidades, valor total, forma de pagamento e data e hora do registro.

**RF-08 - Confirmar venda**

O sistema deve apresentar uma confirmacao clara quando o registro da venda for concluido.

**RF-09 - Validar Pix externamente**

Para vendas por Pix, Carla deve validar o recebimento usando o QR Code da maquininha Mercado Pago. O PDV deve permitir registrar a venda apos essa confirmacao, sem substituir a validacao realizada pela maquininha.

**RF-10 - Nao emitir comprovante**

O sistema nao deve exigir nem emitir comprovante para o cliente ao concluir uma venda.

### Encomendas ja pagas

**RF-11 - Ler QR Code da encomenda**

O sistema deve permitir que Carla leia o QR Code apresentado pelo cliente para localizar uma encomenda ja paga.

**RF-12 - Exibir encomenda localizada**

Apos a leitura do QR Code, o sistema deve exibir os itens e as informacoes necessarias para Carla conferir a encomenda antes da entrega.

**RF-13 - Marcar encomenda como realizada**

Apos conferir e entregar os itens, Carla deve conseguir marcar a encomenda como realizada.

**RF-14 - Impedir baixa duplicada**

O sistema deve informar quando a encomenda ja estiver marcada como realizada e impedir que ela seja baixada novamente como uma nova retirada.

### Consultas de vendas

**RF-15 - Consultar total do dia**

O sistema deve permitir que Carla consulte o total de vendas do dia.

**RF-16 - Consultar vendas por produto**

O sistema deve permitir que Carla consulte a quantidade vendida por produto.

**RF-17 - Consultar vendas por forma de pagamento**

O sistema deve permitir que Carla consulte as vendas agrupadas por Pix, cartao de debito e cartao de credito.

## Regras de negocio funcionais

**RN-01 - Precos fixos:** salgados custam R$ 8,00; bolos e tortas custam R$ 15,00; bebidas custam R$ 6,00.

**RN-02 - Pagamentos aceitos:** Pix, cartao de debito e cartao de credito.

**RN-03 - Pix:** Carla confirma o recebimento pela opcao de QR Code da maquininha Mercado Pago.

**RN-04 - Registro:** toda venda concluida deve ser registrada.

**RN-05 - Encomenda paga:** a encomenda nao deve gerar nova cobranca no momento da retirada.

**RN-06 - Retirada:** a encomenda so deve ser marcada como realizada depois da conferencia e entrega dos itens.

**RN-07 - Comprovante:** o PDV nao deve emitir comprovante ao cliente.

## Rastreabilidade funcional

| Origem validada | Requisitos relacionados |
| --- | --- |
| Carla atende sozinha no pico das 20h30 as 21h | RF-02, RF-05 |
| Precos fixos | RF-01, RF-04, RN-01 |
| Pix, debito e credito | RF-06, RF-09, RF-17, RN-02, RN-03 |
| Registro de toda venda | RF-07, RF-08, RF-15, RF-16, RF-17, RN-04 |
| Encomenda ja paga por QR Code | RF-11, RF-12, RF-13, RF-14, RN-05, RN-06 |
| Sem comprovante | RF-10, RN-07 |
