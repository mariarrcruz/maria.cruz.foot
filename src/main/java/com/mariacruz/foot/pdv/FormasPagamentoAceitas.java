package com.mariacruz.foot.pdv;

/**
 * Formas aceitas (RN-02).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class FormasPagamentoAceitas {

    public boolean aceita(FormaPagamento forma) {
        throw new UnsupportedOperationException("RED: FormasPagamentoAceitas.aceita não implementado");
    }

    public boolean aceitaCodigo(String codigoForma) {
        throw new UnsupportedOperationException("RED: FormasPagamentoAceitas.aceitaCodigo não implementado");
    }
}
