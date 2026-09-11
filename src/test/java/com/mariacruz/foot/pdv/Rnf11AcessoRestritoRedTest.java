package com.mariacruz.foot.pdv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED — RNF-11 Acesso restrito (ASR-08, C-06).
 */
class Rnf11AcessoRestritoRedTest {

    private final ControleAcesso acesso = new ControleAcesso();

    @Test
    void rnf11_feliz_usuarioAutorizadoRegistraConsultaEBaixa() {
        // Arrange: usuário autorizado
        Usuario carla = new Usuario("Carla", true);
        // Act
        boolean podeRegistrar = acesso.podeRegistrar(carla);
        boolean podeConsultar = acesso.podeConsultar(carla);
        boolean podeBaixar = acesso.podeBaixarEncomenda(carla);
        // Assert
        assertTrue(podeRegistrar);
        assertTrue(podeConsultar);
        assertTrue(podeBaixar);
    }

    @Test
    void rnf11_limite_cadaOperacaoProtegidaValidaAutorizacao() {
        // Arrange: autorizado e não autorizado
        Usuario autorizado = new Usuario("Carla", true);
        Usuario anonimo = new Usuario("anonimo", false);
        // Act + Assert: cada operação distingue autorizado de não autorizado
        assertTrue(acesso.podeRegistrar(autorizado));
        assertFalse(acesso.podeRegistrar(anonimo));
        assertTrue(acesso.podeConsultar(autorizado));
        assertFalse(acesso.podeConsultar(anonimo));
        assertTrue(acesso.podeBaixarEncomenda(autorizado));
        assertFalse(acesso.podeBaixarEncomenda(anonimo));
    }

    @Test
    void rnf11_invalida_usuarioNaoAutorizadoTemAcessoNegado() {
        // Arrange
        Usuario invasor = new Usuario("invasor", false);
        // Act
        boolean podeRegistrar = acesso.podeRegistrar(invasor);
        // Assert: acesso negado
        assertFalse(podeRegistrar);
    }

    @ParameterizedTest
    @ValueSource(strings = {"registrar", "consultar", "baixar"})
    void rnf11_estadoProibido_usuarioNaoAutorizadoNaoOpera(String operacao) {
        // Arrange
        Usuario naoAutorizado = new Usuario("invasor", false);
        // Act + Assert: qualquer operação protegida nega acesso
        assertThrows(SecurityException.class,
                () -> acesso.exigirAutorizacao(naoAutorizado, operacao));
    }
}
