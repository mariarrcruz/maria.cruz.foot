package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-03 Confirmação externa do Pix (RF-09).
 * PDV não substitui a maquininha Mercado Pago.
 */
class Rn03PixExternoRedTest {

    private final PagamentoPix pix = new PagamentoPix();

    @Test
    void rn03_feliz_confirmacaoExternaPermiteProsseguirRegistro() {
        // Arrange: Carla confirmou no QR da maquininha
        String referencia = "pix-ref-1";
        // Act
        boolean podeRegistrar = pix.podeRegistrarVenda(referencia);
        Venda venda = pix.registrarAposConfirmacao(
                new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1))));
        // Assert: PDV permite prosseguir e venda é Pix confirmada
        assertTrue(podeRegistrar);
        assertEquals(FormaPagamento.PIX, venda.formaPagamento());
        assertTrue(venda.confirmada());
    }

    @Test
    void rn03_limite_confirmacaoAntesDoRegistroPermiteRegistrarDepois() {
        // Arrange: confirmação externa ocorreu antes do registro no PDV
        String referencia = "pix-ref-2";
        // Act
        boolean confirmadaAntes = pix.confirmacaoExternaRecebida(referencia);
        Venda venda = pix.registrarAposConfirmacao(
                new Pedido(List.of(new ItemPedido(GrupoProduto.BEBIDAS, 1))));
        // Assert: registro pode ocorrer após a confirmação
        assertTrue(confirmadaAntes);
        assertTrue(venda.confirmada());
    }

    @Test
    void rn03_invalida_semConfirmacaoExternaNaoRegistraComoConfirmado() {
        // Arrange: sem confirmação externa
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Optional<Venda> resultado = pix.registrarSemSubstituirMaquininha(pedido, false);
        // Assert: não deve registrar o Pix como confirmado
        assertTrue(resultado.isEmpty() || !resultado.orElseThrow().confirmada());
    }

    @Test
    void rn03_conflito_pdvNaoSubstituiValidacaoExterna() {
        // Arrange: tentativa de confirmar apenas pelo PDV
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Optional<Venda> resultado = pix.registrarSemSubstituirMaquininha(pedido, false);
        // Assert: PDV não confirma sem a validação externa
        assertTrue(resultado.isEmpty() || !resultado.orElseThrow().confirmada());
    }

    @Test
    void rn03_estadoProibido_pixSemConfirmacaoNaoEApresentadoComoConcluido() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Optional<Venda> resultado = pix.registrarSemSubstituirMaquininha(pedido, false);
        // Assert: nunca apresentar como concluída sem confirmação externa
        assertFalse(resultado.isPresent() && resultado.orElseThrow().confirmada());
    }
}
