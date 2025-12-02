package domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GeradorDeRelatorios {
    private GerenciadorDeIngressos gi;
    public static class RelatorioFinanceiro {
        public long totalVendas;
        public long totalCancelamentos;
        public double receitaBruta;
        public double receitaLiquida;
    }
    public GeradorDeRelatorios(GerenciadorDeIngressos gi) { this.gi = gi; }

    public RelatorioFinanceiro gerarRelatorioPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        RelatorioFinanceiro r = new RelatorioFinanceiro();
        List<Ingresso> list = gi.listarTodos();
        for (Ingresso i : list) {
            if (i.getVendidoEm() != null && !i.getVendidoEm().isBefore(inicio) && !i.getVendidoEm().isAfter(fim)) {
                r.totalVendas++; r.receitaBruta += i.getValorPago();
            }
            if (i.isCancelado() && i.getCanceladoEm()!=null && !i.getCanceladoEm().isBefore(inicio) && !i.getCanceladoEm().isAfter(fim)) {
                r.totalCancelamentos++; r.receitaLiquida -= i.getValorPago();
            }
        }
        r.receitaLiquida += r.receitaBruta;
        return r;
    }

    public Map<String, Map<String, Long>> filmesMaisAssistidosPorFaixaEtaria() {
        Map<String, Map<String, Long>> out = new LinkedHashMap<>();
        String[] faixas = {"0-12","13-17","18-29","30-49","50+"};
        for (String f: faixas) out.put(f, new LinkedHashMap<>());

        for (Ingresso i : gi.listarTodos()) {
            if (i.isCancelado()) continue;
            Cliente c = i.getCliente();
            if (c==null) continue;
            int idade = c.getIdade();
            String faixa = idade<=12 ? "0-12" : (idade<=17 ? "13-17" : (idade<=29 ? "18-29" : (idade<=49 ? "30-49" : "50+")));
            String titulo = i.getSessao().getFilme().getTitulo();
            out.get(faixa).merge(titulo, 1L, Long::sum);
        }
        return out;
    }

    public void imprimirRelatorioFinanceiro(RelatorioFinanceiro r, LocalDateTime inicio, LocalDateTime fim) {
        System.out.println("Relatório financeiro de " + inicio + " até " + fim);
        System.out.println("Vendas: " + r.totalVendas);
        System.out.println("Cancelamentos: " + r.totalCancelamentos);
        System.out.println("Receita bruta: R$ " + String.format("%.2f", r.receitaBruta));
        System.out.println("Receita líquida: R$ " + String.format("%.2f", r.receitaLiquida));
    }
}

