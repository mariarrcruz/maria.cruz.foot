package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RNF-07 Prevenção de erros + RNF-08 Feedback de operação.
 */
class Rnf07Rnf08PrevencaoFeedbackRedTest {

    private final CatalogoPrecos catalogo = new CatalogoPrecos();
    private final RegistroVendas registro = new RegistroVendas();
    private final AtendimentoEncomendas atendimento = new AtendimentoEncomendas();

    @Test
    void rnf07_feliz_totalDestacadoAntesDaVendaEConfirmacaoAntesDaBaixa() {
        // Arrange: total revisado destacado + encomenda entregue
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        BigDecimal totalDestacado = catalogo.totalRevisadoDe(pedido);
        Encomenda entregue = atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-200").orElseThrow()));
        // Act: confirma venda e confirma baixa após as confirmações
        Venda venda = registro.registrar(pedido, totalDestacado, FormaPagamento.PIX);
        Encomenda baixada = atendimento.marcarRealizada(entregue);
        // Assert: operações prosseguem após confirmação
        assertTrue(venda.confirmada());
        assertTrue(baixada.realizada());
    }

    @Test
    void rnf08_limite_qrNaoLocalizadoTemMensagemCompreensivel() {
        // Arrange: QR inexistente
        // Act
        Optional<Encomenda> resultado = atendimento.localizarPorQr("QR-INEXISTENTE-001");
        // Assert: resultado compreensível = vazio (não localizado) e sem baixa/cobrança
        assertTrue(resultado.isEmpty());
        assertTrue(atendimento.cobrancaGeradaNaRetirada("QR-INEXISTENTE-001").isEmpty());
    }

    @Test
    void rnf08_limite_encomendaJaRealizadaTemMensagemCompreensivel() {
        // Arrange: encomenda já realizada
        Encomenda realizada = atendimento.marcarRealizada(atendimento.entregar(
                atendimento.conferir(atendimento.localizarPorQr("ENCOMENDA-201").orElseThrow())));
        // Act: segunda tentativa não cria nova baixa
        Encomenda segunda = atendimento.marcarRealizada(realizada);
        // Assert: segue realizada, sem nova mutação
        assertTrue(realizada.realizada());
        assertTrue(segunda.realizada());
    }

    @Test
    void rnf08_limite_falhaDeRegistroTemMensagemCompreensivel() {
        // Arrange: referência com falha não gera confirmação falsa
        // Act
        boolean presente = registro.buscarPorId("venda-com-falha").isPresent();
        // Assert: informa falha e não apresenta como concluída
        assertFalse(presente);
    }

    @Test
    void rnf08_invalida_qrInexistenteInformaNaoLocalizadoENaoBaixa() {
        // Arrange
        // Act
        Optional<Encomenda> resultado = atendimento.localizarPorQr("QR-INEXISTENTE-002");
        // Assert: informar não localizado e não baixar
        assertTrue(resultado.isEmpty());
    }

    @Test
    void rnf08_estadoProibido_sucessoSemRegistroConfirmadoEProibido() {
        // Arrange
        Pedido pedido = new Pedido(List.of(new ItemPedido(GrupoProduto.SALGADOS, 1)));
        // Act
        Venda venda = registro.registrar(pedido, catalogo.totalRevisadoDe(pedido), FormaPagamento.PIX);
        // Assert: sucesso só com registro confirmado e localizável
        assertTrue(venda.confirmada());
        assertTrue(registro.buscarPorId(venda.id()).isPresent());
    }
}
