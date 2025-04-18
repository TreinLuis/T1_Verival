package com.br.pucrs.verificao.validacao.t1.demo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculadoraTarifaEstacionamentoTest {

    @ParameterizedTest
    @CsvSource({
            "2025-04-18T10:00, 2025-04-18T10:15, false, 0.0",
            "2025-04-18T10:00, 2025-04-18T10:40, false, 9.0",
            "2025-04-18T10:00, 2025-04-18T13:00, false, 20.10",
            "2025-04-18T22:00, 2025-04-19T09:30, false, 50.0",
            "2025-04-18T10:00, 2025-04-18T10:15, true, 0.0",
            "2025-04-18T10:00, 2025-04-18T10:40, true, 4.5",
            "2025-04-18T10:00, 2025-04-18T13:00, true, 10.05",
            "2025-04-18T22:00, 2025-04-19T09:30, true, 25.0"
    })
    void testarCalculoTarifa(LocalDateTime entrada, LocalDateTime saida, boolean isVip, double valorEsperado) {
        double resultado = CalculadoraTarifaEstacionamento.calcularTarifa(entrada, saida, isVip);
        assertEquals(valorEsperado, resultado, 0.01);
    }
}
