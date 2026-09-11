package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-06 Retirada somente após conferência e entrega (RF-12/RF-13, RNF-07/RNF-12).
 */
class Rn06RetiradaRedTest {

    private final AtendimentoEncomendas atendimento = new AtendimentoEncomendas();

    @Test
    void rn06_feliz_localizaConfereEntregaEConfirmaMarcaRealizada() {
        // Arrange: encomenda localizada
        Encomenda localizada = atendimento.localizarPorQr("ENCOMENDA-100").orElseThrow();
        // Act: conferência → entrega → baixa
        Encomenda conferida = atendimento.conferir(localizada);
        Encomenda entregue = atendimento.entregar(conferida);
        Encomenda realizada = atendimento.marcarRealizada(entregue);
        // Assert: marcada como realizada
        assertTrue(realizada.realizada());
    }

    @Test
    void rn06_limite_confirmacaoImediataAposEntregaPermiteBaixa() {
        // Arrange: conferida e entregue
        Encomenda localizada = atendimento.localizarPorQr("ENCOMENDA-101").orElseThrow();
        Encomenda entregue = atendimento.entregar(atendimento.conferir(localizada));
        // Act
        Encomenda realizada = atendimento.marcarRealizada(entregue);
        // Assert: baixa permitida respeitando o estado atual
        assertTrue(realizada.realizada());
    }

    @Test
    void rn06_invalida_baixaAntesDaConferenciaExigeConferencia() {
        // Arrange: localizada mas não conferida nem entregue
        Encomenda localizada = atendimento.localizarPorQr("ENCOMENDA-102").orElseThrow();
        // Act
        Encomenda tentativa = atendimento.marcarRealizada(localizada);
        // Assert: não marca como realizada sem conferência
        assertFalse(tentativa.realizada());
    }

    @Test
    void rn06_invalida_baixaAntesDaEntregaNaoMarcaRealizada() {
        // Arrange: conferida mas não entregue
        Encomenda localizada = atendimento.localizarPorQr("ENCOMENDA-103").orElseThrow();
        Encomenda conferida = atendimento.conferir(localizada);
        // Act
        Encomenda tentativa = atendimento.marcarRealizada(conferida);
        // Assert: não marca como realizada sem entrega
        assertFalse(tentativa.realizada());
    }

    @Test
    void rn06_conflito_duasTentativasDeBaixaNaoGeramDuasBaixas() {
        // Arrange: fluxo completo uma vez
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-104").orElseThrow()));
        // Act: duas tentativas de baixa
        Encomenda primeira = atendimento.marcarRealizada(entregue);
        Encomenda segunda = atendimento.marcarRealizada(entregue);
        // Assert: primeira realiza; segunda não gera nova mutação (segue realizada, sem duplicar efeito)
        assertTrue(primeira.realizada());
        assertTrue(segunda.realizada());
        // segunda chamada não pode reabrir nem duplicar: continua realizada com mesmo id
        assertTrue(primeira.id().equals(segunda.id()));
    }

    @Test
    void rn06_estadoProibido_realizadaSemConferenciaEEntregaEProibida() {
        // Arrange: sem conferência e sem entrega
        Encomenda localizada = atendimento.localizarPorQr("ENCOMENDA-105").orElseThrow();
        // Act
        Encomenda tentativa = atendimento.marcarRealizada(localizada);
        // Assert: proibido por RN-06
        assertFalse(tentativa.realizada());
    }

    @Test
    void rn06_estadoProibido_encomendaJaRealizadaNaoRecebeNovaBaixa() {
        // Arrange: já realizada
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-106").orElseThrow()));
        Encomenda realizada = atendimento.marcarRealizada(entregue);
        // Act: nova baixa sobre a já realizada
        Encomenda novaTentativa = atendimento.marcarRealizada(realizada);
        // Assert: proibido por RF-14/RNF-12 — segue realizada sem nova mutação
        assertTrue(novaTentativa.realizada());
        assertTrue(novaTentativa.id().equals(realizada.id()));
    }
}
