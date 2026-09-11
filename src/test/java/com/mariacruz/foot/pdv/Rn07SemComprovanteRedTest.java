package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-07 Não emitir comprovante (RF-10).
 */
class Rn07SemComprovanteRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rn07_feliz_vendaConcluidaSemExigirNemEmitirComprovante() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: confirma sem comprovante
        assertTrue(venda.confirmada());
        assertFalse(venda.comprovanteEmitido());
    }

    @Test
    void rn07_limite_qualquerFormaAceitaMantemNaoEmissao() {
        // Arrange: todas as formas aceitas
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        // Act
        Venda pix = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        Venda debito = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_DEBITO);
        Venda credito = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_CREDITO);
        // Assert: nenhuma emite comprovante
        assertFalse(pix.comprovanteEmitido());
        assertFalse(debito.comprovanteEmitido());
        assertFalse(credito.comprovanteEmitido());
    }

    @Test
    void rn07_invalida_fluxoNaoExigeComprovanteParaConcluir() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BOLOS_TORTAS, 1)));
        BigDecimal revisado = new CatalogoPrecos().totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.CARTAO_CREDITO);
        // Assert: conclui confirmada mesmo sem comprovante
        assertTrue(venda.confirmada());
        assertFalse(venda.comprovanteEmitido());
    }

    @Test
    void rn07_estadoProibido_pdvNaoEmiteComprovante() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: proibido por RN-07/RF-10
        assertFalse(venda.comprovanteEmitido());
    }
}
