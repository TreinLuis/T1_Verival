package com.br.pucrs.verificao.validacao.t1.demo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalculadoraTarifaEstacionamento {
    private static final LocalTime ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(2, 0);
    private static final LocalTime ENTRADA_MAXIMA = LocalTime.of(23, 59);
    private static final LocalTime SAIDA_PROIBIDA_INICIO = LocalTime.of(2, 0);
    private static final LocalTime SAIDA_PROIBIDA_FIM = LocalTime.of(7, 59);
    private static final double TARIFA_FIXA = 9.00;
    private static final double TARIFA_ADICIONAL = 5.55;
    private static final double TARIFA_PERNOITE = 50.00;
    private static final long MINUTOS_CORTESIA = 20;

    public static double calcularTarifa(LocalDateTime entrada, LocalDateTime saida, boolean isVip) {
        if (!entradaValida(entrada)) {
            throw new IllegalArgumentException("Horário de entrada inválido.");
        }

        if (!saidaValida(entrada, saida)) {
            throw new IllegalArgumentException("Horário de saída inválido.");
        }

        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("A saída não pode ocorrer antes da entrada.");
        }

        Duration duracao = Duration.between(entrada, saida);
        long minutos = duracao.toMinutes();
        long horas = duracao.toHours();

        if (minutos <= MINUTOS_CORTESIA) {
            return 0.0;
        }

        if (saida.toLocalTime().isAfter(ABERTURA) && saida.toLocalDate().isAfter(entrada.toLocalDate())) {
            return isVip ? TARIFA_PERNOITE / 2 : TARIFA_PERNOITE;
        }

        if (horas <= 1) {
            return isVip ? TARIFA_FIXA / 2 : TARIFA_FIXA;
        }

        double tarifa = TARIFA_FIXA + (horas - 1) * TARIFA_ADICIONAL;
        return isVip ? tarifa / 2 : tarifa;

    }

    private static boolean entradaValida(LocalDateTime entrada) {
        LocalTime horaEntrada = entrada.toLocalTime();
        return !horaEntrada.isBefore(ABERTURA) && !horaEntrada.isAfter(ENTRADA_MAXIMA);
    }

    private static boolean saidaValida(LocalDateTime entrada, LocalDateTime saida) {
        LocalTime horaSaida = saida.toLocalTime();

        if (saida.toLocalDate().isEqual(entrada.toLocalDate())) {
            return !(horaSaida.isAfter(SAIDA_PROIBIDA_INICIO) && horaSaida.isBefore(SAIDA_PROIBIDA_FIM));
        }

        if (saida.toLocalDate().isAfter(entrada.toLocalDate())) {
            return !(horaSaida.isAfter(SAIDA_PROIBIDA_INICIO) && horaSaida.isBefore(SAIDA_PROIBIDA_FIM));
        }

        return true;
    }
}

