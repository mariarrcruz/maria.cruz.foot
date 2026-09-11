package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-04 Toda venda concluída é registrada (RF-07/RF-08, RNF-02/RNF-09).
 */
class Rn04RegistroVendaRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rn04_feliz_vendaValidaRegistradaComConfirmacaoClara() {
        // Arrange: itens, quantidades, total, pagamento e data/hora válidos
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 2)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.CARTAO_DEBITO);
        // Assert: registrada com os dados e confirmação clara
        assertTrue(venda.confirmada());
        assertEquals(1, venda.itens().size());
        assertEquals(2, venda.itens().get(0).quantidade());
        assertEquals(revisado, venda.totalRegistrado());
        assertEquals(FormaPagamento.CARTAO_DEBITO, venda.formaPagamento());
        assertNotNull(venda.dataHora());
    }

    @Test
    void rn04_limite_vendaComUmItemPreservaItemEQuantidade() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.PIX);
        // Assert
        assertEquals(1, venda.itens().size());
        assertEquals(GrupoProduto.BEBIDAS, venda.itens().get(0).grupo());
        assertEquals(1, venda.itens().get(0).quantidade());
    }

    @Test
    void rn04_limite_vendaComVariosItensPreservaTodos() {
        // Arrange
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(GrupoProduto.SALGADOS, 1),
                new ItemPedido(GrupoProduto.BOLOS_TORTAS, 2),
                new ItemPedido(GrupoProduto.BEBIDAS, 3)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        // Act
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.CARTAO_CREDITO);
        // Assert
        assertEquals(3, venda.itens().size());
        assertEquals(1, venda.itens().get(0).quantidade());
        assertEquals(2, venda.itens().get(1).quantidade());
        assertEquals(3, venda.itens().get(2).quantidade());
    }

    @Test
    void rn04_invalida_falhaAntesDaConfirmacaoNaoApresentaComoConcluida() {
        // Arrange: falha de comunicação simulada — busca por id inexistente
        // Act: nenhuma venda confirmada para referência que falhou
        boolean presente = registro.buscarPorId("venda-que-falhou").isPresent();
        // Assert: não apresentar venda como concluída (RNF-10)
        assertTrue(!presente);
    }

    @Test
    void rn04_estadoProibido_vendaConcluidaAusenteNasConsultasFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Act
        boolean encontrada = registro.buscarPorId(venda.id()).isPresent()
                && registro.consultarTodas().contains(venda);
        // Assert: concluída deve estar nas consultas
        assertTrue(encontrada);
    }

    @Test
    void rn04_estadoProibido_mesmaVendaDuplicadaNasConsultasFalha() {
        // Arrange: uma venda registrada uma vez
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Act
        long ocorrencias = registro.consultarTodas().stream()
                .filter(v -> v.id().equals(venda.id())).count();
        // Assert: aparece exatamente uma vez
        assertEquals(1L, ocorrencias);
    }
}
