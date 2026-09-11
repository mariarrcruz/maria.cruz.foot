package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * RED — RNF-13 Preços centralizados + RNF-14 Rastreabilidade (M-01/M-02/M-03).
 */
class Rnf13Rnf14PrecosRastreabilidadeRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();
    private final AtendimentoEncomendas atendimento = new AtendimentoEncomendas();

    @Test
    void rnf13_feliz_selecaoCalculoRevisaoEConsultaUsamFonteCentral() {
        // Arrange: mesma fonte central para todas as etapas
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        java.math.BigDecimal precoSelecao = catalogo.precoDe(GrupoProduto.SALGADOS);
        java.math.BigDecimal totalCalculo = catalogo.totalDe(pedido);
        java.math.BigDecimal totalRevisao = catalogo.totalRevisadoDe(pedido);
        Venda venda = registro.registrar(pedido, totalRevisao, FormaPagamento.PIX);
        // Assert: todas usam a fonte central (R$ 8,00)
        assertEquals(new java.math.BigDecimal("8.00"), precoSelecao);
        assertEquals(new java.math.BigDecimal("8.00"), totalCalculo);
        assertEquals(new java.math.BigDecimal("8.00"), totalRevisao);
        assertEquals(new java.math.BigDecimal("8.00"), venda.totalRegistrado());
    }

    @Test
    void rnf14_feliz_vendaEAlteracaoDeEstadoTemIdEDataHora() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        Encomenda baixada = atendimento.marcarRealizada(atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-400").orElseThrow())));
        // Assert: identificação e data/hora para conferência e suporte
        assertNotNull(venda.id());
        assertNotNull(venda.dataHora());
        assertNotNull(baixada.id());
        assertNotNull(baixada.dataHoraAlteracao());
    }

    @Test
    void rnf13_invalida_origemDePrecoDiferenteEntreTelasNaoEAceita() {
        // Arrange: preço de tela diverge do central
        java.math.BigDecimal central = catalogo.precoDe(GrupoProduto.BEBIDAS);
        java.math.BigDecimal divergente = central.add(new java.math.BigDecimal("2.00"));
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, divergente, FormaPagamento.PIX);
        // Assert: não aceitar divergência — registrado preserva o central
        assertEquals(central, venda.totalRegistrado());
    }

    @Test
    void rnf14_estadoProibido_registroSemIdOuTimestampFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: id e timestamp exigidos
        assertNotNull(venda.id());
        assertNotNull(venda.dataHora());
    }
}
