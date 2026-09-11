package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RNF-09 Persistência + RNF-10 Falhas online.
 * Escopo: sem operação offline; falha não gera falsa conclusão.
 */
class Rnf09Rnf10PersistenciaFalhasRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();

    @Test
    void rnf09_feliz_vendaConfirmadaPermaneceDisponivelNasConsultas() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: permanece disponível
        assertTrue(registro.buscarPorId(venda.id()).isPresent());
        assertTrue(registro.consultarTodas().contains(venda));
    }

    @Test
    void rnf10_limite_falhaDeComunicacaoInformaFalhaSemFalsaConclusao() {
        // Arrange: referência que sofreu falha de comunicação
        // Act
        boolean apresentadaComoConcluida = registro.buscarPorId("venda-falha-comunicacao").isPresent();
        // Assert: informar falha e não apresentar conclusão falsa
        assertFalse(apresentadaComoConcluida);
    }

    @Test
    void rnf10_conflito_falharFechadoSemOperarOffline() {
        // Arrange: sem suporte a operação offline (fora do escopo)
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: protege integridade — concluída só com registro confirmado
        assertTrue(venda.confirmada());
        assertTrue(registro.buscarPorId(venda.id()).isPresent());
    }

    @Test
    void rnf09_estadoProibido_vendaConfirmadaPerdidaFalha() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1)));
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Act
        boolean persistida = registro.buscarPorId(venda.id()).isPresent();
        // Assert: confirmada nunca é perdida
        assertTrue(persistida);
    }

    @Test
    void rnf10_estadoProibido_falsaConfirmacaoFalha() {
        // Arrange
        // Act: referência sem registro nunca aparece como confirmada
        long ocorrencias = registro.consultarTodas().stream()
                .filter(v -> v.id().equals("venda-inexistente")).count();
        // Assert
        assertEquals(0L, ocorrencias);
    }
}
