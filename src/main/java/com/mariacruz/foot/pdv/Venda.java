package com.mariacruz.foot.pdv;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Venda(
        String id,
        List<ItemPedido> itens,
        BigDecimal totalRevisado,
        BigDecimal totalRegistrado,
        FormaPagamento formaPagamento,
        LocalDateTime dataHora,
        boolean confirmada,
        boolean comprovanteEmitido) {
}
