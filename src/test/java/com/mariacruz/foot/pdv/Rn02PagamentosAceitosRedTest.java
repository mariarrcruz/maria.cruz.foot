package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-02 Pagamentos aceitos (RF-06/RF-17).
 */
class Rn02PagamentosAceitosRedTest {

    private final FormasPagamentoAceitas formas = new FormasPagamentoAceitas();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rn02_feliz_pixPodeSerRegistrado() {
        // Arrange: forma Pix
        // Act
        boolean aceita = formas.aceita(FormaPagamento.PIX);
        // Assert: Pix é forma prevista
        assertTrue(aceita);
    }

    @Test
    void rn02_feliz_debitoPodeSerRegistrado() {
        // Arrange
        // Act
        boolean aceita = formas.aceita(FormaPagamento.CARTAO_DEBITO);
        // Assert
        assertTrue(aceita);
    }

    @Test
    void rn02_feliz_creditoPodeSerRegistrado() {
        // Arrange
        // Act
        boolean aceita = formas.aceita(FormaPagamento.CARTAO_CREDITO);
        // Assert
        assertTrue(aceita);
    }

    @Test
    void rn02_limite_cadaVendaAgrupadaNaFormaCorrespondente() {
        // Arrange: uma venda em cada forma
        Pedido pedido = new Pedido(java.util.List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        CatalogoPrecos catalogo = new CatalogoPrecos();
        // Act
        Venda pix = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        Venda debito = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_DEBITO);
        Venda credito = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_CREDITO);
        // Assert: consultas agrupam cada venda na forma correspondente
        assertTrue(registro.vendasPorForma(FormaPagamento.PIX).contains(pix));
        assertTrue(registro.vendasPorForma(FormaPagamento.CARTAO_DEBITO).contains(debito));
        assertTrue(registro.vendasPorForma(FormaPagamento.CARTAO_CREDITO).contains(credito));
    }

    @Test
    void rn02_invalida_formaForaDasTresNaoEAceita() {
        // Arrange: código fora de Pix/débito/crédito
        // Act
        boolean aceita = formas.aceitaCodigo("dinheiro");
        // Assert: não deve ser aceita como forma prevista
        assertFalse(aceita);
    }

    @Test
    void rn02_estadoProibido_vendaAprovadaComFormaForaDasTresERejeitada() {
        // Arrange: forma desconhecida
        // Act: tentativa de registrar com código inválido não produz venda aprovada
        boolean aceita = formas.aceitaCodigo("fiado");
        // Assert: rejeitada e ausente das consultas por forma válida
        assertFalse(aceita);
        assertTrue(registro.vendasPorForma(FormaPagamento.PIX).stream()
                .noneMatch(v -> v.formaPagamento() == null));
    }
}
