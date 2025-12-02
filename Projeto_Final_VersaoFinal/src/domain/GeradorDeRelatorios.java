package domain;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerador de relatórios:
 * - relatório financeiro por período (vendas, cancelamentos, receita bruta, receita líquida)
 * - estatísticas de filmes mais assistidos por faixa etária
 */
public class GeradorDeRelatorios {
    private GerenciadorDeIngressos gerenciadorDeIngressos;

    public GeradorDeRelatorios(GerenciadorDeIngressos gi) {
        this.gerenciadorDeIngressos = gi;
    }

    public static class RelatorioFinanceiro {
        public long totalVendas;            // quantidade de vendas efetuadas no período
        public long totalCancelamentos;     // quantidade de cancelamentos no período
        public double receitaBruta;         // soma dos valores vendidos no período (independente de cancelamento)
        public double receitaLiquida;       // receita bruta - valores de ingressos cancelados (assumimos reembolso total)
    }

    /**
     * Gera relatório financeiro para o período [inicio, fim] inclusive.
     */
    public RelatorioFinanceiro gerarRelatorioPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        Collection<Ingresso> todos = gerenciadorDeIngressos.listarTodos();

        long vendas = todos.stream()
                .filter(i -> !i.getVendidoEm().isBefore(inicio) && !i.getVendidoEm().isAfter(fim))
                .count();

        long cancelamentos = todos.stream()
                .filter(i -> i.isCancelado() && i.getCanceladoEm() != null
                        && !i.getCanceladoEm().isBefore(inicio) && !i.getCanceladoEm().isAfter(fim))
                .count();

        double receitaBruta = todos.stream()
                .filter(i -> !i.getVendidoEm().isBefore(inicio) && !i.getVendidoEm().isAfter(fim))
                .mapToDouble(Ingresso::getValorPago).sum();

        // Consideramos que ingressos cancelados que foram vendidos dentro do período geram reembolso (subtrai do bruto)
        double totalReembolsos = todos.stream()
                .filter(i -> i.isCancelado() && i.getCanceladoEm() != null
                        && !i.getCanceladoEm().isBefore(inicio) && !i.getCanceladoEm().isAfter(fim))
                .mapToDouble(Ingresso::getValorPago).sum();

        RelatorioFinanceiro r = new RelatorioFinanceiro();
        r.totalVendas = vendas;
        r.totalCancelamentos = cancelamentos;
        r.receitaBruta = receitaBruta;
        r.receitaLiquida = receitaBruta - totalReembolsos;
        return r;
    }

    /**
     * Retorna mapa: faixaEtaria -> (tituloFilme -> quantidade)
     * Faixas usadas: "0-12", "13-17", "18-29", "30-49", "50+"
     */
    public Map<String, Map<String, Long>> filmesMaisAssistidosPorFaixaEtaria() {
        Collection<Ingresso> todos = gerenciadorDeIngressos.listarTodos();

        // calcular idade do cliente no momento da venda (usa data atual)
        Map<String, Map<String, Long>> resultado = new LinkedHashMap<>();
        List<String> faixas = Arrays.asList("0-12","13-17","18-29","30-49","50+");
        for (String f: faixas) resultado.put(f, new HashMap<>());

        for (Ingresso i : todos) {
            if (i.isCancelado()) continue; // não conta cancelados
            Cliente c = i.getCliente();
            if (c == null || c.getDataNascimento() == null) continue;
            int idade = c.getIdade();
            String faixa = faixaParaIdade(idade);
            String titulo = i.getSessao().getFilme().getTitulo();
            Map<String, Long> mapa = resultado.get(faixa);
            mapa.put(titulo, mapa.getOrDefault(titulo, 0L) + 1L);
        }

        // ordenar internamente por quantidade decrescente (opcional)
        Map<String, Map<String, Long>> ordenado = new LinkedHashMap<>();
        for (String faixa : resultado.keySet()) {
            Map<String, Long> map = resultado.get(faixa);
            Map<String, Long> sorted = map.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1,e2)->e1, LinkedHashMap::new));
            ordenado.put(faixa, sorted);
        }

        return ordenado;
    }

    private String faixaParaIdade(int idade) {
        if (idade <= 12) return "0-12";
        if (idade <= 17) return "13-17";
        if (idade <= 29) return "18-29";
        if (idade <= 49) return "30-49";
        return "50+";
    }

    // método utilitário para imprimir relatório (pode ser usado pelo Main)
    public void imprimirRelatorioFinanceiro(RelatorioFinanceiro r, LocalDateTime inicio, LocalDateTime fim) {
        System.out.println("Relatório financeiro de " + inicio + " até " + fim);
        System.out.println("Vendas: " + r.totalVendas);
        System.out.println("Cancelamentos: " + r.totalCancelamentos);
        System.out.printf("Receita bruta: R$ %.2f%n", r.receitaBruta);
        System.out.printf("Receita líquida: R$ %.2f%n", r.receitaLiquida);
    }
}

