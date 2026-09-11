package com.mariacruz.foot.pdv;

import java.util.Optional;

/**
 * Pix com confirmação externa na maquininha (RN-03).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class PagamentoPix {

    public boolean confirmacaoExternaRecebida(String vendaOuReferencia) {
        throw new UnsupportedOperationException("RED: PagamentoPix.confirmacaoExternaRecebida não implementado");
    }

    public boolean podeRegistrarVenda(String vendaOuReferencia) {
        throw new UnsupportedOperationException("RED: PagamentoPix.podeRegistrarVenda não implementado");
    }

    public Venda registrarAposConfirmacao(Pedido pedido) {
        throw new UnsupportedOperationException("RED: PagamentoPix.registrarAposConfirmacao não implementado");
    }

    public Optional<Venda> registrarSemSubstituirMaquininha(Pedido pedido, boolean confirmacaoExterna) {
        throw new UnsupportedOperationException("RED: PagamentoPix.registrarSemSubstituirMaquininha não implementado");
    }
}
