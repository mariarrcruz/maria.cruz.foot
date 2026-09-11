package com.mariacruz.foot.pdv;

/**
 * Acesso restrito (RNF-11).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class ControleAcesso {

    public boolean podeRegistrar(Usuario usuario) {
        throw new UnsupportedOperationException("RED: ControleAcesso.podeRegistrar não implementado");
    }

    public boolean podeConsultar(Usuario usuario) {
        throw new UnsupportedOperationException("RED: ControleAcesso.podeConsultar não implementado");
    }

    public boolean podeBaixarEncomenda(Usuario usuario) {
        throw new UnsupportedOperationException("RED: ControleAcesso.podeBaixarEncomenda não implementado");
    }

    public void exigirAutorizacao(Usuario usuario, String operacao) {
        throw new UnsupportedOperationException("RED: ControleAcesso.exigirAutorizacao não implementado");
    }
}
