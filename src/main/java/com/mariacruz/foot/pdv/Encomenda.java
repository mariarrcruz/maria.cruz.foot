package com.mariacruz.foot.pdv;

import java.time.LocalDateTime;
import java.util.List;

public record Encomenda(
        String qrCode,
        List<ItemPedido> itens,
        boolean paga,
        boolean conferida,
        boolean entregue,
        boolean realizada,
        String id,
        LocalDateTime dataHoraAlteracao) {
}
