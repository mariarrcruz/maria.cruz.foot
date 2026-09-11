package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RNF-12 Proteção contra retirada duplicada (RF-14, M-03).
 */
class Rnf12ProtecaoDuplicadaRedTest {

    private final AtendimentoEncomendas atendimento = new AtendimentoEncomendas();

    @Test
    void rnf12_feliz_encomendaElegivelBaixadaUmaVez() {
        // Arrange: elegível (conferida + entregue)
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-300").orElseThrow()));
        // Act
        Encomenda baixada = atendimento.marcarRealizada(entregue);
        // Assert: baixada uma vez
        assertTrue(baixada.realizada());
    }

    @Test
    void rnf12_limite_segundaTentativaOuDuploCliqueNaoGeraSegundaMutacao() {
        // Arrange: já baixada uma vez
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-301").orElseThrow()));
        Encomenda primeira = atendimento.marcarRealizada(entregue);
        // Act: segunda tentativa (duplo clique / repetição)
        Encomenda segunda = atendimento.marcarRealizada(entregue);
        // Assert: nenhuma segunda mutação — mesmo id, segue realizada
        assertTrue(primeira.realizada());
        assertTrue(segunda.realizada());
        assertTrue(primeira.id().equals(segunda.id()));
    }

    @Test
    void rnf12_invalida_baixaComEstadoJaRealizadoEImpedida() {
        // Arrange: estado atual já realizado
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-302").orElseThrow()));
        Encomenda realizada = atendimento.marcarRealizada(entregue);
        // Act
        Encomenda tentativa = atendimento.marcarRealizada(realizada);
        // Assert: impedir nova baixa (sem nova mutação)
        assertTrue(tentativa.id().equals(realizada.id()));
        assertTrue(tentativa.realizada());
    }

    @Test
    void rnf12_estadoProibido_duasBaixasParaMesmaEncomendaFalha() {
        // Arrange
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-303").orElseThrow()));
        // Act
        Encomenda primeira = atendimento.marcarRealizada(entregue);
        Encomenda segunda = atendimento.marcarRealizada(entregue);
        // Assert: nunca duas baixas distintas — mesmo registro realizado
        assertFalse(!primeira.realizada() || !segunda.realizada());
        assertTrue(primeira.id().equals(segunda.id()));
    }
}
