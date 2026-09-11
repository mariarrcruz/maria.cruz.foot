package com.mariacruz.foot.pdv;

import java.math.BigDecimal;

/**
 * Fonte central de preços (RN-01 / RNF-01 / RNF-13).
 * Assinatura mínima para fase RED — sem lógica de negócio.
 */
public class CatalogoPrecos {

    public BigDecimal precoDe(GrupoProduto grupo) {
        throw new UnsupportedOperationException("RED: CatalogoPrecos.precoDe não implementado");
    }

    public BigDecimal totalDe(Pedido pedido) {
        throw new UnsupportedOperationException("RED: CatalogoPrecos.totalDe não implementado");
    }

    public BigDecimal totalRevisadoDe(Pedido pedido) {
        throw new UnsupportedOperationException("RED: CatalogoPrecos.totalRevisadoDe não implementado");
    }
}
