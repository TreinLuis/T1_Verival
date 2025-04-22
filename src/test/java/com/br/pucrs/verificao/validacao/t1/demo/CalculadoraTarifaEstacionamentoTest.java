package com.br.pucrs.verificao.validacao.t1.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraTarifaEstacionamentoTest {

    // Teste de tempo de permanência
    @ParameterizedTest(name = "Teste de tempo de permanência: {0}")
    @MethodSource("tempoPermanenciaProvider")
    void testeTempoPermanencia(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip, double valorEsperado) {
        assertEquals(valorEsperado, CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip), 0.01,
                "Falha no cálculo da tarifa para: " + descricao);
    }

    static Stream<Arguments> tempoPermanenciaProvider() {
        return Stream.of(
                // TP1: Permanência dentro do período de cortesia (exatamente 20 minutos)
                Arguments.of("Exatamente 20 minutos (limite cortesia)",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 10, 20),
                        false, 0.0),

                // TP2: Permanência dentro do período de cortesia (menos de 20 minutos)
                Arguments.of("Menos de 20 minutos",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 10, 15),
                        false, 0.0),

                // TP3: Permanência logo após período de cortesia (21 minutos)
                Arguments.of("21 minutos (limite+1)",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 10, 21),
                        false, 9.0),

                // TP4: Permanência de exatamente 1 hora
                Arguments.of("Exatamente 1 hora",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 11, 0),
                        false, 9.0),

                // TP5: Permanência logo após 1 hora (1 hora e 1 minuto)
                Arguments.of("1 hora e 1 minuto",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 11, 1),
                        false, 14.55),

                // TP6: Permanência de 2 horas exatas
                Arguments.of("2 horas exatas",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 12, 0),
                        false, 14.55),

                // TP7: Permanência de 3 horas e meia
                Arguments.of("3 horas e 30 minutos",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 13, 30),
                        false, 25.65)
        );
    }

    // Teste de cliente VIP
    @ParameterizedTest(name = "Teste de cliente VIP: {0}")
    @MethodSource("clienteVipProvider")
    void testeClienteVip(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip, double valorEsperado) {
        assertEquals(valorEsperado, CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip), 0.01,
                "Falha no cálculo da tarifa para cliente VIP: " + descricao);
    }

    static Stream<Arguments> clienteVipProvider() {
        return Stream.of(
                // CV1: Cliente VIP com permanência dentro do período de cortesia
                Arguments.of("VIP em período de cortesia",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 10, 20),
                        true, 0.0),

                // CV2: Cliente VIP com permanência de exatamente 1 hora
                Arguments.of("VIP por 1 hora",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 11, 0),
                        true, 4.50),

                // CV3: Cliente VIP com permanência de 2 horas
                Arguments.of("VIP por 2 horas",
                        LocalDateTime.of(2025, 4, 21, 10, 0),
                        LocalDateTime.of(2025, 4, 21, 12, 0),
                        true, 7.28),

                // CV4: Cliente VIP com pernoite
                Arguments.of("VIP com pernoite",
                        LocalDateTime.of(2025, 4, 21, 22, 0),
                        LocalDateTime.of(2025, 4, 22, 9, 0),
                        true, 25.0)
        );
    }

    // Teste de pernoite
    @ParameterizedTest(name = "Teste de pernoite: {0}")
    @MethodSource("pernoiteProvider")
    void testePernoite(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip, double valorEsperado) {
        assertEquals(valorEsperado, CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip), 0.01,
                "Falha no cálculo da tarifa para pernoite: " + descricao);
    }

    static Stream<Arguments> pernoiteProvider() {
        return Stream.of(
                // PN1: Pernoite com saída logo após 8:00 do dia seguinte
                Arguments.of("Pernoite com saída às 8:01",
                        LocalDateTime.of(2025, 4, 21, 23, 0),
                        LocalDateTime.of(2025, 4, 22, 8, 1),
                        false, 50.0),

//                // PN2: Permanência durante a noite com saída às 8:00 exatas
//                Arguments.of("Saída às 8:00 exatas (não pernoite)",
//                        LocalDateTime.of(2025, 4, 21, 23, 0),
//                        LocalDateTime.of(2025, 4, 22, 8, 0),
//                        false, 14.55),

                // PN3: Permanência longa durante o mesmo dia
                Arguments.of("Longa permanência no mesmo dia",
                        LocalDateTime.of(2025, 4, 21, 8, 0),
                        LocalDateTime.of(2025, 4, 21, 23, 59),
                        false, 92.25),

                // PN4: Permanência entre dias com saída antes das 8:00
                Arguments.of("Saída antes das 8:00 do dia seguinte",
                        LocalDateTime.of(2025, 4, 21, 23, 0),
                        LocalDateTime.of(2025, 4, 22, 1, 59),
                        false, 20.1)
        );
    }

    // Teste de horários limite
    @ParameterizedTest(name = "Teste de horários limite: {0}")
    @MethodSource("horariosLimiteProvider")
    void testeHorariosLimite(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip, double valorEsperado) {
        assertEquals(valorEsperado, CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip), 0.01,
                "Falha no cálculo da tarifa para horários limite: " + descricao);
    }

    static Stream<Arguments> horariosLimiteProvider() {
        return Stream.of(
                // HE1: Entrada no horário de abertura
                Arguments.of("Entrada no horário de abertura",
                        LocalDateTime.of(2025, 4, 21, 8, 0),
                        LocalDateTime.of(2025, 4, 21, 9, 0),
                        false, 9.0),

                // HE2: Entrada no horário máximo permitido
                Arguments.of("Entrada no horário máximo permitido",
                        LocalDateTime.of(2025, 4, 21, 23, 59),
                        LocalDateTime.of(2025, 4, 22, 0, 59),
                        false, 9.0),

//                // HS1: Saída no horário limite permitido
//                Arguments.of("Saída no horário limite permitido",
//                        LocalDateTime.of(2025, 4, 21, 1, 0),
//                        LocalDateTime.of(2025, 4, 21, 2, 0),
//                        false,9.0),

                // HS2: Saída no horário de abertura
                Arguments.of("Saída no horário de abertura",
                        LocalDateTime.of(2025, 4, 21, 20, 0),
                        LocalDateTime.of(2025, 4, 22, 8, 0),
                        false, 70.05)
        );
    }
    @ParameterizedTest(name = "Teste de exceção para saída inválida: {0}")
    @MethodSource("casosComSaidaInvalida")
    void testeSaidaInvalida(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip) {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip);
        }, "Era esperada uma IllegalArgumentException para o caso: " + descricao);
    }

    static Stream<Arguments> casosComSaidaInvalida() {
        return Stream.of(
                Arguments.of("Saída no horário proibido (2h01)",
                        LocalDateTime.of(2025, 4, 21, 1, 0),
                        LocalDateTime.of(2025, 4, 21, 2, 1),
                        false)
        );
    }

    // Testes de exceção
    @Test
    void testeEntradaAntesDoHorarioDeAbertura() {
        LocalDateTime entrada = LocalDateTime.of(2025, 4, 21, 7, 59);
        LocalDateTime saida = LocalDateTime.of(2025, 4, 21, 9, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                        CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, false),
                "Deveria lançar exceção para entrada antes do horário de abertura");

        assertTrue(exception.getMessage().contains("Horário de entrada inválido."));
    }

    @Test
    void testeEntradaAposHorarioMaximoPermitido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 4, 22, 0, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 4, 22, 1, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                        CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, false),
                "Deveria lançar exceção para entrada após horário máximo permitido");

        assertTrue(exception.getMessage().contains("Horário de entrada inválido."));
    }

    @Test
    void testeSaidaEmHorarioProibido() {
        LocalDateTime entrada = LocalDateTime.of(2025, 4, 21, 20, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 4, 22, 3, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                        CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, false),
                "Deveria lançar exceção para saída em horário proibido");

        assertTrue(exception.getMessage().contains("Horário de saída inválido."));
    }

    @Test
    void testeSaidaAntesEntrada() {
        LocalDateTime entrada = LocalDateTime.of(2025, 4, 21, 10, 0);
        LocalDateTime saida = LocalDateTime.of(2025, 4, 21, 9, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                        CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, false),
                "Deveria lançar exceção para saída antes da entrada");

        assertTrue(exception.getMessage().contains("A saída não pode ocorrer antes da entrada."));
    }

    @ParameterizedTest(name = "Teste de entrada inválida: {0}")
    @MethodSource("casosComEntradaInvalida")
    void testeEntradaInvalida(String descricao, LocalDateTime entrada, LocalDateTime saida, boolean isVip) {
        assertThrows(IllegalArgumentException.class, () ->
                        CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip),
                "Esperava exceção para entrada inválida: " + descricao);
    }

    static Stream<Arguments> casosComEntradaInvalida() {
        return Stream.of(
                Arguments.of("Entrada antes do horário de abertura",
                        LocalDateTime.of(2025, 4, 21, 7, 59),
                        LocalDateTime.of(2025, 4, 21, 9, 0),
                        false),

                Arguments.of("Entrada durante período de fechamento (03:00)",
                        LocalDateTime.of(2025, 4, 21, 3, 0),
                        LocalDateTime.of(2025, 4, 21, 4, 0),
                        false),

                Arguments.of("Entrada logo após o horário de fechamento (02:01)",
                        LocalDateTime.of(2025, 4, 21, 2, 1),
                        LocalDateTime.of(2025, 4, 21, 9, 0),
                        false)
        );
    }


}