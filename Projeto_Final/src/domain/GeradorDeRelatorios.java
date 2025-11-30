package domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GeradorDeRelatorios {
    private GerenciadorDeIngressos gerenciadorDeIngressos;

    public GeradorDeRelatorios(GerenciadorDeIngressos gi) {
        this.gerenciadorDeIngressos = gi;
    }

    public double receitaTotal() {
        return gerenciadorDeIngressos.listarTodos().stream()
                .filter(i -> !i.isCancelado())
                .mapToDouble(Ingresso::getValorPago).sum();
    }

    public double receitaNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return gerenciadorDeIngressos.listarTodos().stream()
                .filter(i -> !i.isCancelado())
                .filter(i -> !i.getVendidoEm().isBefore(inicio) && !i.getVendidoEm().isAfter(fim))
                .mapToDouble(Ingresso::getValorPago).sum();
    }

    public Map<String, Long> filmesMaisAssistidosPorFaixaEtaria(Collection<Ingresso> ingressos) {
        return ingressos.stream()
                .filter(i -> !i.isCancelado())
                .collect(Collectors.groupingBy(i -> i.getSessao().getFilme().getTitulo(), Collectors.counting()));
    }
}
