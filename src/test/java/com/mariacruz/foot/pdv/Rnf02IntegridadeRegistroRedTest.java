package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RNF-02 Integridade do registro (M-02).
 */
class Rnf02IntegridadeRegistroRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rnf02_feliz_vendaConfirmadaApareceEmTotalProdutoEForma() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: localizável nas três consultas
        assertTrue(registro.buscarPorId(venda.id()).isPresent());
        assertTrue(registro.vendasPorForma(FormaPagamento.PIX).contains(venda));
        assertEquals(1, registro.quantidadePorProduto(GrupoProduto.SALGADOS));
    }

    @Test
    void rnf02_limite_vendaDeUmItemComQuantidadeCorreta() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 2)));
        // Act
        registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_DEBITO);
        // Assert
        assertEquals(2, registro.quantidadePorProduto(GrupoProduto.BEBIDAS));
    }

    @Test
    void rnf02_limite_vendaDeVariosItensComQuantidadesCorretas() {
        // Arrange
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(GrupoProduto.SALGADOS, 1),
                new ItemPedido(GrupoProduto.BEBIDAS, 3)));
        // Act
        registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.CARTAO_CREDITO);
        // Assert
        assertEquals(1, registro.quantidadePorProduto(GrupoProduto.SALGADOS));
        assertEquals(3, registro.quantidadePorProduto(GrupoProduto.BEBIDAS));
    }

    @Test
    void rnf02_invalida_falhaAntesDeConfirmarNaoApareceComoConfirmada() {
        // Arrange: referência que nunca foi confirmada
        // Act
        boolean presente = registro.buscarPorId("venda-nao-confirmada").isPresent();
        // Assert: não aparece como venda confirmada
        assertTrue(!presente);
    }

    @Test
    void rnf02_estadoProibido_registroConfirmadoAusenteFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Act
        boolean nasConsultas = registro.consultarTodas().contains(venda)
                && registro.buscarPorId(venda.id()).isPresent();
        // Assert
        assertTrue(nasConsultas);
    }

    @Test
    void rnf02_estadoProibido_registroConfirmadoDuplicadoFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Act
        long ocorrencias = registro.consultarTodas().stream()
                .filter(v -> v.id().equals(venda.id())).count();
        BigDecimal total = registro.totalDoDia();
        // Assert: 0 duplicações; total do dia reflete a venda uma vez
        assertEquals(1L, ocorrencias);
        assertEquals(venda.totalRegistrado(), total);
    }
}
