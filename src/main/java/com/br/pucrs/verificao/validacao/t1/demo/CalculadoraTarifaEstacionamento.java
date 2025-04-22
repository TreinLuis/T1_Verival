package com.br.pucrs.verificao.validacao.t1.demo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalculadoraTarifaEstacionamento {
    private static final LocalTime ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(2, 0); // 2:00 do dia seguinte
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

        if (!saidaValida(saida)) {
            throw new IllegalArgumentException("Horário de saída inválido.");
        }

        if (saida.isBefore(entrada)) {
            throw new IllegalArgumentException("A saída não pode ocorrer antes da entrada.");
        }

        // Verificar se é pernoite (saída após 8:00 do dia seguinte à entrada)
        boolean isPernoite = saida.toLocalDate().isAfter(entrada.toLocalDate()) &&
                saida.toLocalTime().isAfter(ABERTURA);

        if (isPernoite) {
            return aplicarDescontoVip(TARIFA_PERNOITE, isVip);
        }

        Duration duracao = Duration.between(entrada, saida);
        long minutos = duracao.toMinutes();

        // Período de cortesia
        if (minutos <= MINUTOS_CORTESIA) {
            return 0.0;
        }

        // Cálculo de tarifa normal
        double tarifa;

        // Arredonda para cima para considerar hora parcial
        long horas = minutos / 60;
        long minutosAdicionais = minutos % 60;

        if (minutosAdicionais > 0) {
            horas++; // Incrementa hora se houver minutos adicionais
        }

        if (horas <= 1) {
            tarifa = TARIFA_FIXA;
        } else {
            tarifa = TARIFA_FIXA + (horas - 1) * TARIFA_ADICIONAL;
        }

        return aplicarDescontoVip(tarifa, isVip);
    }

    private static double aplicarDescontoVip(double tarifa, boolean isVip) {
        return isVip ? tarifa / 2 : tarifa;
    }

    private static boolean entradaValida(LocalDateTime entrada) {
        LocalTime horaEntrada = entrada.toLocalTime();
        return !horaEntrada.isBefore(ABERTURA) && !horaEntrada.isAfter(ENTRADA_MAXIMA);
    }

    private static boolean saidaValida(LocalDateTime saida) {
        LocalTime horaSaida = saida.toLocalTime();
        return !(horaSaida.isAfter(SAIDA_PROIBIDA_INICIO.minusMinutes(1)) &&
                horaSaida.isBefore(ABERTURA));
    }
}