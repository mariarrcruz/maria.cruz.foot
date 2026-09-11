package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * RED — RNF-01 Consistência dos cálculos (M-01).
 */
class Rnf01ConsistenciaCalculosRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rnf01_feliz_totalRevisaoIgualTotalPersistido() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 2)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.PIX);
        // Assert: igualdade sem arredondamento indevido
        assertEquals(revisado, venda.totalRegistrado());
    }

    @Test
    void rnf01_limite_pedidoDeUmItemPreservaIgualdade() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.PIX);
        // Assert
        assertEquals(new BigDecimal("6.00"), revisado);
        assertEquals(revisado, venda.totalRegistrado());
    }

    @Test
    void rnf01_limite_pedidoDeVariosItensPreservaIgualdade() {
        // Arrange: 1x8 + 2x15 + 1x6 = 44
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(GrupoProduto.SALGADOS, 1),
                new ItemPedido(GrupoProduto.BOLOS_TORTAS, 2),
                new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.CARTAO_DEBITO);
        // Assert
        assertEquals(new BigDecimal("44.00"), revisado);
        assertEquals(revisado, venda.totalRegistrado());
    }

    @Test
    void rnf01_conflito_precoDaTelaDivergenteDoCadastradoNaoConclui() {
        // Arrange: total revisado diverge do catálogo central
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        BigDecimal precoCentral = catalogo.precoDe(GrupoProduto.SALGADOS);
        BigDecimal revisadoDivergente = precoCentral.add(new BigDecimal("1.00"));
        // Act
        Venda venda = registro.registrar(pedido, revisadoDivergente, FormaPagamento.PIX);
        // Assert: não concluir com valor divergente — registrado preserva o central
        assertEquals(precoCentral, venda.totalRegistrado());
    }

    @Test
    void rnf01_estadoProibido_valorRevisadoDiferenteDoRegistradoFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BOLOS_TORTAS, 1)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.CARTAO_CREDITO);
        // Assert
        assertEquals(revisado, venda.totalRegistrado());
    }
}
