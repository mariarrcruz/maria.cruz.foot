package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-01 Preços fixos (RF-01/RF-04, RNF-01/RNF-13).
 * Fonte: docs/testes/plano-tdd.md §3 RN-01; requisitos-funcionais.md RN-01.
 */
class Rn01PrecosFixosRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();

    @Test
    void rn01_feliz_salgadoCustaOitoReais() {
        // Arrange: catálogo central, nenhum estado prévio
        // Act: preço do grupo salgados
        BigDecimal preco = catalogo.precoDe(GrupoProduto.SALGADOS);
        // Assert: comportamento esperado R$ 8,00
        assertEquals(new BigDecimal("8.00"), preco);
    }

    @Test
    void rn01_feliz_bolosETortasCustamQuinzeReais() {
        // Arrange
        // Act
        BigDecimal preco = catalogo.precoDe(GrupoProduto.BOLOS_TORTAS);
        // Assert
        assertEquals(new BigDecimal("15.00"), preco);
    }

    @Test
    void rn01_feliz_bebidaCustaSeisReais() {
        // Arrange
        // Act
        BigDecimal preco = catalogo.precoDe(GrupoProduto.BEBIDAS);
        // Assert
        assertEquals(new BigDecimal("6.00"), preco);
    }

    @Test
    void rn01_limite_pedidoComUmDeCadaGrupoSomaVinteENove() {
        // Arrange: um item de cada grupo, qtd 1
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(GrupoProduto.SALGADOS, 1),
                new ItemPedido(GrupoProduto.BOLOS_TORTAS, 1),
                new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        // Act
        BigDecimal total = catalogo.totalDe(pedido);
        // Assert: 8 + 15 + 6 = 29, sem arredondamento indevido
        assertEquals(new BigDecimal("29.00"), total);
    }

    @Test
    void rn01_limite_quantidadeConsistenteEntreSelecaoRevisaoERegistro() {
        // Arrange: 2 salgados + 1 bebida
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(GrupoProduto.SALGADOS, 2),
                new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        // Act: total calculado e total de revisão usam mesma quantidade
        BigDecimal totalCalculado = catalogo.totalDe(pedido);
        BigDecimal totalRevisado = catalogo.totalRevisadoDe(pedido);
        // Assert: 2x8 + 1x6 = 22 e revisão preserva o cálculo
        assertEquals(new BigDecimal("22.00"), totalCalculado);
        assertEquals(totalCalculado, totalRevisado);
    }

    @Test
    void rn01_estadoProibido_vendaPersistidaNaoDivergeDoPrecoRevisado() {
        // Arrange: pedido revisado
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        BigDecimal revisado = catalogo.totalRevisadoDe(pedido);
        RegistroVendas registro = new RegistroVendas();
        // Act: registra a venda concluída
        Venda venda = registro.registrar(pedido, revisado, FormaPagamento.PIX);
        // Assert: valor registrado preserva o revisado (R$ 8,00)
        assertEquals(revisado, venda.totalRegistrado());
        assertEquals(new BigDecimal("8.00"), venda.totalRegistrado());
        assertTrue(venda.confirmada());
    }
}
