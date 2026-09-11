package com.mariacruz.foot.pdv;

import java.util.Optional;

/**
 * Retirada de encomendas já pagas (RN-05 / RN-06 / RNF-12).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class AtendimentoEncomendas {

    public Optional<Encomenda> localizarPorQr(String qrCode) {
        throw new UnsupportedOperationException("RED: AtendimentoEncomendas.localizarPorQr não implementado");
    }

    public Encomenda conferir(Encomenda encomenda) {
        throw new UnsupportedOperationException("RED: AtendimentoEncomendas.conferir não implementado");
    }

    public Encomenda entregar(Encomenda encomenda) {
        throw new UnsupportedOperationException("RED: AtendimentoEncomendas.entregar não implementado");
    }

    public Encomenda marcarRealizada(Encomenda encomenda) {
        throw new UnsupportedOperationException("RED: AtendimentoEncomendas.marcarRealizada não implementado");
    }

    public Optional<Venda> cobrancaGeradaNaRetirada(String qrCode) {
        throw new UnsupportedOperationException("RED: AtendimentoEncomendas.cobrancaGeradaNaRetirada não implementado");
    }
}
