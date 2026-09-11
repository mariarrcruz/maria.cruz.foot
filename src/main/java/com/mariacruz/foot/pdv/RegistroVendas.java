package com.mariacruz.foot.pdv;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Registro de vendas (RN-04 / RNF-02 / RNF-09 / RNF-10).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class RegistroVendas {

    public Venda registrar(Pedido pedido, BigDecimal totalRevisado, FormaPagamento forma) {
        throw new UnsupportedOperationException("RED: RegistroVendas.registrar não implementado");
    }

    public Optional<Venda> buscarPorId(String id) {
        throw new UnsupportedOperationException("RED: RegistroVendas.buscarPorId não implementado");
    }

    public List<Venda> consultarTodas() {
        throw new UnsupportedOperationException("RED: RegistroVendas.consultarTodas não implementado");
    }

    public BigDecimal totalDoDia() {
        throw new UnsupportedOperationException("RED: RegistroVendas.totalDoDia não implementado");
    }

    public int quantidadePorProduto(GrupoProduto grupo) {
        throw new UnsupportedOperationException("RED: RegistroVendas.quantidadePorProduto não implementado");
    }

    public List<Venda> vendasPorForma(FormaPagamento forma) {
        throw new UnsupportedOperationException("RED: RegistroVendas.vendasPorForma não implementado");
    }
}
