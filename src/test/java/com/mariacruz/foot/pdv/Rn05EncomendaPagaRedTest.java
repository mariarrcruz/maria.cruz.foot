package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RN-05 Encomenda paga não gera nova cobrança (RF-11 a RF-14).
 */
class Rn05EncomendaPagaRedTest {

    private final AtendimentoEncomendas atendimento = new AtendimentoEncomendas();

    @Test
    void rn05_feliz_qrLocalizaPagaExibeSemNovaCobranca() {
        // Arrange: QR de encomenda já paga
        String qr = "ENCOMENDA-PAGA-001";
        // Act
        Optional<Encomenda> encomenda = atendimento.localizarPorQr(qr);
        Optional<Venda> cobranca = atendimento.cobrancaGeradaNaRetirada(qr);
        // Assert: exibe para conferência e permite retirada sem nova cobrança
        assertTrue(encomenda.isPresent());
        assertTrue(encomenda.orElseThrow().paga());
        assertTrue(cobranca.isEmpty());
    }

    @Test
    void rn05_limite_itensLocalizadosElegiveisConferemSemCobrancaAdicional() {
        // Arrange
        String qr = "ENCOMENDA-PAGA-002";
        // Act
        Encomenda encomenda = atendimento.localizarPorQr(qr).orElseThrow();
        // Assert: itens disponíveis para conferência e nenhuma cobrança adicional
        assertTrue(!encomenda.itens().isEmpty());
        assertTrue(atendimento.cobrancaGeradaNaRetirada(qr).isEmpty());
    }

    @Test
    void rn05_invalida_qrNaoLocalizadoInformaSemCobrarNemBaixar() {
        // Arrange: QR inexistente
        // Act
        Optional<Encomenda> encomenda = atendimento.localizarPorQr("QR-INEXISTENTE-999");
        Optional<Venda> cobranca = atendimento.cobrancaGeradaNaRetirada("QR-INEXISTENTE-999");
        // Assert (RNF-08): não localiza, não cobra e não baixa
        assertTrue(encomenda.isEmpty());
        assertTrue(cobranca.isEmpty());
    }

    @Test
    void rn05_conflito_fluxoRetiradaNaoOferecePagamentoDeJaPaga() {
        // Arrange: encomenda já paga localizada
        String qr = "ENCOMENDA-PAGA-003";
        // Act
        Encomenda encomenda = atendimento.localizarPorQr(qr).orElseThrow();
        Optional<Venda> cobranca = atendimento.cobrancaGeradaNaRetirada(qr);
        // Assert: nenhuma nova cobrança é gerada na retirada
        assertTrue(encomenda.paga());
        assertTrue(cobranca.isEmpty());
    }

    @Test
    void rn05_estadoProibido_retiradaNaoGeraVendaOuCobrancaNova() {
        // Arrange
        String qr = "ENCOMENDA-PAGA-004";
        // Act
        atendimento.localizarPorQr(qr).orElseThrow();
        Optional<Venda> cobranca = atendimento.cobrancaGeradaNaRetirada(qr);
        // Assert: proibido gerar venda/cobrança nova (RN-05)
        assertTrue(cobranca.isEmpty());
        assertEquals(Optional.empty(), cobranca);
    }
}
